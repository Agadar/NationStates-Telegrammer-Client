package com.github.agadar.telegrammer.cli;

import com.github.agadar.nationstates.NationStates;
import com.github.agadar.nationstates.NationStatesAPIException;
import com.github.agadar.nationstates.enumerator.RegionTag;

import com.github.agadar.telegrammer.core.enums.FilterType;
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
import com.github.agadar.telegrammer.core.manager.HistoryManager;
import com.github.agadar.telegrammer.core.manager.PropertiesManager;
import com.github.agadar.telegrammer.core.manager.TelegramManager;
import com.github.agadar.telegrammer.core.util.StringFunctions;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
     * Default split string.
     */
    private static final String SPLITSTRING = ",";

    /**
     * The TelegramManager used throughout the program.
     */
    private static final TelegramManager TG_MANAGER = TelegramManager.get();

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

        // Attempt to set and verify user agent.
        try {
            NationStates.setUserAgent("Agadar's Telegrammer CLI (https://github.com/Agadar/NationStates-Telegrammer)");
        } catch (NationStatesAPIException ex) {
            LOGGER.log(Level.SEVERE, "Failed to communicate with NationStates API", ex);
            System.exit(1);
        }

        // Retrieve history.
        HistoryManager.get().loadHistory();

        // Retrieve and parse filters.
        try {
            retrieveAndParseFilters(FILTERS_FILENAME, SPLITSTRING, TG_MANAGER, LOGGER);
        } catch (IOException ex) {
            // The file probably doesn't exist. Log it and exit.
            LOGGER.log(Level.WARNING, "Failed to load filters file", ex);
            System.exit(1);
        }

        // If there is at least one recipient, start sending.
        if (TG_MANAGER.numberOfRecipients() == 0 && TG_MANAGER.cantRetrieveMoreNations()) {
            LOGGER.log(Level.WARNING, "No recipients could be generated with the supplied filters");
            System.exit(1);
        }
        TG_MANAGER.startSending(false);

        
    }

    /**
     * Retrieves and parses the filters from the filter file, adding them to the
     * telegram manager.
     *
     * @param fileName
     * @param splitter
     * @param telegramManager
     * @param logger
     * @throws IOException if the file was not found
     */
    private static void retrieveAndParseFilters(String fileName, String splitter,
            TelegramManager telegramManager, Logger logger) throws IOException {
        // Break the filters file contents into lines and iterate over them.
        try (final Stream<String> lines = Files.lines(Paths.get(fileName), Charset.defaultCharset())) {
            lines.filter(line -> line != null && !line.isEmpty()).map(line -> line.split(splitter)).forEach(splitLine -> {

                // Try parsing the filter type, which should be the first item in line.
                FilterType filterType;
                try {
                    filterType = FilterType.valueOf(splitLine[0]);
                } catch (IllegalArgumentException | NullPointerException ex) {
                    // Failed to parse the filter type, so we log it and skip it.
                    logger.log(Level.WARNING, "Failed to parse value ''{0}'' to a filter type", splitLine[0]);
                    return;
                }

                // If the FilterType is any of the given types but does not list
                // at least one parameter, then log it and skip it.
                if (!(filterType == FilterType.ALL || filterType == FilterType.DELEGATES_EXCL
                        || filterType == FilterType.DELEGATES_INCL || filterType == FilterType.DELEGATES_NEW
                        || filterType == FilterType.NATIONS_NEW || filterType == FilterType.NATIONS_REFOUNDED
                        || filterType == FilterType.NATIONS_EJECTED || filterType == FilterType.WA_MEMBERS_EXCL
                        || filterType == FilterType.WA_MEMBERS_INCL || filterType == FilterType.WA_MEMBERS_NEW)
                        && splitLine.length < 2) {
                    logger.log(Level.WARNING, "Parsed filter type requires parameters, but none supplied");
                    return;
                }
                final List<String> argumentsList = new ArrayList<>(Arrays.asList(splitLine));
                argumentsList.remove(0);    // Remove first element, which is the filter type.

                // Instantiate a new filter according to the type and the parameters.
                switch (filterType) {
                    case ALL:
                        telegramManager.addFilter(new FilterAll());
                        break;
                    case DELEGATES_EXCL:
                        telegramManager.addFilter(new FilterDelegates(false));
                        break;
                    case DELEGATES_INCL:
                        telegramManager.addFilter(new FilterDelegates(true));
                        break;
                    case DELEGATES_NEW:
                        telegramManager.addFilter(new FilterDelegatesNew());
                        break;
                    case DELEGATES_NEW_MAX: {
                        final int amount = StringFunctions.stringToUInt(argumentsList.get(0));
                        telegramManager.addFilter(new FilterDelegatesNewFinite(amount));
                        break;
                    }
                    case EMBASSIES_EXCL:
                        telegramManager.addFilter(new FilterEmbassies(new HashSet<>(argumentsList), false));
                        break;
                    case EMBASSIES_INCL:
                        telegramManager.addFilter(new FilterEmbassies(new HashSet<>(argumentsList), true));
                        break;
                    case NATIONS_EXCL:
                        telegramManager.addFilter(new FilterNations(new HashSet<>(argumentsList), false));
                        break;
                    case NATIONS_INCL:
                        telegramManager.addFilter(new FilterNations(new HashSet<>(argumentsList), true));
                        break;
                    case NATIONS_NEW_MAX: {
                        final int amount = StringFunctions.stringToUInt(argumentsList.get(0));
                        telegramManager.addFilter(new FilterNationsNewFinite(amount));
                        break;
                    }
                    case NATIONS_NEW:
                        telegramManager.addFilter(new FilterNationsNew());
                        break;
                    case NATIONS_REFOUNDED_MAX: {
                        final int amount = StringFunctions.stringToUInt(argumentsList.get(0));
                        telegramManager.addFilter(new FilterNationsRefoundedFinite(amount));
                        break;
                    }
                    case NATIONS_REFOUNDED:
                        telegramManager.addFilter(new FilterNationsRefounded());
                        break;
                    case NATIONS_EJECTED_MAX: {
                        final int amount = StringFunctions.stringToUInt(argumentsList.get(0));
                        telegramManager.addFilter(new FilterNationsEjectedFinite(amount));
                        break;
                    }
                    case NATIONS_EJECTED:
                        telegramManager.addFilter(new FilterNationsEjected());
                        break;
                    case REGIONS_EXCL:
                        telegramManager.addFilter(new FilterRegions(new HashSet<>(argumentsList), false));
                        break;
                    case REGIONS_INCL:
                        telegramManager.addFilter(new FilterRegions(new HashSet<>(argumentsList), true));
                        break;
                    case REGIONS_WITH_TAGS_EXCL: {
                        final Set<RegionTag> recipients = StringFunctions.stringsToRegionTags(argumentsList);
                        if (recipients.size() > 0) {
                            telegramManager.addFilter(new FilterRegionsWithTags(recipients, false));
                        }
                        break;
                    }
                    case REGIONS_WITH_TAGS_INCL: {
                        final Set<RegionTag> recipients = StringFunctions.stringsToRegionTags(argumentsList);
                        if (recipients.size() > 0) {
                            telegramManager.addFilter(new FilterRegionsWithTags(recipients, true));
                        }
                        break;
                    }
                    case REGIONS_WO_TAGS_EXCL: {
                        final Set<RegionTag> recipients = StringFunctions.stringsToRegionTags(argumentsList);
                        if (recipients.size() > 0) {
                            telegramManager.addFilter(new FilterRegionsWithoutTags(recipients, false));
                        }
                        break;
                    }
                    case REGIONS_WO_TAGS_INCL: {
                        final Set<RegionTag> recipients = StringFunctions.stringsToRegionTags(argumentsList);
                        if (recipients.size() > 0) {
                            telegramManager.addFilter(new FilterRegionsWithoutTags(recipients, true));
                        }
                        break;
                    }
                    case WA_MEMBERS_EXCL:
                        telegramManager.addFilter(new FilterWAMembers(false));
                        break;
                    case WA_MEMBERS_INCL:
                        telegramManager.addFilter(new FilterWAMembers(true));
                        break;
                    case WA_MEMBERS_NEW_MAX: {
                        final int amount = StringFunctions.stringToUInt(argumentsList.get(0));
                        telegramManager.addFilter(new FilterWAMembersNewFinite(amount));
                        break;
                    }
                    case WA_MEMBERS_NEW:
                        telegramManager.addFilter(new FilterWAMembersNew());
                        break;
                    default:
                        logger.log(Level.WARNING, "Unsupported filter type: ''{0}''", filterType.name());
                        break;
                }

            });
        } catch (IOException ex) {
            throw ex;
        }
    }
}
