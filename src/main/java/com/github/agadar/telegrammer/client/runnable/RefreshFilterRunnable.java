package com.github.agadar.telegrammer.client.runnable;

import com.github.agadar.telegrammer.client.form.NSTelegramForm;
import com.github.agadar.telegrammer.client.form.NSTelegramForm.Status;
import com.github.agadar.telegrammer.core.recipients.filter.IRecipientsFilter;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

/**
 * Runnable used by NSTelegramForm to add a new filter to the TelegramManager.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RefreshFilterRunnable implements Runnable {

    private final NSTelegramForm form;
    private final IRecipientsFilter recipientsFilterToRefresh;

    public RefreshFilterRunnable(NSTelegramForm form, IRecipientsFilter recipientsFilterToRefresh) {
        this.form = form;
        this.recipientsFilterToRefresh = recipientsFilterToRefresh;
    }

    @Override
    public void run() {
        try {
            recipientsFilterToRefresh.refreshFilter();
            SwingUtilities.invokeLater(() -> {
                ((DefaultListModel<String>) form.JListFilters.getModel())
                        .addElement(recipientsFilterToRefresh.toString());
                form.TextAreaOutput.setText(form.duration());
            });

        } catch (Exception | OutOfMemoryError ex) {
            // If an exception occured, print it to the output textarea.
            SwingUtilities.invokeLater(() -> {
                form.printToOutput("a fatal error occured:\n" + ex.getMessage(), false);
            });
        } finally {
            // Always re-enable the 'add filter' and 'start sending' buttons.
            SwingUtilities.invokeLater(() -> {
                form.updateGui(Status.Idle);
            });
        }
    }
}
