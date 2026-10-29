# Portfolio UI Tests

End-to-end UI automation for this portfolio site, written in **Java 17+** with
**Playwright** and **TestNG**, built with **Maven**, and run in **GitHub Actions**
on every push across **Chromium, Firefox and WebKit**.

The suite is **data-driven**: it reads the same `_data/*.yml` files that generate
the page and checks the rendered site against them. Update a YAML file and the
expected results update with it.

## What's covered

| Test class | What it checks |
|---|---|
| `SmokeTest` | Page returns 200, title, CSS/JS served, script runs, photo loads, no JS errors or broken local files, `lang` and meta description |
| `NavigationTest` | Nav links match visible sections, no broken in-page anchors, each link scrolls to its section, active-link highlighting, logo and back-to-top return to top, section numbers are sequential |
| `HeroTest` | Greeting, headline, role, summary, badges, status pill, main button target and stats all match `profile.yml`; hero fits above the fold |
| `ContentTest` | About cards, core tools, every skill group, every experience card (role, company, dates, description, tags, "Current" badge), every degree (incl. GPA/honors), and Projects/Resume appearing only when they have data |
| `CertificationsTest` | Card order and details, clickable only when `url`/`file` is set, correct `href`/`target`/`rel`, links use https, **no two certificates share a link**, uploaded files are served as PDF/image, clicking opens a new tab |
| `ContactTest` | Phone `tel:` link, email shown only when set, LinkedIn/GitHub links open safely, contact heading, footer year |
| `ThemeTest` | Follows system light/dark, toggle switches both ways, choice persists after reload |
| `ResponsiveTest` | No sideways scrolling from 360px to 1920px, mobile menu open/close, photo above greeting and two-column stats on phones |
| `AccessibilityTest` | One H1, landmarks, image alt text, labelled icon buttons, no empty links, `rel=noopener` on new-tab links, keyboard Tab order |

## Design

```
tests/
├── pom.xml                          Maven build: Playwright, TestNG, SnakeYAML
├── testng.xml                       Suite definition
└── src/test/java/com/vidyullatha/portfolio/
    ├── config/Config.java           baseUrl, browser, headless, dataDir
    ├── data/SiteData.java           Loads _data/*.yml as test data
    ├── base/BaseTest.java           Browser per class, fresh context per test,
    │                                screenshot on failure
    ├── pages/                       Page Object Model: one class per section
    │   ├── PortfolioPage.java
    │   ├── NavBar.java, HeroSection.java, SkillsSection.java,
    │   └── ExperienceSection.java, EducationSection.java, ContactSection.java
    ├── support/Text.java            Whitespace-normalising text helpers
    └── tests/                       The test classes above
```

- **Page Object Model** keeps selectors in one place; tests read like the page.
- **TestNG DataProviders** generate one test per job, skill group, degree,
  certificate and viewport straight from the YAML.
- **Playwright web-first assertions** (`assertThat(locator)…`) retry until the
  page settles, so there are no sleeps.
- Each test gets a **new browser context** (clean storage, own viewport), with
  **reduced motion** so animations don't make results timing-dependent.
- **Failure screenshots** are saved to `target/screenshots/` and uploaded by CI.

## Running locally

Needs Java 17+ and Maven. First, serve the site (from the repo root):

```bash
bundle exec jekyll serve          # serves http://localhost:4000/
```

Then, from `tests/`:

```bash
# one-time: install a browser
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"

mvn test                                          # Chromium, headless
mvn test -Dbrowser=firefox                        # or webkit
mvn test -Dheadless=false                         # watch it run
mvn test -Dtest=CertificationsTest                # one class
mvn test -DbaseUrl=https://vpesaru-test.github.io/MyPortfolio/   # the live site
```

Reports land in `target/surefire-reports/` (open `index.html`).

## CI

`.github/workflows/ui-tests.yml` runs on every push to `main` and every pull
request: it builds the site with the same Jekyll builder GitHub Pages uses,
serves it on `localhost:4000`, and runs the suite in Chromium, Firefox and WebKit
in parallel. Reports and failure screenshots are attached to each run under
**Artifacts**.
