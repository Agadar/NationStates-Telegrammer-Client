package com.github.agadar.telegrammer.core.manager;

import com.github.agadar.telegrammer.core.enums.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.util.Tuple;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Assists in saving and loading the history file for this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class HistoryManager {

    /**
     * The singleton.
     */
    private static HistoryManager INSTANCE;

    /**
     * Default file name.
     */
    private static final Path HISTORY_FILE = Paths.get(".nationstates-telegrammer.history");

    /**
     * Default split string.
     */
    private static final String SPLITSTRING = ",";

    /**
     * The history data retrieved from and saved to the file.
     */
    private Map<Tuple<String, String>, SkippedRecipientReason> history;

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
     * Gets the SkippedRecipientReason mapped to the given telegramId and
     * recipient.
     *
     * @param telegramId
     * @param recipient
     * @return The SkippedRecipientReason mapped to the given telegramId and
     * recipient, otherwise null.
     */
    public SkippedRecipientReason getSkippedRecipientReason(String telegramId, String recipient) {
        return history.get(new Tuple(telegramId, recipient));
    }

    /**
     * Saves a new entry in the telegram history and persists it to the history
     * file.
     *
     * @param telegramId Id of the sent telegram.
     * @param recipient Recipient of the sent telegram.
     * @param reason The reason for storing in history.
     * @return True if successful, otherwise false.
     */
    public boolean saveHistory(String telegramId, String recipient, SkippedRecipientReason reason) {
        // If the history is null, instantiate it.
        if (history == null) {
            history = new HashMap<>();
        }

        // Add the new entry to the history.
        history.put(new Tuple(telegramId, recipient), reason);

        // Make sure the history file exists. If not, create it.
        if (!Files.exists(HISTORY_FILE)) {
            try {
                Files.createFile(HISTORY_FILE);
            } catch (IOException ex) {
                return false;
            }
        }

        // Persist the new entry to the history file.
        final String entry = telegramId + SPLITSTRING + recipient + SPLITSTRING
                + reason.name() + System.lineSeparator();
        try {
            Files.write(HISTORY_FILE, entry.getBytes(), StandardOpenOption.APPEND);
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
        try (final Stream<String> lines = Files.lines(HISTORY_FILE, Charset.defaultCharset())) {
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
