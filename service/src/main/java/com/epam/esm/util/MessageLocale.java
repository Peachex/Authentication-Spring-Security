package com.epam.esm.util;

import java.util.Locale;

public enum MessageLocale {
    RU("ru", "RU"),
    EN("en", "US");

    private final String language;
    private final String country;

    MessageLocale(String language, String country) {
        this.language = language;
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public String getCountry() {
        return country;
    }

    public static Locale defineLocale(String localeStr) {
        if (localeStr != null && !localeStr.isEmpty()) {
            for (MessageLocale locale : MessageLocale.values()) {
                if (locale.country.equalsIgnoreCase(localeStr) || locale.language.equalsIgnoreCase(localeStr) ||
                        localeStr.equalsIgnoreCase(locale.toString())) {
                    return new Locale(locale.language, locale.country);
                }
            }
        }
        return new Locale(EN.language, EN.country);
    }

    @Override
    public String toString() {
        return language + "_" + country;
    }
}
