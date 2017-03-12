package com.github.agadar.telegrammer.core.filter.abstractfilter;

import com.github.agadar.nationstates.domain.common.Happening;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract parent for filters that add nations derived from Happenings. These
 * can go on retrieving new recipients forever.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public abstract class FilterHappenings extends Filter {

    /**
     * Pattern used for extracting nation names from happenings descriptions.
     */
    private final static Pattern PATTERN = Pattern.compile("\\@\\@(.*?)\\@\\@");

    /**
     * The keyword used for finding the correct happenings in a list of
     * happenings.
     */
    public enum KeyWord {
        became, // new delegates
        refounded, // refounded nations
        ejected, // ejected nations
        admitted // new WA members
        ;
    }

    /**
     * This instance's keyword.
     */
    private final KeyWord myKeyWord;

    public FilterHappenings(KeyWord keyWord) {
        this.myKeyWord = keyWord;
    }

    /**
     * From a list of happenings, derives nation names from each happening that
     * contains in its description the supplied string.
     *
     * @param happenings
     * @return
     */
    protected Set<String> filterHappenings(Set<Happening> happenings) {
        final Set<String> temp = new HashSet<>();

        happenings.forEach(h
                -> {
            if (h.description.contains(myKeyWord.toString())) {
                final Matcher matcher = PATTERN.matcher(h.description);

                if (matcher.find()) {
                    temp.add(matcher.group(1));
                }
            }
        });
        return temp;
    }
}
