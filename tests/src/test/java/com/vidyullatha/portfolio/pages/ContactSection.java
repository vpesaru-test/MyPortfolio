package com.vidyullatha.portfolio.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** "Let's Connect": heading and contact buttons. */
public class ContactSection {

    private final Page page;

    ContactSection(Page page) {
        this.page = page;
    }

    public Locator heading() {
        return page.locator("#contact h2");
    }

    public Locator links() {
        return page.locator("#contact .contact-link");
    }

    public Locator phoneLink() {
        return page.locator("#contact a[href^='tel:']");
    }

    public Locator emailLink() {
        return page.locator("#contact a[href^='mailto:']");
    }

    public Locator linkTo(String href) {
        return page.locator("#contact a[href='" + href + "']");
    }
}
