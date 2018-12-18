package com.github.agadar.telegrammer.client.runnable;

import com.github.agadar.telegrammer.client.form.NSTelegramForm;
import com.github.agadar.telegrammer.client.form.NSTelegramForm.Status;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.listbuilder.RecipientsListBuilder;

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
    private final RecipientsListBuilder recipientsListBuilder;

    public RefreshFilterRunnable(NSTelegramForm form, RecipientsFilter filter,
            RecipientsListBuilder recipientsListBuilder) {
        this.form = form;
        this.filter = filter;
        this.recipientsListBuilder = recipientsListBuilder;
    }

    @Override
    public void run() {

        invokeOnGuiThread(() -> {
            form.TextAreaOutput.setText("updating recipient list...\n"); // Inform user, as this might take a while.
            form.updateGui(Status.CompilingRecipients);
        });

        try {
            filter.refreshFilter();

        } catch (Exception | OutOfMemoryError ex) {
            invokeOnGuiThread(() -> {
                form.TextAreaOutput.setText("");
                form.printFailedFilterRefreshToOutput(filter, ex);
            });
            return;

        } finally {
            reenableButtons();
        }

        recipientsListBuilder.addFilter(filter);
        invokeOnGuiThread(() -> {
            form.TextFieldFilterValues.setText("");
            addFilterToGuiFilterList();
            form.TextAreaOutput.setText(form.duration());
        });
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
