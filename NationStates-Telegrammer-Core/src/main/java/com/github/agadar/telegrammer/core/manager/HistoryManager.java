package com.github.agadar.telegrammer.core.manager;

import com.github.agadar.telegrammer.core.enums.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.util.Tuple;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Assists in saving and loading the history file for this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class HistoryManager {

    /**
     * The singleton.
     */
    private static HistoryManager INSTANCE;

    /**
     * Default file name.
     */
    private static final String FILENAME = ".nationstates-telegrammer.history";

    /**
     * Default split string.
     */
    private static final String SPLITSTRING = ",";

    /**
     * The history data retrieved from and saved to the file.
     */
    public Map<Tuple<String, String>, SkippedRecipientReason> history;

    /**
     * Gets this class's singleton.
     *
     * @return This class's singleton.
     */
    public static HistoryManager get() {
        if (INSTANCE == null) {
            INSTANCE = new HistoryManager();
        }
        return INSTANCE;
    }

    /**
     * Private constructor, because singleton.
     */
    private HistoryManager() {
    }

    /**
     * Saves the application's history data to the file.
     *
     * @return True if saving succeeded, false otherwise.
     */
    public boolean saveHistory() {
        // If there is no history, don't even bother.
        if (history == null || history.isEmpty()) {
            return true;
        }

        try {
            // Parse all entries to a single string.
            final String contents = history.entrySet().stream()
                    .map(historyEntry -> historyEntry.getKey().x
                            + SPLITSTRING + historyEntry.getKey().y
                            + SPLITSTRING + historyEntry.getValue().name())
                    .collect(Collectors.joining(System.lineSeparator()));
            Files.write(Paths.get(FILENAME), contents.getBytes()); // Write the resulting string to the file.
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    /**
     * Loads the application's history data from the file.
     *
     * @return True if loading succeeded, false otherwise.
     */
    public boolean loadHistory() {
        history = new HashMap<>(); // Ensure history is new and empty.

        // Break the history file contents into lines and iterate over them.
        try (final Stream<String> lines = Files.lines(Paths.get(FILENAME), Charset.defaultCharset())) {
            lines.map(line -> line.split(SPLITSTRING)).filter(splitLine -> splitLine.length >= 3).forEach(splitLine -> {
                try {
                    // Try parse the reason string to the correct type. If this succeeds, the line was succesfully parsed,
                    // so we add it to the history.
                    final SkippedRecipientReason reason = SkippedRecipientReason.valueOf(splitLine[2]);
                    final Tuple<String, String> telegramIdAndRecipient = new Tuple<>(splitLine[0], splitLine[1]);
                    history.put(telegramIdAndRecipient, reason);
                } catch (IllegalArgumentException | NullPointerException ex) {
                    // Failed to parse the reason. We simply skip this line.
                }
            });
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
}
