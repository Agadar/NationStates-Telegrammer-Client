package com.github.agadar.telegrammer.core.util;

import com.github.agadar.nationstates.enumerator.RegionTag;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exposes some String-related utility functions.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class StringFunctions {

    /**
     * Private constructor.
     */
    private StringFunctions() {
    }

    /**
     * Converts a comma-separated string to a list of strings.
     *
     * @param string
     * @return
     */
    public static Set<String> stringToStringList(String string) {
        final List<String> asList = Arrays.asList(string.trim().split("\\s*,\\s*"));
        return asList.size() == 1 && asList.get(0).isEmpty() ? new HashSet<>() : new HashSet<>(asList);
    }

    /**
     * Parses the supplied string values to a set of RegionTags. Strings that
     * cannot be parsed are ignored.
     *
     * @param tagsStrSet The strings to parse
     * @return The resulting RegionTags
     */
    public static Set<RegionTag> stringsToRegionTags(Set<String> tagsStrSet) {
        final Set<RegionTag> tags = new HashSet();
        tagsStrSet.stream().forEach(tagStr -> {
            try {
                tags.add(RegionTag.fromString(tagStr));
            } catch (IllegalArgumentException ex) {
                // Ignore because we don't care.
            }
        });
        return tags;
    }

    /**
     * Parses the supplied string to an unsigned int. If the supplied string is
     * null or cannot be parsed, then 0 is returned.
     *
     * @param parseMe
     * @return
     */
    public static int stringToUInt(String parseMe) {
        if (parseMe == null) {
            return 0;
        }

        try {
            return Integer.parseUnsignedInt(parseMe);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
