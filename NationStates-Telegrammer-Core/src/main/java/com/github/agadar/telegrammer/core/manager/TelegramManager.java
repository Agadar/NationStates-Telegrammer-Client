package com.github.agadar.telegrammer.core.manager;

import com.github.agadar.nationstates.NationStates;

import com.github.agadar.telegrammer.core.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.filter.abstractfilter.Filter;
import com.github.agadar.telegrammer.core.runnable.SendTelegramsRunnable;
import com.github.agadar.telegrammer.core.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Manages the recipients list and sending telegrams to the former.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class TelegramManager {

    private static TelegramManager INSTANCE;
    private final static String USER_AGENT = "Agadar's Telegrammer using Client "
            + "Key '%s' (https://github.com/Agadar/NationStates-Telegrammer)";  // User agent string for formatting.
    private final static int NO_ADDRESSEES_FOUND_TIMEOUT = 60000;               // Duration in milliseconds for timeout when no recipients were found while looping.

    private final List<Filter> filters = new ArrayList<>();                 // The filters to apply, in chronological order.
    private final Set<String> recipients = new HashSet<>();                 // Supposedly most up-to-date recipients list, based on Filters.
    private final Set<TelegramManagerListener> listeners = new HashSet<>(); // Listeners to events thrown by this.

    private Thread telegramThread;                                          // The thread on which the TelegramQuery is running.

    public static TelegramManager get() {
        if (INSTANCE == null) {
            INSTANCE = new TelegramManager();
        }
        return INSTANCE;
    }

    private TelegramManager() {
    }

    /**
     * Returns true if none of the filters can retrieve any new nations by more
     * calls to refresh().
     *
     * @return
     */
    public boolean cantRetrieveMoreNations() {
        return filters.stream().noneMatch((filter) -> (!filter.cantRetrieveMoreNations()));
    }

    /**
     * Returns true if at least one of the filters is potentially infinite.
     *
     * @return
     */
    public boolean potentiallyInfinite() {
        return filters.stream().anyMatch(filter -> filter.potentiallyInfinite());
    }

    /**
     * Adds new filter. Assumes it hasn't been refreshed yet.
     *
     * @param filter
     */
    public void addFilter(Filter filter) {
        filter.refresh();
        filter.applyFilter(recipients);
        filters.add(filter);
    }

    /**
     * Gives the number of recipients.
     *
     * @return
     */
    public int numberOfRecipients() {
        return recipients.size();
    }

    /**
     * Gets a copy of the Recipients.
     *
     * @return
     */
    public Set<String> getRecipients() {
        return new HashSet<>(recipients);
    }

    /**
     * Resets and reapplies all filters to the address list.
     */
    public void resetAndReapplyFilters() {
        recipients.clear();
        filters.forEach((filter) -> {
            filter.reset();
            //filter.refresh();
            filter.applyFilter(recipients);
        });
    }

    /**
     * Refreshes and reapplies all filters to the address list.
     */
    public void refreshAndReapplyFilters() {
        recipients.clear();
        filters.forEach((filter) -> {
            filter.refresh();
            filter.applyFilter(recipients);
        });
    }

    /**
     * Removes the filter with the given index.
     *
     * @param index
     */
    public void removeFilterAt(int index) {
        filters.remove(index);
        recipients.clear();
        filters.forEach((filter) -> {
            filter.reset();
            filter.applyFilter(recipients);
        });
    }

    /**
     * Starts sending the telegram to the recipients in a new thread. Throws
     * IllegalArgumentException if the variables are not properly set.
     *
     */
    public void startSending() {
        PropertiesManager propsManager = PropertiesManager.get();

        // Make sure all inputs are valid.
        if (propsManager.clientKey == null || propsManager.clientKey.isEmpty()) {
            throw new IllegalArgumentException("Please supply a Client Key!");
        }
        if (propsManager.telegramId == null || propsManager.telegramId.isEmpty()) {
            throw new IllegalArgumentException("Please supply a Telegram Id!");
        }
        if (propsManager.secretKey == null || propsManager.secretKey.isEmpty()) {
            throw new IllegalArgumentException("Please supply a Secret Key!");
        }

        // Check to make sure the thread is not already running to prevent synchronization issues.
        if (telegramThread != null && telegramThread.isAlive()) {
            throw new IllegalThreadStateException("Telegram thread already running!");
        }

        //refreshFilters(true);   // Refresh filters one last time before checking # of recipients
        if (numberOfRecipients() == 0 && cantRetrieveMoreNations()) {
            throw new IllegalArgumentException("Please supply at least one recipient!");
        }

        // removeOldRecipients(true);  // Remove old recipients.
        NationStates.setUserAgent(String.format(USER_AGENT, propsManager.clientKey)); // Update user agent.

        // Prepare thread, then run it.
        telegramThread = new Thread(new SendTelegramsRunnable(this, recipients, listeners,
                NO_ADDRESSEES_FOUND_TIMEOUT, propsManager));
        telegramThread.start();
    }

    /**
     * Stops sending the telegram to the recipients.
     */
    public void stopSending() {
        if (telegramThread != null) {
            telegramThread.interrupt();
        }
    }

    /**
     * Removes invalid recipients from the supplied set.
     *
     * @param nations
     */
    public void removeOldRecipients(Set<String> nations) {
        for (final Iterator<String> it = nations.iterator(); it.hasNext();) {
            if (HistoryManager.get().history.get(new Tuple(PropertiesManager.get().telegramId, it.next())) != null) {
                it.remove();   // Remove recipient
            }
        }
    }

    /**
     * Registers new telegram manager listeners.
     *
     * @param newlisteners the listeners to register
     */
    public void addListeners(TelegramManagerListener... newlisteners) {
        synchronized (listeners) {
            listeners.addAll(Arrays.asList(newlisteners));
        }
    }
}
