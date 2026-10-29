package com.vidyullatha.portfolio.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.vidyullatha.portfolio.config.Config;

import java.util.Map;

/**
 * Page object for the single-page portfolio. Each section of the page is its
 * own component so tests read as "portfolio.nav().links()",
 * "portfolio.experience().cards()" and so on.
 */
public class PortfolioPage {

    private final Page page;
    private final NavBar nav;
    private final HeroSection hero;
    private final SkillsSection skills;
    private final ExperienceSection experience;
    private final EducationSection education;
    private final ContactSection contact;

    public PortfolioPage(Page page) {
        this.page = page;
        this.nav = new NavBar(page);
        this.hero = new HeroSection(page);
        this.skills = new SkillsSection(page);
        this.experience = new ExperienceSection(page);
        this.education = new EducationSection(page);
        this.contact = new ContactSection(page);
    }

    public Response open() {
        return page.navigate(Config.baseUrl());
    }

    public Page page() {
        return page;
    }

    public NavBar nav() {
        return nav;
    }

    public HeroSection hero() {
        return hero;
    }

    public SkillsSection skills() {
        return skills;
    }

    public ExperienceSection experience() {
        return experience;
    }

    public EducationSection education() {
        return education;
    }

    public ContactSection contact() {
        return contact;
    }

    public Locator section(String id) {
        return page.locator("#" + id);
    }

    public Locator statItems() {
        return page.locator(".stats-band .stat");
    }

    public Locator aboutPointTitles() {
        return page.locator("#about .point-card h3");
    }

    public Locator projectCards() {
        return page.locator("#projects .project-card");
    }

    public Locator numberedEyebrows() {
        return page.locator(".section-head .eyebrow, .about-kicker .eyebrow");
    }

    public Locator backToTop() {
        return page.locator(".to-top");
    }

    public Locator footer() {
        return page.locator("footer.footer");
    }

    public Locator footerYear() {
        return page.locator("footer .js-year");
    }

    public void scrollTo(int y) {
        page.evaluate("y => window.scrollTo(0, y)", y);
    }

    public void scrollIntoView(String sectionId) {
        page.evaluate("id => document.getElementById(id).scrollIntoView()", sectionId);
    }

    public int scrollY() {
        return ((Number) page.evaluate("() => window.scrollY")).intValue();
    }

    public void waitForScrollNear(int y, int tolerance) {
        page.waitForFunction("a => Math.abs(window.scrollY - a.y) <= a.t", Map.of("y", y, "t", tolerance));
    }

    /** True when the page is wider than the viewport, i.e. it scrolls sideways. */
    public boolean hasHorizontalOverflow() {
        return (Boolean) page.evaluate(
                "() => document.documentElement.scrollWidth > document.documentElement.clientWidth");
    }

    /** Sum of the R, G and B values of the page background: high for light, low for dark. */
    public void waitForBackground(boolean dark) {
        String script = "dark => { const c = getComputedStyle(document.body).backgroundColor.match(/\\d+/g).map(Number);"
                + " const sum = c[0] + c[1] + c[2]; return dark ? sum < 200 : sum > 600; }";
        page.waitForFunction(script, dark);
    }
}
