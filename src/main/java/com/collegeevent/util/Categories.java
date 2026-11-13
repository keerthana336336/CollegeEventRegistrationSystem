package com.collegeevent.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Fixed drop-down values used by forms and filters. */
public final class Categories {
    public static final List<String> EVENT_CATEGORIES = Collections.unmodifiableList(
            Arrays.asList("Technical", "Cultural", "Sports", "Workshop", "Seminar", "Career", "Social"));

    public static final List<String> DEPARTMENTS = Collections.unmodifiableList(
            Arrays.asList("Computer Science", "Information Technology", "Electronics", "Electrical",
                    "Mechanical", "Civil", "AI & Data Science", "Business Administration", "Arts & Sciences", "Other"));

    public static final List<Integer> YEARS = Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4, 5));

    private Categories() { }

    public static boolean isValidCategory(String c) { return c != null && EVENT_CATEGORIES.contains(c); }

    public static boolean isValidDepartment(String d) { return d != null && DEPARTMENTS.contains(d); }

    /** CSS class used for poster colours, e.g. "cat-technical". */
    public static String cssClass(String category) {
        return "cat-" + (category == null ? "other" : category.toLowerCase().replaceAll("[^a-z]", ""));
    }
}
