package com.vidyullatha.portfolio.tests;

import com.vidyullatha.portfolio.base.BaseTest;
import com.vidyullatha.portfolio.data.SiteData;
import com.vidyullatha.portfolio.support.Text;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * Data-driven checks: About, Skills, Experience, Education, Projects and Resume
 * render exactly what is in the _data/*.yml files, in the same order.
 */
public class ContentTest extends BaseTest {

    @DataProvider(name = "jobs")
    public Object[][] jobs() {
        List<Map<String, Object>> jobs = SiteData.experience();
        Object[][] rows = new Object[jobs.size()][];
        for (int i = 0; i < jobs.size(); i++) {
            rows[i] = new Object[] {i, SiteData.str(jobs.get(i), "role"), jobs.get(i)};
        }
        return rows;
    }

    @DataProvider(name = "skillGroups")
    public Object[][] skillGroups() {
        List<Map<String, Object>> groups = SiteData.mapList(SiteData.skills(), "groups");
        Object[][] rows = new Object[groups.size()][];
        for (int i = 0; i < groups.size(); i++) {
            rows[i] = new Object[] {i, SiteData.str(groups.get(i), "group"), groups.get(i)};
        }
        return rows;
    }

    @DataProvider(name = "degrees")
    public Object[][] degrees() {
        List<Map<String, Object>> degrees = SiteData.education();
        Object[][] rows = new Object[degrees.size()][];
        for (int i = 0; i < degrees.size(); i++) {
            rows[i] = new Object[] {i, SiteData.str(degrees.get(i), "degree"), degrees.get(i)};
        }
        return rows;
    }

    // ── About ──────────────────────────────────────────────────

    @Test(description = "About focus cards match _data/about.yml")
    public void aboutPointsMatchData() {
        List<String> expected = new ArrayList<>();
        for (Map<String, Object> point : SiteData.mapList(SiteData.about(), "points")) {
            expected.add(SiteData.str(point, "title"));
        }
        assertEquals(Text.norm(portfolio.aboutPointTitles().allTextContents()), expected);
    }

    // ── Skills ─────────────────────────────────────────────────

    @Test(description = "Core tool tiles match skills.core, in order")
    public void coreToolsMatchData() {
        assertEquals(Text.norm(portfolio.skills().coreToolNames().allTextContents()),
                SiteData.strList(SiteData.skills(), "core"));
    }

    @Test(description = "One skill card per group in skills.groups")
    public void skillGroupCountMatchesData() {
        assertThat(portfolio.skills().groups()).hasCount(SiteData.mapList(SiteData.skills(), "groups").size());
    }

    @Test(dataProvider = "skillGroups", description = "Each skill card has the right title and items")
    public void skillGroupMatchesData(int index, String groupName, Map<String, Object> group) {
        assertEquals(Text.norm(portfolio.skills().groupTitle(index).textContent()), groupName);
        assertEquals(Text.norm(portfolio.skills().groupItems(index).allTextContents()),
                SiteData.strList(group, "items"), "Items in '" + groupName + "'");
    }

    // ── Experience ─────────────────────────────────────────────

    @Test(description = "One experience card per job")
    public void experienceCountMatchesData() {
        assertThat(portfolio.experience().cards()).hasCount(SiteData.experience().size());
    }

    @Test(dataProvider = "jobs", description = "Each experience card shows the right role, company, dates and tags")
    public void experienceCardMatchesData(int index, String role, Map<String, Object> job) {
        var section = portfolio.experience();
        assertEquals(Text.norm(section.role(index).textContent()), role);
        assertEquals(Text.norm(section.company(index).textContent()),
                SiteData.str(job, "company") + " · " + SiteData.str(job, "location"));
        assertEquals(Text.norm(section.period(index).textContent()), SiteData.str(job, "period"));
        assertEquals(Text.norm(section.description(index).textContent()), Text.norm(SiteData.str(job, "description")));

        Map<String, Object> tags = SiteData.nested(job, "tags");
        List<String> expectedTags = new ArrayList<>();
        expectedTags.addAll(SiteData.strList(tags, "primary"));
        expectedTags.addAll(SiteData.strList(tags, "secondary"));
        expectedTags.addAll(SiteData.strList(tags, "other"));
        assertEquals(Text.norm(section.tags(index).allTextContents()), expectedTags, "Tags for " + role);
    }

    @Test(dataProvider = "jobs", description = "Only roles that run to 'Present' get the Current badge")
    public void currentBadgeOnlyOnCurrentRole(int index, String role, Map<String, Object> job) {
        boolean current = SiteData.str(job, "period").contains("Present");
        assertThat(portfolio.experience().currentBadge(index)).hasCount(current ? 1 : 0);
    }

    // ── Education ──────────────────────────────────────────────

    @Test(dataProvider = "degrees", description = "Each degree card matches the data, with GPA/honors only when set")
    public void degreeCardMatchesData(int index, String degree, Map<String, Object> entry) {
        var education = portfolio.education();
        assertEquals(Text.norm(education.degree(index).textContent()), degree);
        assertEquals(Text.norm(education.school(index).textContent()), SiteData.str(entry, "school"));

        List<String> expectedHighlights = new ArrayList<>();
        if (SiteData.isSet(entry, "gpa")) {
            expectedHighlights.add(SiteData.str(entry, "gpa"));
        }
        if (SiteData.isSet(entry, "honors")) {
            expectedHighlights.add(SiteData.str(entry, "honors"));
        }
        assertEquals(Text.norm(education.highlights(index).allTextContents()), expectedHighlights);
    }

    // ── Optional sections ──────────────────────────────────────

    @Test(description = "Projects section appears only when projects.yml has entries")
    public void projectsSectionFollowsData() {
        List<Map<String, Object>> projects = SiteData.projects();
        if (projects.isEmpty()) {
            assertThat(portfolio.section("projects")).hasCount(0);
            assertThat(portfolio.nav().link("projects")).hasCount(0);
        } else {
            assertThat(portfolio.projectCards()).hasCount(projects.size());
            List<String> titles = new ArrayList<>();
            for (Map<String, Object> project : projects) {
                titles.add(SiteData.str(project, "title"));
            }
            assertEquals(Text.norm(portfolio.projectCards().locator(".project-title").allTextContents()), titles);
        }
    }

    @Test(description = "Resume section and buttons appear only when resume_pdf is set")
    public void resumeFollowsData() {
        Map<String, Object> profile = SiteData.profile();
        if (SiteData.isSet(profile, "resume_pdf")) {
            String pdf = SiteData.str(profile, "resume_pdf");
            assertThat(portfolio.section("resume")).hasCount(1);
            assertThat(portfolio.nav().resumeButton()).hasAttribute("href", pdf);
        } else {
            assertThat(portfolio.section("resume")).hasCount(0);
            assertThat(portfolio.nav().resumeButton()).hasCount(0);
        }
    }
}
