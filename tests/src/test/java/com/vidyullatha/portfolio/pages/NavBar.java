package com.vidyullatha.portfolio.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.ArrayList;
import java.util.List;

/** The fixed top navigation: logo, section links, resume button, theme toggle and mobile menu. */
public class NavBar {

    private final Page page;

    NavBar(Page page) {
        this.page = page;
    }

    public Locator root() {
        return page.locator("nav.nav");
    }

    public Locator logo() {
        return page.locator(".nav-logo");
    }

    public Locator linkList() {
        return page.locator("#nav-links");
    }

    public Locator links() {
        return page.locator("#nav-links a");
    }

    public Locator link(String sectionId) {
        return page.locator("#nav-links a[href='#" + sectionId + "']");
    }

    /** Section ids the nav links point to, in order (e.g. "about", "skills"). */
    public List<String> linkTargets() {
        List<String> ids = new ArrayList<>();
        for (Locator link : links().all()) {
            String href = link.getAttribute("href");
            if (href != null && href.startsWith("#")) {
                ids.add(href.substring(1));
            }
        }
        return ids;
    }

    public Locator resumeButton() {
        return page.locator(".nav-actions a.btn");
    }

    public Locator themeToggle() {
        return page.locator(".theme-toggle");
    }

    public Locator menuToggle() {
        return page.locator(".menu-toggle");
    }

    public void toggleTheme() {
        themeToggle().click();
    }

    public void openMobileMenu() {
        menuToggle().click();
    }
}
