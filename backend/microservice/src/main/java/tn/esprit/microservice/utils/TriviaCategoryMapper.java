package tn.esprit.microservice.utils;

import java.util.HashMap;
import java.util.Map;

public class TriviaCategoryMapper {
    private static final Map<String, Integer> CATEGORY_MAP = new HashMap<>();

    static {
        CATEGORY_MAP.put("General Knowledge", 9);
        CATEGORY_MAP.put("Science: Computers", 18);
        CATEGORY_MAP.put("Mathematics", 19);
        CATEGORY_MAP.put("History", 23);
        CATEGORY_MAP.put("Sports", 21);
        CATEGORY_MAP.put("Java", 18); // Example mapping for "Java"
        CATEGORY_MAP.put("Gaming", 15); // Added Gaming category
        CATEGORY_MAP.put("Python", 18); // Added Python
        CATEGORY_MAP.put("C#", 18); // Added C#
        CATEGORY_MAP.put("C++", 18); // Added C++
        CATEGORY_MAP.put("PHP", 18); // Added PHP
        CATEGORY_MAP.put("JavaScript", 18); // Added JavaScript
        CATEGORY_MAP.put("SQL", 18); // Added SQL
        CATEGORY_MAP.put("Hadoop", 18); // Added Hadoop
        CATEGORY_MAP.put(".NET", 18); // Added .NET
        // Add more categories as needed
    }

    public static Integer getCategoryId(String categoryName) {
        return CATEGORY_MAP.getOrDefault(categoryName, null);
    }
}
