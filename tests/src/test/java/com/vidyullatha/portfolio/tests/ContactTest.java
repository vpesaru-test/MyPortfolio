package com.vidyullatha.portfolio.tests;

import com.vidyullatha.portfolio.base.BaseTest;
import com.vidyullatha.portfolio.data.SiteData;
import com.vidyullatha.portfolio.support.Text;
import org.testng.annotations.Test;

import java.time.Year;
import java.util.Map;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** Contact section and footer: every way to reach out works and matches the profile. */
public class ContactTest extends BaseTest {

    private final Map<String, Object> contact = SiteData.nested(SiteData.profile(), "contact");

    @Test(description = "Phone button dials the profile's number")
    public void phoneLinkMatchesProfile() {
        if (!SiteData.isSet(contact, "phone")) {
            assertThat(portfolio.contact().phoneLink()).hasCount(0);
            return;
        }
        String digits = Text.digitsOnly(SiteData.str(contact, "phone"));
        assertThat(portfolio.contact().phoneLink()).hasAttribute("href", "tel:" + digits);
        assertThat(portfolio.contact().phoneLink()).containsText(SiteData.str(contact, "phone"));
    }

    @Test(description = "Email button appears only when an email is set, and opens a new message")
    public void emailLinkFollowsProfile() {
        if (SiteData.isSet(contact, "email")) {
            assertThat(portfolio.contact().emailLink())
                    .hasAttribute("href", "mailto:" + SiteData.str(contact, "email"));
        } else {
            assertThat(page.locator("a[href^='mailto:']")).hasCount(0);
        }
    }

    @Test(description = "LinkedIn and GitHub buttons match the profile and open in a new tab")
    public void socialLinksMatchProfile() {
        for (String key : new String[] {"linkedin", "github"}) {
            if (!SiteData.isSet(contact, key)) {
                continue;
            }
            var link = portfolio.contact().linkTo(SiteData.str(contact, key));
            assertThat(link).hasCount(1);
            assertThat(link).hasAttribute("target", "_blank");
            assertThat(link).hasAttribute("rel", Pattern.compile("\\bnoopener\\b"));
        }
    }

    @Test(description = "Contact heading matches the profile")
    public void contactHeadingMatchesProfile() {
        String heading = SiteData.isSet(contact, "heading") ? SiteData.str(contact, "heading") : "Let's Connect";
        assertThat(portfolio.contact().heading()).hasText(heading);
    }

    @Test(description = "Footer shows the current year and the name")
    public void footerIsCurrent() {
        assertThat(portfolio.footerYear()).hasText(String.valueOf(Year.now().getValue()));
        assertThat(portfolio.footer()).containsText(SiteData.str(SiteData.profile(), "name"));
    }
}
