package com.vidyullatha.portfolio.config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Run settings. Each value comes from a system property (-Dname=value), then an
 * environment variable, then a default.
 *
 * <ul>
 *   <li>{@code baseUrl}  / BASE_URL  — site under test (default http://localhost:4000/)</li>
 *   <li>{@code browser}  / BROWSER   — chromium | firefox | webkit (default chromium)</li>
 *   <li>{@code headless} / HEADLESS  — true | false (default true)</li>
 *   <li>{@code dataDir}  / DATA_DIR  — the site's _data folder (default ../_data)</li>
 * </ul>
 */
public final class Config {

    private Config() {
    }

    public static String baseUrl() {
        String url = firstNonBlank(System.getProperty("baseUrl"), System.getenv("BASE_URL"), "http://localhost:4000/");
        return url.endsWith("/") ? url : url + "/";
    }

    public static String browser() {
        return firstNonBlank(System.getProperty("browser"), System.getenv("BROWSER"), "chromium").toLowerCase();
    }

    public static boolean headless() {
        return !"false".equalsIgnoreCase(firstNonBlank(System.getProperty("headless"), System.getenv("HEADLESS"), "true"));
    }

    public static Path dataDir() {
        return Paths.get(firstNonBlank(System.getProperty("dataDir"), System.getenv("DATA_DIR"), "../_data"))
                .toAbsolutePath()
                .normalize();
    }

    public static Path screenshotDir() {
        return Paths.get("target", "screenshots");
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}
