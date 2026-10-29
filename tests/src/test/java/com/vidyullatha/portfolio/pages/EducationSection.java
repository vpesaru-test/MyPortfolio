package com.vidyullatha.portfolio.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Education cards and the certifications grid. */
public class EducationSection {

    private final Page page;

    EducationSection(Page page) {
        this.page = page;
    }

    public Locator degreeCards() {
        return page.locator("#education .edu-card");
    }

    public Locator degree(int index) {
        return degreeCards().nth(index).locator(".edu-degree");
    }

    public Locator school(int index) {
        return degreeCards().nth(index).locator(".edu-school");
    }

    public Locator highlights(int index) {
        return degreeCards().nth(index).locator(".edu-highlights .pill");
    }

    public Locator certCards() {
        return page.locator("#education .cert-card");
    }

    public Locator certCard(int index) {
        return certCards().nth(index);
    }

    public Locator certName(int index) {
        return certCard(index).locator(".cert-name");
    }

    public Locator certMeta(int index) {
        return certCard(index).locator(".cert-meta");
    }

    public Locator certViewCue(int index) {
        return certCard(index).locator(".cert-view");
    }

    public Locator linkedCertCards() {
        return page.locator("#education a.cert-card");
    }
}
