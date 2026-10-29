package com.vidyullatha.portfolio.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** The hero: greeting, headline, summary, call-to-action buttons, badges and photo. */
public class HeroSection {

    private final Page page;

    HeroSection(Page page) {
        this.page = page;
    }

    public Locator root() {
        return page.locator("section.hero");
    }

    public Locator heading() {
        return page.locator("section.hero h1");
    }

    public Locator headline() {
        return page.locator(".hero-headline");
    }

    public Locator role() {
        return page.locator(".hero-role");
    }

    public Locator summary() {
        return page.locator(".hero-desc");
    }

    public Locator statusPill() {
        return page.locator(".status-pill");
    }

    public Locator ctaButtons() {
        return page.locator(".hero-ctas a");
    }

    public Locator badges() {
        return page.locator(".hero-badges .pill");
    }

    public Locator photo() {
        return page.locator(".hero-photo img");
    }

    public Locator photoWrapper() {
        return page.locator(".hero-photo");
    }
}
