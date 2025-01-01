package com.example.assignment.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageManager {
    private static final String LANGUAGE_PATH = "src/main/resources/com/example/assignment/static/i18n/";
    private static final Map<String, Object> translations = new HashMap<>();
    private static String currentLanguage = "en";
    private static Runnable onLanguageChangeListener;
    private static final List<Runnable> languageChangeListeners = new ArrayList<>();
    public static void loadLanguage(String languageCode) {
        try (FileReader reader = new FileReader(LANGUAGE_PATH + languageCode + ".json")) {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(reader);

            translations.clear();
            translations.putAll(jsonObject);

            currentLanguage = languageCode;

            System.out.println("Triggering language change listeners...");
            notifyLanguageChange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        String[] keys = key.split("\\.");
        Object current = translations;
        for (String k : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(k);
            } else {
                return "Key not found";
            }
        }
        return current instanceof String ? (String) current : "Key not found";
    }

    public static String getCurrentLanguage() {
        return currentLanguage;
    }

    public static void setOnLanguageChangeListener(Runnable listener) {
        System.out.println("Language change listener registered.");
        onLanguageChangeListener = listener;
    }

    public static void addLanguageChangeListener(Runnable listener) {
        languageChangeListeners.add(listener);
        System.out.println("Language change listener added.");
    }

    public static void notifyLanguageChange() {
        for (Runnable listener : languageChangeListeners) {
            listener.run();
        }
    }



}
