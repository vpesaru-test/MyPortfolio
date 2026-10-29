package com.vidyullatha.portfolio.tests;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.vidyullatha.portfolio.base.BaseTest;
import com.vidyullatha.portfolio.config.Config;
import com.vidyullatha.portfolio.data.SiteData;
import com.vidyullatha.portfolio.support.Text;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Certification cards: content, which ones are clickable, where they link,
 * and data-quality checks on the links themselves.
 */
public class CertificationsTest extends BaseTest {

    private static final String CERT_DIR = "assets/certificates/";

    @DataProvider(name = "certs")
    public Object[][] certs() {
        List<Map<String, Object>> certs = SiteData.certifications();
        Object[][] rows = new Object[certs.size()][];
        for (int i = 0; i < certs.size(); i++) {
            rows[i] = new Object[] {i, SiteData.str(certs.get(i), "name"), certs.get(i)};
        }
        return rows;
    }

    @DataProvider(name = "certFiles")
    public Object[][] certFiles() {
        List<Object[]> rows = new ArrayList<>();
        for (Map<String, Object> cert : SiteData.certifications()) {
            if (SiteData.isSet(cert, "file") && !SiteData.isSet(cert, "url")) {
                rows.add(new Object[] {SiteData.str(cert, "name"), SiteData.str(cert, "file")});
            }
        }
        return rows.toArray(new Object[0][]);
    }

    /** What a card should link to: url wins over file; null means not clickable. */
    private static String expectedHref(Map<String, Object> cert) {
        if (SiteData.isSet(cert, "url")) {
            return SiteData.str(cert, "url");
        }
        if (SiteData.isSet(cert, "file")) {
            return CERT_DIR + SiteData.str(cert, "file");
        }
        return null;
    }

    @Test(description = "One card per certification, in the order of certifications.yml")
    public void certificationsMatchDataOrder() {
        List<String> names = new ArrayList<>();
        for (Map<String, Object> cert : SiteData.certifications()) {
            names.add(SiteData.str(cert, "name"));
        }
        assertEquals(Text.norm(portfolio.education().certCards().locator(".cert-name").allTextContents()), names);
    }

    @Test(dataProvider = "certs", description = "Each card shows issuer and date, and links only when url/file is set")
    public void certificationCardMatchesData(int index, String name, Map<String, Object> cert) {
        var education = portfolio.education();
        Locator card = education.certCard(index);

        assertEquals(Text.norm(education.certMeta(index).textContent()),
                SiteData.str(cert, "issuer") + " · " + SiteData.str(cert, "date"), "Issuer/date for " + name);

        String href = expectedHref(cert);
        String tag = (String) card.evaluate("el => el.tagName");
        if (href == null) {
            assertEquals(tag, "DIV", name + " has no url/file, so it should not be a link");
            assertThat(education.certViewCue(index)).hasCount(0);
        } else {
            assertEquals(tag, "A", name + " has a url/file, so it should be a link");
            assertThat(card).hasAttribute("href", href);
            assertThat(card).hasAttribute("target", "_blank");
            assertThat(card).hasAttribute("rel", Pattern.compile("\\bnoopener\\b"));
            assertThat(education.certViewCue(index)).isVisible();
        }
    }

    @Test(description = "Certificate links use https")
    public void certificateUrlsUseHttps() {
        List<String> insecure = new ArrayList<>();
        for (Map<String, Object> cert : SiteData.certifications()) {
            String url = SiteData.str(cert, "url");
            if (!url.isEmpty() && !url.startsWith("https://")) {
                insecure.add(SiteData.str(cert, "name") + " → " + url);
            }
        }
        assertTrue(insecure.isEmpty(), "Certificate links that are not https:\n" + String.join("\n", insecure));
    }

    @Test(description = "No two certifications share the same link (usually a copy-paste mistake)")
    public void certificateUrlsAreUnique() {
        Map<String, List<String>> byUrl = new LinkedHashMap<>();
        for (Map<String, Object> cert : SiteData.certifications()) {
            String url = SiteData.str(cert, "url");
            if (!url.isEmpty()) {
                byUrl.computeIfAbsent(url, u -> new ArrayList<>()).add(SiteData.str(cert, "name"));
            }
        }
        List<String> duplicates = new ArrayList<>();
        byUrl.forEach((url, names) -> {
            if (names.size() > 1) {
                duplicates.add(url + " is used by: " + String.join(", ", names));
            }
        });
        assertTrue(duplicates.isEmpty(),
                "Each certificate should link to its own credential. Fix these in _data/certifications.yml:\n"
                        + String.join("\n", duplicates));
    }

    @Test(dataProvider = "certFiles", description = "Uploaded certificate files exist and are PDFs or images")
    public void certificateFileIsServed(String name, String file) {
        APIResponse response = page.request().get(Config.baseUrl() + CERT_DIR + file);
        assertEquals(response.status(), 200, name + ": " + CERT_DIR + file + " was not found");
        String type = String.valueOf(response.headers().get("content-type"));
        assertTrue(type.startsWith("application/pdf") || type.startsWith("image/"),
                name + ": expected a PDF or image but got " + type);
    }

    @Test(description = "Clicking a certificate opens it in a new tab and leaves the portfolio open")
    public void certificateOpensInNewTab() {
        Locator localCert = portfolio.education().linkedCertCards()
                .and(page.locator("[href^='" + CERT_DIR + "']"));
        if (localCert.count() == 0) {
            throw new SkipException("No certificate files in " + CERT_DIR + " to click");
        }
        String portfolioUrl = page.url();

        Page tab = page.waitForPopup(() -> localCert.first().click());

        assertEquals(context.pages().size(), 2, "Expected the certificate in a second tab");
        assertEquals(page.url(), portfolioUrl, "The portfolio tab should not navigate away");
        tab.close();
    }
}
