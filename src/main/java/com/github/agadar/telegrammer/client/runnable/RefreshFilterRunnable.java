package com.github.agadar.telegrammer.client.runnable;

import com.github.agadar.telegrammer.client.form.NSTelegramForm;
import com.github.agadar.telegrammer.client.form.NSTelegramForm.Status;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

/**
 * Runnable used by NSTelegramForm to add a new filter to the TelegramManager.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class RefreshFilterRunnable implements Runnable {

    private final NSTelegramForm form;
    private final RecipientsFilter filter;

    public RefreshFilterRunnable(NSTelegramForm form, RecipientsFilter filter) {
        this.form = form;
        this.filter = filter;
    }

    @Override
    public void run() {
        try {
            filter.refreshFilter();
            invokeOnGuiThread(() -> {
                addFilterToGuiFilterList();
                form.TextAreaOutput.setText(form.duration());
            });

        } catch (Exception | OutOfMemoryError ex) {
            invokeOnGuiThread(() -> {
                form.TextAreaOutput.setText("");
                form.printFailedFilterRefreshToOutput(filter, ex);
            });

        } finally {
            reenableButtons();
        }
    }

    private void addFilterToGuiFilterList() {
        ((DefaultListModel<String>) form.JListFilters.getModel()).addElement(filter.toString());
    }

    private void reenableButtons() {
        invokeOnGuiThread(() -> form.updateGui(Status.Idle));
    }

    private void invokeOnGuiThread(Runnable action) {
        SwingUtilities.invokeLater(action);
    }
}
