package com.vidyullatha.portfolio.tests;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Response;
import com.vidyullatha.portfolio.base.BaseTest;
import com.vidyullatha.portfolio.config.Config;
import com.vidyullatha.portfolio.data.SiteData;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/** Does the page load, with its styles, script and photo, and without errors? */
public class SmokeTest extends BaseTest {

    @Test(description = "Home page responds 200 and the title starts with the profile name")
    public void homePageLoads() {
        Response response = portfolio.open();
        assertNotNull(response, "No response for " + Config.baseUrl());
        assertEquals(response.status(), 200, "HTTP status of " + Config.baseUrl());

        String name = SiteData.str(SiteData.profile(), "name");
        assertThat(page).hasTitle(Pattern.compile("^" + Pattern.quote(name) + " — "));
    }

    @Test(description = "Stylesheet and script are served")
    public void stylesheetAndScriptAreServed() {
        for (String asset : List.of("assets/css/style.css", "assets/js/main.js")) {
            APIResponse response = page.request().get(Config.baseUrl() + asset);
            assertEquals(response.status(), 200, asset);
        }
        // main.js marks the page as enhanced; this proves the script ran, not just loaded
        assertThat(page.locator("html")).hasClass(Pattern.compile("\\bjs\\b"));
    }

    @Test(description = "Profile photo loads (not a broken image)")
    public void profilePhotoLoads() {
        assertThat(portfolio.hero().photo()).isVisible();
        Number naturalWidth = (Number) portfolio.hero().photo().evaluate("img => img.naturalWidth");
        assertTrue(naturalWidth.intValue() > 0, "Profile photo has no pixels, so it failed to load");
    }

    @Test(description = "No JavaScript errors and no broken files from this site while loading and scrolling")
    public void noScriptErrorsOrBrokenLocalFiles() {
        List<String> problems = new ArrayList<>();
        page.onPageError(error -> problems.add("JavaScript error: " + error));
        page.onResponse(response -> {
            boolean ours = response.url().startsWith(Config.baseUrl());
            boolean favicon = response.url().endsWith("/favicon.ico"); // requested by some browsers automatically
            if (ours && !favicon && response.status() >= 400) {
                problems.add(response.status() + " " + response.url());
            }
        });

        page.reload();
        portfolio.scrollTo(100_000);
        portfolio.scrollTo(0);
        page.waitForTimeout(300);

        assertTrue(problems.isEmpty(), "Problems found:\n" + String.join("\n", problems));
    }

    @Test(description = "Page declares its language and a meta description for search engines")
    public void languageAndDescriptionAreSet() {
        assertThat(page.locator("html")).hasAttribute("lang", "en");
        String description = page.locator("meta[name='description']").getAttribute("content");
        assertNotNull(description, "Missing <meta name=\"description\">");
        assertTrue(description.length() > 50, "Meta description is too short: " + description);
    }
}
