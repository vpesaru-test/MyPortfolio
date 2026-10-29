package com.vidyullatha.portfolio.data;

import com.vidyullatha.portfolio.config.Config;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reads the same _data/*.yml files Jekyll uses to build the page, so every
 * content assertion checks the rendered site against its source of truth.
 * Edit a YAML file and the expectations update with it.
 */
public final class SiteData {

    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    private SiteData() {
    }

    public static Map<String, Object> profile() {
        return asMap(load("profile"));
    }

    public static Map<String, Object> about() {
        return asMap(load("about"));
    }

    public static Map<String, Object> skills() {
        return asMap(load("skills"));
    }

    public static List<Map<String, Object>> experience() {
        return asListOfMaps(load("experience"));
    }

    public static List<Map<String, Object>> education() {
        return asListOfMaps(load("education"));
    }

    public static List<Map<String, Object>> certifications() {
        return asListOfMaps(load("certifications"));
    }

    public static List<Map<String, Object>> projects() {
        return asListOfMaps(load("projects"));
    }

    // ── Field helpers ──────────────────────────────────────────

    /** The value as trimmed text, or "" when missing or null. */
    public static String str(Map<String, Object> map, String key) {
        Object value = map == null ? null : map.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }

    /** True when the field exists and is not blank. */
    public static boolean isSet(Map<String, Object> map, String key) {
        return !str(map, key).isEmpty();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> nested(Map<String, Object> map, String key) {
        Object value = map == null ? null : map.get(key);
        return value instanceof Map ? (Map<String, Object>) value : Collections.emptyMap();
    }

    public static List<String> strList(Map<String, Object> map, String key) {
        Object value = map == null ? null : map.get(key);
        List<String> out = new ArrayList<>();
        if (value instanceof List<?> list) {
            for (Object item : list) {
                out.add(String.valueOf(item).trim());
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> mapList(Map<String, Object> map, String key) {
        return asListOfMaps(map == null ? null : map.get(key));
    }

    // ── Loading ────────────────────────────────────────────────

    private static Object load(String name) {
        return CACHE.computeIfAbsent(name, n -> {
            Path file = Config.dataDir().resolve(n + ".yml");
            if (!Files.exists(file)) {
                throw new IllegalStateException("Data file not found: " + file
                        + " (set -DdataDir to the site's _data folder)");
            }
            try (Reader reader = Files.newBufferedReader(file)) {
                Object parsed = new Yaml().load(reader);
                return parsed == null ? new ArrayList<>() : parsed;
            } catch (IOException e) {
                throw new UncheckedIOException("Could not read " + file, e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value) {
        return value instanceof Map ? (Map<String, Object>) value : new LinkedHashMap<>();
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> asListOfMaps(Object value) {
        List<Map<String, Object>> out = new ArrayList<>();
        if (value instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map) {
                    out.add((Map<String, Object>) item);
                }
            }
        }
        return out;
    }
}
