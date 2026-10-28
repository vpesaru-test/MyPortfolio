// Small progressive enhancements. The page works without any of this.
(function () {
  var root = document.documentElement;
  root.classList.add('js');

  // ── Light / dark toggle ────────────────────────────────────
  function currentTheme() {
    var set = root.getAttribute('data-theme');
    if (set) return set;
    return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }
  var themeBtn = document.querySelector('.theme-toggle');
  if (themeBtn) {
    themeBtn.addEventListener('click', function () {
      var next = currentTheme() === 'dark' ? 'light' : 'dark';
      root.setAttribute('data-theme', next);
      try { localStorage.setItem('theme', next); } catch (e) {}
    });
  }

  // ── Mobile menu ────────────────────────────────────────────
  var menuBtn = document.querySelector('.menu-toggle');
  var links = document.getElementById('nav-links');
  if (menuBtn && links) {
    function setOpen(open) {
      links.classList.toggle('is-open', open);
      menuBtn.setAttribute('aria-expanded', open ? 'true' : 'false');
      menuBtn.textContent = open ? '✕' : '☰';
    }
    menuBtn.addEventListener('click', function () { setOpen(!links.classList.contains('is-open')); });
    links.addEventListener('click', function (e) { if (e.target.tagName === 'A') setOpen(false); });
  }

  // ── Fade cards in as they scroll into view ─────────────────
  var reveals = document.querySelectorAll('.reveal');
  if ('IntersectionObserver' in window) {
    var revealObs = new IntersectionObserver(function (entries) {
      entries.forEach(function (e) {
        if (e.isIntersecting) { e.target.classList.add('is-visible'); revealObs.unobserve(e.target); }
      });
    }, { threshold: 0.1 });
    reveals.forEach(function (el) { revealObs.observe(el); });
  } else {
    reveals.forEach(function (el) { el.classList.add('is-visible'); });
  }

  // ── Highlight the nav link for the section in view ─────────
  var navLinks = document.querySelectorAll('.nav-links a[href^="#"]');
  if ('IntersectionObserver' in window && navLinks.length) {
    var byId = {};
    navLinks.forEach(function (a) { byId[a.getAttribute('href').slice(1)] = a; });
    var sectionObs = new IntersectionObserver(function (entries) {
      entries.forEach(function (e) {
        if (e.isIntersecting && byId[e.target.id]) {
          navLinks.forEach(function (a) { a.classList.remove('is-active'); });
          byId[e.target.id].classList.add('is-active');
        }
      });
    }, { rootMargin: '-45% 0px -50% 0px' });
    Object.keys(byId).forEach(function (id) {
      var s = document.getElementById(id);
      if (s) sectionObs.observe(s);
    });
  }

  // ── Project filters ────────────────────────────────────────
  var chips = document.querySelectorAll('.filter-chip');
  var cards = document.querySelectorAll('.project-card');
  chips.forEach(function (chip) {
    chip.addEventListener('click', function () {
      var f = chip.getAttribute('data-filter');
      chips.forEach(function (c) {
        var on = c === chip;
        c.classList.toggle('is-active', on);
        c.setAttribute('aria-pressed', on ? 'true' : 'false');
      });
      cards.forEach(function (card) {
        card.hidden = !(f === 'all' || card.getAttribute('data-category') === f);
      });
    });
  });

  // ── Back to top ────────────────────────────────────────────
  var toTop = document.querySelector('.to-top');
  if (toTop) {
    window.addEventListener('scroll', function () {
      toTop.classList.toggle('is-visible', window.scrollY > 700);
    }, { passive: true });
    toTop.addEventListener('click', function () { window.scrollTo({ top: 0 }); });
  }

  // Keep the footer year current between rebuilds
  var year = document.querySelector('.js-year');
  if (year) year.textContent = new Date().getFullYear();
})();
