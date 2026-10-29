package com.vidyullatha.portfolio.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Experience: one card per role. */
public class ExperienceSection {

    private final Page page;

    ExperienceSection(Page page) {
        this.page = page;
    }

    public Locator cards() {
        return page.locator("#experience .exp-card");
    }

    public Locator role(int index) {
        return cards().nth(index).locator(".exp-heading h3");
    }

    public Locator company(int index) {
        return cards().nth(index).locator(".exp-company");
    }

    public Locator period(int index) {
        return cards().nth(index).locator(".exp-period");
    }

    public Locator description(int index) {
        return cards().nth(index).locator(".exp-desc");
    }

    public Locator currentBadge(int index) {
        return cards().nth(index).locator(".pill-xs");
    }

    public Locator tags(int index) {
        return cards().nth(index).locator(".pill-row .pill");
    }
}
