package com.vidyullatha.portfolio.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Skills: the core tool tiles and the grouped skill cards. */
public class SkillsSection {

    private final Page page;

    SkillsSection(Page page) {
        this.page = page;
    }

    public Locator coreToolNames() {
        return page.locator("#skills .core-tile .core-name");
    }

    public Locator groups() {
        return page.locator("#skills .skill-group");
    }

    public Locator groupTitle(int index) {
        return groups().nth(index).locator(".skill-group-title");
    }

    public Locator groupItems(int index) {
        return groups().nth(index).locator(".pill");
    }
}
