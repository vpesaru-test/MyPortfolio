package com.vidyullatha.portfolio.tests;

import com.vidyullatha.portfolio.base.BaseTest;
import com.vidyullatha.portfolio.data.SiteData;
import com.vidyullatha.portfolio.support.Text;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Hero and stats band render exactly what _data/profile.yml says. */
public class HeroTest extends BaseTest {

    private final Map<String, Object> profile = SiteData.profile();

    @Test(description = "Greeting uses greeting_name (or name)")
    public void greetingUsesProfileName() {
        String name = SiteData.isSet(profile, "greeting_name")
                ? SiteData.str(profile, "greeting_name")
                : SiteData.str(profile, "name");
        assertEquals(Text.norm(portfolio.hero().heading().textContent()), "Hello, I'm " + name);
    }

    @Test(description = "Headline is assembled from headline.before / highlight / after")
    public void headlineMatchesProfile() {
        Map<String, Object> headline = SiteData.nested(profile, "headline");
        String expected = Text.norm(SiteData.str(headline, "before") + " "
                + SiteData.str(headline, "highlight") + " " + SiteData.str(headline, "after"));
        assertEquals(Text.norm(portfolio.hero().headline().textContent()), expected);
    }

    @Test(description = "Role line and summary match the profile")
    public void roleAndSummaryMatchProfile() {
        assertEquals(Text.norm(portfolio.hero().role().textContent()), SiteData.str(profile, "role"));
        assertEquals(Text.norm(portfolio.hero().summary().textContent()), Text.norm(SiteData.str(profile, "summary")));
    }

    @Test(description = "Badges match the profile, in order")
    public void badgesMatchProfile() {
        assertEquals(Text.norm(portfolio.hero().badges().allTextContents()), SiteData.strList(profile, "badges"));
    }

    @Test(description = "Status pill shows only when status is set")
    public void statusPillFollowsProfile() {
        if (SiteData.isSet(profile, "status")) {
            assertThat(portfolio.hero().statusPill()).hasText(SiteData.str(profile, "status"));
        } else {
            assertThat(portfolio.hero().statusPill()).hasCount(0);
        }
    }

    @Test(description = "Main button goes to Projects when there are projects, otherwise Experience")
    public void primaryButtonTarget() {
        String expected = SiteData.projects().isEmpty() ? "#experience" : "#projects";
        assertThat(portfolio.hero().ctaButtons().first()).hasAttribute("href", expected);
        assertThat(portfolio.hero().ctaButtons().last()).hasAttribute("href", "#contact");
    }

    @Test(description = "Stats band shows each stat's value, suffix and label, in order")
    public void statsMatchProfile() {
        List<Map<String, Object>> stats = SiteData.mapList(profile, "stats");
        assertThat(portfolio.statItems()).hasCount(stats.size());

        List<String> expected = new ArrayList<>();
        for (Map<String, Object> stat : stats) {
            expected.add(SiteData.str(stat, "value") + SiteData.str(stat, "suffix") + " " + SiteData.str(stat, "label"));
        }
        List<String> actual = new ArrayList<>();
        for (int i = 0; i < stats.size(); i++) {
            String num = Text.norm(portfolio.statItems().nth(i).locator(".stat-num").textContent());
            String label = Text.norm(portfolio.statItems().nth(i).locator(".stat-label").textContent());
            actual.add(num + " " + label);
        }
        assertEquals(actual, expected);
    }

    @Test(description = "Hero, photo included, fits in the first screen on desktop")
    public void heroFitsAboveTheFold() {
        assertThat(portfolio.hero().heading()).isInViewport();
        assertThat(portfolio.hero().photo()).isInViewport();
        assertTrue(portfolio.hero().ctaButtons().first().boundingBox().y < DESKTOP_HEIGHT,
                "Main button is below the fold");
    }
}
