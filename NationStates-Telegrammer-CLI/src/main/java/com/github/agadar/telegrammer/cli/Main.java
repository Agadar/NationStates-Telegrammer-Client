package com.github.agadar.telegrammer.cli;

import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.telegrammer.core.enums.FilterType;
import com.github.agadar.telegrammer.core.enums.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.filter.FilterAll;
import com.github.agadar.telegrammer.core.filter.FilterDelegates;
import com.github.agadar.telegrammer.core.filter.FilterDelegatesNew;
import com.github.agadar.telegrammer.core.filter.FilterDelegatesNewFinite;
import com.github.agadar.telegrammer.core.filter.FilterEmbassies;
import com.github.agadar.telegrammer.core.filter.FilterNations;
import com.github.agadar.telegrammer.core.filter.FilterNationsEjected;
import com.github.agadar.telegrammer.core.filter.FilterNationsEjectedFinite;
import com.github.agadar.telegrammer.core.filter.FilterNationsNew;
import com.github.agadar.telegrammer.core.filter.FilterNationsNewFinite;
import com.github.agadar.telegrammer.core.filter.FilterNationsRefounded;
import com.github.agadar.telegrammer.core.filter.FilterNationsRefoundedFinite;
import com.github.agadar.telegrammer.core.filter.FilterRegions;
import com.github.agadar.telegrammer.core.filter.FilterRegionsWithTags;
import com.github.agadar.telegrammer.core.filter.FilterRegionsWithoutTags;
import com.github.agadar.telegrammer.core.filter.FilterWAMembers;
import com.github.agadar.telegrammer.core.filter.FilterWAMembersNew;
import com.github.agadar.telegrammer.core.filter.FilterWAMembersNewFinite;
import com.github.agadar.telegrammer.core.filter.abstractfilter.Filter;
import com.github.agadar.telegrammer.core.manager.HistoryManager;
import com.github.agadar.telegrammer.core.manager.PropertiesManager;
import com.github.agadar.telegrammer.core.manager.TelegramManager;
import com.github.agadar.telegrammer.core.util.Tuple;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * CLI-based client that reads configurations from the properties file and a
 * filters file and immediately starts sending telegrams.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class Main {

    /**
     * Logs information to the CLI.
     */
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    /**
     * Name of the file which the preconfigured filters should reside in.
     */
    private static final String FILTERS_FILENAME = ".nationstates-telegrammer.filters";

    /**
     * The filters, derived from the filters file.
     */
    private static final List<Filter> FILTERS = new ArrayList<>();

    /**
     * Default split string.
     */
    private static final String SPLITSTRING = ",";

    /**
     * Program entry point. Arguments are ignored.
     *
     * @param args
     */
    public static void main(String[] args) {

        // Retrieve properties, quit if they aren't supplied.
        if (!PropertiesManager.get().loadProperties()) {
            LOGGER.log(Level.WARNING, "Failed to load properties file");
            System.exit(1);
        }

        // Break the filters file contents into lines and iterate over them.
        try (final Stream<String> lines = Files.lines(Paths.get(FILTERS_FILENAME), Charset.defaultCharset())) {
            lines.map(line -> line.split(SPLITSTRING)).filter(splitLine -> splitLine.length >= 1).forEach(splitLine -> {
                
                // Try parsing the filter type, which should be the first item in line.
                FilterType filterType;
                try { 
                    filterType = FilterType.valueOf(splitLine[0]);
                } catch (IllegalArgumentException | NullPointerException ex) {
                    // Failed to parse the filter type, so we log it and skip it.
                    LOGGER.log(Level.WARNING, "Failed to parse filter type", ex);
                    return;
                }
                
                // If the FilterType is any of the given types but does not list
                // at least one parameter, then log it and skip it.
                if (!(filterType == FilterType.ALL || filterType == FilterType.DELEGATES_EXCL ||
                        filterType == FilterType.DELEGATES_INCL || filterType == FilterType.DELEGATES_NEW ||
                        filterType == FilterType.NATIONS_NEW || filterType == FilterType.NATIONS_REFOUNDED ||
                        filterType == FilterType.NATIONS_EJECTED || filterType == FilterType.WA_MEMBERS_EXCL ||
                        filterType == FilterType.WA_MEMBERS_INCL || filterType == FilterType.WA_MEMBERS_NEW) &&
                        splitLine.length < 2) {
                    LOGGER.log(Level.WARNING, "Parsed filter type requires parameters, but none supplied");
                    return;
                }
                
                // Instantiate a new filter according to the type and the parameters.
                Filter filter;
                            
            });
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Failed to load filters file", ex);
            System.exit(1);
        }

        // Retrieve history.
        HistoryManager.get().loadHistory();

        // Apply the filters.
        // If there is at least one recipient, start sending.
        // 
    }

}
