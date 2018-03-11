package com.github.agadar.telegrammer.client.form;

import java.awt.event.ItemEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

import com.github.agadar.nationstates.event.TelegramSentEvent;
import com.github.agadar.telegrammer.client.properties.TelegrammerClientProperties;
import com.github.agadar.telegrammer.client.runnable.RefreshFilterRunnable;
import com.github.agadar.telegrammer.core.properties.manager.IPropertiesManager;
import com.github.agadar.telegrammer.core.recipients.RecipientsProviderType;
import com.github.agadar.telegrammer.core.recipients.filter.IRecipientsFilter;
import com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType;
import com.github.agadar.telegrammer.core.recipients.translator.IRecipientsFilterTranslator;
import com.github.agadar.telegrammer.core.telegram.TelegramType;
import com.github.agadar.telegrammer.core.telegram.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.telegram.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.telegram.event.RecipientsRefreshedEvent;
import com.github.agadar.telegrammer.core.telegram.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.telegram.event.TelegramManagerListener;
import com.github.agadar.telegrammer.core.telegram.sender.ITelegramSender;
import com.github.agadar.telegrammer.core.util.StringFunctions;
import java.awt.event.ItemListener;

/**
 * The main GUI of this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class NSTelegramForm extends javax.swing.JFrame implements TelegramManagerListener {

    /**
     * Enumerator for different states.
     */
    public enum Status {
	CompilingRecipients, Idle, SendingTelegrams;
    }

    public final static String FORM_TITLE = "Agadar's NationStates Telegrammer Client 1.6.0";

    private final static String BORDER = "------------------------------------------";

    private Thread compileRecipientsWorker;
    private final IRecipientsFilterTranslator filterTranslator;
    private final TelegrammerClientProperties properties;
    private final IPropertiesManager propertiesManager;
    private final ITelegramSender telegramSender;

    // GUI elements.
    public javax.swing.JTextArea TextAreaOutput;
    public javax.swing.JList<String> JListFilters;
    private javax.swing.JButton BtnStart;
    private javax.swing.JButton BtnClearOutput;
    private javax.swing.JButton BtnStop;
    private javax.swing.JButton ButtonAddFilter;
    private javax.swing.JButton ButtonRemoveFilter;
    private JCheckBoxMenuItem chckbxmntmHideSkippedRecipients;
    private JCheckBoxMenuItem chckbxmntmRunIndefinitely;
    private JCheckBoxMenuItem chckbxmntmStartSendingOn;
    private javax.swing.JComboBox<com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType> ComboBoxFilterType;
    private javax.swing.JComboBox<com.github.agadar.telegrammer.core.recipients.RecipientsProviderType> ComboBoxProviderType;
    private javax.swing.JComboBox<TelegramType> ComboBoxTelegramType;
    private javax.swing.JLabel LabelClientKey;
    private javax.swing.JLabel LabelRegionFrom;
    private javax.swing.JLabel LabelSecretKey;
    private javax.swing.JLabel LabelTelegramId;
    private javax.swing.JLabel LabelTelegramType;
    private JMenuBar menuBar;
    private JMenu mnNewMenu;
    private javax.swing.JPanel PanelActions;
    private javax.swing.JPanel PanelFilters;
    private javax.swing.JPanel PanelOutput;
    private javax.swing.JPanel PanelTelegram;
    private javax.swing.JScrollPane ScrollPaneFilters;
    private javax.swing.JScrollPane ScrollPaneOutput;
    private com.github.agadar.telegrammer.client.form.HintTextField TextFieldFilterValues;
    private javax.swing.JTextField TxtFieldClientKey;
    private javax.swing.JTextField TxtFieldRegionFrom;
    private javax.swing.JTextField TxtFieldSecretKey;
    private javax.swing.JTextField TxtFieldTelegramId;
    private JCheckBoxMenuItem chckbxmntmStartMinimized;
    private JCheckBoxMenuItem chckbxmntmRefreshRecipientsAfter;

    public NSTelegramForm(ITelegramSender telegramSender, IPropertiesManager propertiesManager,
            TelegrammerClientProperties properties, IRecipientsFilterTranslator filterTranslator) {
	initComponents();

	this.telegramSender = telegramSender;
	this.propertiesManager = propertiesManager;
	this.filterTranslator = filterTranslator;
	this.properties = properties;

	// Sets the output textarea such that it auto-scrolls down.
	((DefaultCaret) TextAreaOutput.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

	// Set fields according to values retrieved from properties file.
	TxtFieldClientKey.setText(properties.clientKey);
	TxtFieldTelegramId.setText(properties.telegramId);
	TxtFieldSecretKey.setText(properties.secretKey);
	ComboBoxTelegramType.setSelectedItem(properties.lastTelegramType);
	TxtFieldRegionFrom.setText(properties.fromRegion);
	chckbxmntmRunIndefinitely.setSelected(properties.runIndefinitely);
	chckbxmntmHideSkippedRecipients.setSelected(properties.hideSkippedRecipients);
	chckbxmntmStartSendingOn.setSelected(properties.startSendingOnStartup);
	chckbxmntmStartMinimized.setSelected(properties.startMinimized);
	chckbxmntmRefreshRecipientsAfter.setSelected(properties.updateRecipientsAfterEveryTelegram);
	final DefaultListModel filtersModel = (DefaultListModel) this.JListFilters.getModel();
	properties.recipientsListBuilder.getFilters().forEach(filter -> {
	    filtersModel.addElement(filter.toString());
	});

	// Set hint properly.
	setInputHint((RecipientsProviderType) ComboBoxProviderType.getSelectedItem());

	// Start sending telegrams right away if so configured.
	if (!this.properties.startSendingOnStartup) {
	    updateGui(Status.Idle); // Update entire GUI in case we missed something in visual designer.
	    TextAreaOutput.setText(duration()); // Set output textarea, for consistency's sake.
	} else {
	    this.startSendingTelegrams();
	}

	// Minimize if so configured.
	if (this.properties.startMinimized) {
	    this.setExtendedState(java.awt.Frame.ICONIFIED);
	}
    }

    /**
     * Called when the 'clear output' button has been clicked.
     *
     * @param evt
     */
    private void BtnClearOutputActionPerformed(java.awt.event.ActionEvent evt) {
	TextAreaOutput.setText("");
    }

    /**
     * Called by the Start button. Sets the GUI components properly and tells the
     * TelegramManager to start sending.
     *
     * @param evt
     */
    private void BtnStartActionPerformed(java.awt.event.ActionEvent evt) {
	startSendingTelegrams();
    }

    private void startSendingTelegrams() {

	// Make sure there are no spaces in the fields.
	TxtFieldRegionFrom.setText(properties.fromRegion.trim());
	TxtFieldClientKey.setText(properties.clientKey.replace(" ", ""));
	TxtFieldSecretKey.setText(properties.secretKey.replace(" ", ""));
	TxtFieldTelegramId.setText(properties.telegramId.replace(" ", ""));

	updateGui(Status.SendingTelegrams);
	TextAreaOutput.setText(duration());

	try {
	    telegramSender.startSending(properties.recipientsListBuilder); // start sending telegrams
	} catch (Exception ex) {
	    // if something went wrong while starting sending telegrams, reset GUI
	    TextAreaOutput.setText(ex.getMessage() + "\n");
	    updateGui(Status.Idle);
	}
    }

    /**
     * Called by the Stop button. Sets the GUI components properly, and tells the
     * telegram manager to stop sending.
     *
     * @param evt
     */
    private void BtnStopActionPerformed(java.awt.event.ActionEvent evt) {
	telegramSender.stopSending();
    }

    /**
     * Called when the 'add addressee' button was clicked. Retrieves from the server
     * the nation names corresponding to the addressees to add (if applicable),
     * tells the telegram manager to add these to its sending list, and updates the
     * GUI to reflect the added addressees.
     *
     * @param evt
     */
    private void ButtonAddFilterActionPerformed(java.awt.event.ActionEvent evt) {
	TextAreaOutput.setText("updating recipient list...\n"); // Inform user, as this might take a while.
	updateGui(Status.CompilingRecipients);
	final HashSet<String> filterValues = StringFunctions.stringToHashSet(TextFieldFilterValues.getText());
	TextFieldFilterValues.setText("");
	final RecipientsProviderType providerType = (RecipientsProviderType) ComboBoxProviderType.getSelectedItem();
	final RecipientsFilterType filterType = (RecipientsFilterType) ComboBoxFilterType.getSelectedItem();
	final IRecipientsFilter filter = filterTranslator.toFilter(filterType, providerType, filterValues);

	// Check to make sure the thread is not already running to prevent
	// synchronization issues.
	if (compileRecipientsWorker != null && compileRecipientsWorker.isAlive()) {
	    TextAreaOutput.setText("Compile recipient list thread already running!\n");
	    return;
	}

	// Prepare thread, then run it.
	properties.recipientsListBuilder.addFilter(filter);
	compileRecipientsWorker = new Thread(new RefreshFilterRunnable(this, filter));
	compileRecipientsWorker.start();
    }

    /**
     * Called when the 'remove addressee' button was clicked. Tells the telegram
     * manager to remove the selected addressees, and updates the GUI to reflect the
     * change.
     *
     * @param evt
     */
    private void ButtonRemoveFilterActionPerformed(java.awt.event.ActionEvent evt) {
	// Retrieve selected index, remove filter from telegram manager.
	int index = JListFilters.getSelectedIndex();
	properties.recipientsListBuilder.removeFilterAt(index);

	// Remove filter from GUI, try select preceding filter.
	((DefaultListModel) JListFilters.getModel()).remove(index);
	JListFilters.setSelectedIndex(Math.max(0, --index));

	// Update rest of GUI.
	ButtonRemoveFilter.setEnabled(!JListFilters.isSelectionEmpty());
	TextAreaOutput.setText(duration());
    }

    private void chckbxmntmStartMinimizedOnItemStateChanged(ItemEvent evt) {
	properties.startMinimized = chckbxmntmStartMinimized.isSelected();

    }

    private void chckbxmntmRunIndefinitelyItemStateChanged(ItemEvent evt) {
	properties.runIndefinitely = chckbxmntmRunIndefinitely.isSelected();
    }

    private void chckbxmntmHideSkippedRecipientsItemStateChanged(ItemEvent evt) {
	properties.hideSkippedRecipients = chckbxmntmHideSkippedRecipients.isSelected();
    }

    private void chckbxmntmStartSendingOnItemStateChanged(java.awt.event.ItemEvent evt) {
	properties.startSendingOnStartup = chckbxmntmStartSendingOn.isSelected();
    }

    private void chckbxmntmRefreshRecipientsAfterItemStateChanged(ItemEvent e) {
	properties.updateRecipientsAfterEveryTelegram = chckbxmntmRefreshRecipientsAfter.isSelected();
    }

    /**
     * Called when an item in the filter-type combo box has been selected. Properly
     * enables or disables the textfield for nation/region names.
     *
     * @param evt
     */
    private void ComboBoxProviderTypeItemStateChanged(java.awt.event.ItemEvent evt) {
	// Only run this code if something was SELECTED.
	if (evt.getStateChange() != ItemEvent.SELECTED) {
	    return;
	}

	setInputHint((RecipientsProviderType) evt.getItem()); // Set the tooltip.
	TextFieldFilterValues.setText(""); // Clear the textfield in question.
	setFilterComboBoxEnabled((RecipientsProviderType) evt.getItem(), Status.Idle);
    }

    /**
     * Called when the value of the telegram type combo box has changed.
     *
     * @param evt
     */
    private void ComboBoxTelegramTypeItemStateChanged(java.awt.event.ItemEvent evt) {
	// Only run this code if something was SELECTED.
	if (evt.getStateChange() != ItemEvent.SELECTED) {
	    return;
	}

	// Enable or disable TxtFieldRegionFrom.
	final TelegramType selected = (TelegramType) evt.getItem();
	setFromRegionTextAndEnabled(selected, Status.Idle);
	properties.lastTelegramType = selected;

	TextAreaOutput.setText(duration()); // Print new duration to output textarea.
    }

    /**
     * Calculates the estimated duration of sending all the telegrams, and returns
     * it in a formatted string.
     *
     * @return
     */
    public final String duration() {
	final Set<String> recipients = properties.recipientsListBuilder.getRecipients();
	int estimatedDuration = Math.max(recipients.size() - 1, 0)
	        * ((ComboBoxTelegramType.getSelectedItem() == TelegramType.RECRUITMENT ? 180050 : 30050) / 1000);
	int hours = estimatedDuration / 3600;
	int minutes = estimatedDuration % 3600 / 60;
	int seconds = estimatedDuration % 3600 % 60;
	return String.format(BORDER + "%naddressees selected: %s%nestimated duration: "
	        + "%s hours, %s minutes, %s seconds%n" + BORDER + "%n", recipients.size(), hours, minutes, seconds);

    }

    /**
     * Called when anywhere in the form was clicked. Used for de-selecting an
     * addressee in the addressees list and disabling the remove-button.
     *
     * @param evt
     */
    private void formMouseClicked(java.awt.event.MouseEvent evt) {
	JListFilters.clearSelection();
	ButtonRemoveFilter.setEnabled(false);
    }

    /**
     * Called when the application is closing. Makes sure the properties file is
     * updated with the new values in the textboxes.
     *
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {
	// Store relevant variables to properties and history files.
	propertiesManager.saveProperties(properties);
    }

    @Override
    public void handleNoRecipientsFound(NoRecipientsFoundEvent event) {
	SwingUtilities.invokeLater(() -> {
	    printToOutput("no new recipients found, timing out for " + event.timeOut / 1000 + " seconds...", false);
	});
    }

    @Override
    public void handleRecipientRemoved(RecipientRemovedEvent event) {
	if (!this.properties.hideSkippedRecipients) {
	    SwingUtilities.invokeLater(() -> {
		printToOutput("skipping recipient '" + event.recipient + "': " + event.reason, false);
	    });
	}
    }

    @Override
    public void handleRecipientsRefreshed(RecipientsRefreshedEvent event) {
	SwingUtilities.invokeLater(() -> {
	    printToOutput("updating recipients list...", false);
	});
    }

    @Override
    public void handleStoppedSending(StoppedSendingEvent event) {
	SwingUtilities.invokeLater(() -> {
	    updateGui(Status.Idle);
	    final String message = BORDER + "\nfinished"
	            + (event.causedByError ? " with error: " + event.errorMsg + "\n" : " without fatal errors\n")
	            + "telegrams queued: " + event.queuedSucces + "\n" + "blocked by category: "
	            + event.recipientIsBlocking + "\n" + "recipients not found: " + event.recipientDidntExist + "\n"
	            + "failed b/c other reasons: " + event.disconnectOrOtherReason + "\n" + BORDER + "\n";
	    TextAreaOutput.append(message);
	});
    }

    @Override
    public void handleTelegramSent(TelegramSentEvent event) {
	// Print info to output.
	SwingUtilities.invokeLater(() -> {
	    if (event.queued) {
		printToOutput("queued telegram for '" + event.recipient + "'", false);
	    } else {
		printToOutput("failed to queue telegram for '" + event.recipient + "':\n" + event.errorMessage, false);
	    }
	});
    }

    /**
     * Updates the GUI according to the current status.
     *
     * @param status
     */
    public void updateGui(Status status) {
	BtnStart.setEnabled(status == Status.Idle);
	JListFilters.setEnabled(status == Status.Idle);
	ButtonAddFilter.setEnabled(status == Status.Idle);
	TxtFieldClientKey.setEditable(status == Status.Idle);
	TxtFieldTelegramId.setEditable(status == Status.Idle);
	TxtFieldSecretKey.setEditable(status == Status.Idle);
	TextFieldFilterValues.setEditable(status == Status.Idle);
	ComboBoxTelegramType.setEnabled(status == Status.Idle);
	chckbxmntmRunIndefinitely.setEnabled(status == Status.Idle);
	chckbxmntmHideSkippedRecipients.setEnabled(status == Status.Idle);
	chckbxmntmStartSendingOn.setEnabled(status == Status.Idle);
	chckbxmntmStartMinimized.setEnabled(status == Status.Idle);
	chckbxmntmRefreshRecipientsAfter.setEnabled(status == Status.Idle);
	ComboBoxFilterType.setEnabled(status == Status.Idle);
	ComboBoxProviderType.setEnabled(status == Status.Idle);
	BtnStop.setEnabled(status == Status.SendingTelegrams);
	ButtonRemoveFilter.setEnabled(status == Status.Idle && JListFilters.getSelectedValue() != null);
	setFilterComboBoxEnabled((RecipientsProviderType) ComboBoxProviderType.getSelectedItem(), status);
	setFromRegionTextAndEnabled((TelegramType) ComboBoxTelegramType.getSelectedItem(), status);
    }

    private void initComponents() {
	new javax.swing.ButtonGroup();
	PanelTelegram = new javax.swing.JPanel();
	LabelTelegramId = new javax.swing.JLabel();
	TxtFieldTelegramId = new javax.swing.JTextField();
	LabelSecretKey = new javax.swing.JLabel();
	TxtFieldSecretKey = new javax.swing.JTextField();
	LabelClientKey = new javax.swing.JLabel();
	LabelTelegramType = new javax.swing.JLabel();
	TxtFieldClientKey = new javax.swing.JTextField();
	TxtFieldRegionFrom = new javax.swing.JTextField();
	LabelRegionFrom = new javax.swing.JLabel();
	ComboBoxTelegramType = new javax.swing.JComboBox<>();
	PanelFilters = new javax.swing.JPanel();
	ScrollPaneFilters = new javax.swing.JScrollPane();
	JListFilters = new javax.swing.JList<>();
	ButtonRemoveFilter = new javax.swing.JButton();
	ComboBoxProviderType = new javax.swing.JComboBox<>();
	ButtonAddFilter = new javax.swing.JButton();
	TextFieldFilterValues = new com.github.agadar.telegrammer.client.form.HintTextField();
	ComboBoxFilterType = new javax.swing.JComboBox<>();
	PanelOutput = new javax.swing.JPanel();
	ScrollPaneOutput = new javax.swing.JScrollPane();
	TextAreaOutput = new javax.swing.JTextArea();
	PanelActions = new javax.swing.JPanel();
	BtnStart = new javax.swing.JButton();
	BtnStop = new javax.swing.JButton();
	BtnClearOutput = new javax.swing.JButton();

	setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	setTitle(NSTelegramForm.FORM_TITLE);
	setName("NSTelegramFrame"); // NOI18N
	setResizable(false);
	addMouseListener(new java.awt.event.MouseAdapter() {
	    public void mouseClicked(java.awt.event.MouseEvent evt) {
		formMouseClicked(evt);
	    }
	});
	addWindowListener(new java.awt.event.WindowAdapter() {
	    public void windowClosing(java.awt.event.WindowEvent evt) {
		formWindowClosing(evt);
	    }
	});

	PanelTelegram.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Telegram",
	        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
	        new java.awt.Font("Tahoma", 1, 11))); // NOI18N

	LabelTelegramId.setLabelFor(TxtFieldTelegramId);
	LabelTelegramId.setText("Telegram Id:");
	LabelTelegramId.setName("LabelTelegramId"); // NOI18N

	TxtFieldTelegramId.setName("TxtFieldTelegramId"); // NOI18N
	TxtFieldTelegramId.addKeyListener(new java.awt.event.KeyAdapter() {
	    public void keyReleased(java.awt.event.KeyEvent evt) {
		TxtFieldTelegramIdKeyReleased(evt);
	    }
	});

	LabelSecretKey.setLabelFor(TxtFieldSecretKey);
	LabelSecretKey.setText("Secret Key:");
	LabelSecretKey.setName("LabelSecretKey"); // NOI18N

	TxtFieldSecretKey.setName("TxtFieldSecretKey"); // NOI18N
	TxtFieldSecretKey.addKeyListener(new java.awt.event.KeyAdapter() {
	    public void keyReleased(java.awt.event.KeyEvent evt) {
		TxtFieldSecretKeyKeyReleased(evt);
	    }
	});

	LabelClientKey.setLabelFor(TxtFieldClientKey);
	LabelClientKey.setText("Client Key:");
	LabelClientKey.setName("LabelClientKey"); // NOI18N

	LabelTelegramType.setText("Type:");
	LabelTelegramType.setName("LabelTelegramType"); // NOI18N

	TxtFieldClientKey.setName("TxtFieldClientKey"); // NOI18N
	TxtFieldClientKey.addKeyListener(new java.awt.event.KeyAdapter() {
	    public void keyReleased(java.awt.event.KeyEvent evt) {
		TxtFieldClientKeyKeyReleased(evt);
	    }
	});

	TxtFieldRegionFrom.setEditable(false);
	TxtFieldRegionFrom.setName("TxtFieldSecretKey"); // NOI18N
	TxtFieldRegionFrom.addKeyListener(new java.awt.event.KeyAdapter() {
	    public void keyReleased(java.awt.event.KeyEvent evt) {
		TxtFieldRegionFromKeyReleased(evt);
	    }
	});

	LabelRegionFrom.setText("For region:");
	LabelRegionFrom.setName("LabelRecruiting"); // NOI18N

	ComboBoxTelegramType.setModel(new DefaultComboBoxModel(TelegramType.values()));
	ComboBoxTelegramType.addItemListener(new java.awt.event.ItemListener() {
	    public void itemStateChanged(java.awt.event.ItemEvent evt) {
		ComboBoxTelegramTypeItemStateChanged(evt);
	    }
	});

	javax.swing.GroupLayout PanelTelegramLayout = new javax.swing.GroupLayout(PanelTelegram);
	PanelTelegramLayout
	        .setHorizontalGroup(PanelTelegramLayout.createParallelGroup(Alignment.LEADING)
	                .addGroup(PanelTelegramLayout.createSequentialGroup().addContainerGap()
	                        .addGroup(PanelTelegramLayout.createParallelGroup(Alignment.LEADING)
	                                .addComponent(LabelRegionFrom).addComponent(LabelTelegramType)
	                                .addComponent(LabelSecretKey).addComponent(LabelTelegramId)
	                                .addComponent(LabelClientKey))
	                        .addGap(71)
	                        .addGroup(PanelTelegramLayout.createParallelGroup(Alignment.LEADING, false)
	                                .addComponent(TxtFieldRegionFrom, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE,
	                                        150, Short.MAX_VALUE)
	                                .addComponent(TxtFieldClientKey, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
	                                .addComponent(TxtFieldTelegramId, GroupLayout.DEFAULT_SIZE, 150,
	                                        Short.MAX_VALUE)
	                                .addComponent(TxtFieldSecretKey, GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
	                                .addComponent(ComboBoxTelegramType, 0, GroupLayout.DEFAULT_SIZE,
	                                        Short.MAX_VALUE))
	                        .addGap(0, 32, Short.MAX_VALUE)));
	PanelTelegramLayout
	        .setVerticalGroup(
	                PanelTelegramLayout.createParallelGroup(Alignment.LEADING)
	                        .addGroup(PanelTelegramLayout.createSequentialGroup().addContainerGap()
	                                .addGroup(PanelTelegramLayout.createParallelGroup(Alignment.BASELINE)
	                                        .addComponent(LabelClientKey).addComponent(TxtFieldClientKey,
	                                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
	                                                GroupLayout.PREFERRED_SIZE))
	                                .addPreferredGap(ComponentPlacement.UNRELATED)
	                                .addGroup(PanelTelegramLayout.createParallelGroup(Alignment.BASELINE)
	                                        .addComponent(LabelTelegramId).addComponent(TxtFieldTelegramId,
	                                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
	                                                GroupLayout.PREFERRED_SIZE))
	                                .addPreferredGap(ComponentPlacement.UNRELATED)
	                                .addGroup(PanelTelegramLayout.createParallelGroup(Alignment.BASELINE)
	                                        .addComponent(LabelSecretKey).addComponent(TxtFieldSecretKey,
	                                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
	                                                GroupLayout.PREFERRED_SIZE))
	                                .addPreferredGap(ComponentPlacement.UNRELATED)
	                                .addGroup(PanelTelegramLayout.createParallelGroup(Alignment.BASELINE)
	                                        .addComponent(LabelTelegramType).addComponent(ComboBoxTelegramType,
	                                                GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
	                                                GroupLayout.PREFERRED_SIZE))
	                                .addPreferredGap(ComponentPlacement.UNRELATED)
	                                .addGroup(PanelTelegramLayout.createParallelGroup(Alignment.BASELINE)
	                                        .addComponent(TxtFieldRegionFrom, GroupLayout.PREFERRED_SIZE,
	                                                GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                                        .addComponent(LabelRegionFrom))
	                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	PanelTelegram.setLayout(PanelTelegramLayout);

	PanelFilters.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filters",
	        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
	        new java.awt.Font("Tahoma", 1, 11))); // NOI18N
	PanelFilters.setPreferredSize(new java.awt.Dimension(289, 172));

	ScrollPaneFilters.setName("ScrollPaneFilters"); // NOI18N

	JListFilters.setModel(new DefaultListModel());
	JListFilters.setName("JListFilters"); // NOI18N
	JListFilters.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
	    public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
		JListFiltersValueChanged(evt);
	    }
	});
	ScrollPaneFilters.setViewportView(JListFilters);

	ButtonRemoveFilter.setText("Remove filter");
	ButtonRemoveFilter.setEnabled(false);
	ButtonRemoveFilter.setName("ButtonRemoveFilter"); // NOI18N
	ButtonRemoveFilter.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		ButtonRemoveFilterActionPerformed(evt);
	    }
	});

	ComboBoxProviderType.setModel(new DefaultComboBoxModel(
	        com.github.agadar.telegrammer.core.recipients.RecipientsProviderType.values()));
	ComboBoxProviderType.setName("ComboBoxProviderType"); // NOI18N
	ComboBoxProviderType.addItemListener(new java.awt.event.ItemListener() {
	    public void itemStateChanged(java.awt.event.ItemEvent evt) {
		ComboBoxProviderTypeItemStateChanged(evt);
	    }
	});

	ButtonAddFilter.setText("Add filter");
	ButtonAddFilter.setName("ButtonAddFilter"); // NOI18N
	ButtonAddFilter.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		ButtonAddFilterActionPerformed(evt);
	    }
	});

	TextFieldFilterValues.setHint("");

	ComboBoxFilterType.setModel(new DefaultComboBoxModel(
	        com.github.agadar.telegrammer.core.recipients.filter.RecipientsFilterType.values()));
	ComboBoxFilterType.setName("ComboBoxFilterType"); // NOI18N

	javax.swing.GroupLayout PanelFiltersLayout = new javax.swing.GroupLayout(PanelFilters);
	PanelFilters.setLayout(PanelFiltersLayout);
	PanelFiltersLayout.setHorizontalGroup(PanelFiltersLayout
	        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(PanelFiltersLayout.createSequentialGroup().addContainerGap().addGroup(PanelFiltersLayout
	                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                .addComponent(TextFieldFilterValues, javax.swing.GroupLayout.DEFAULT_SIZE,
	                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(ComboBoxProviderType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(ScrollPaneFilters, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
	                .addGroup(PanelFiltersLayout.createSequentialGroup().addComponent(ButtonRemoveFilter)
	                        .addGap(18, 18, 18).addComponent(ButtonAddFilter, javax.swing.GroupLayout.DEFAULT_SIZE,
	                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addComponent(ComboBoxFilterType, javax.swing.GroupLayout.Alignment.TRAILING, 0,
	                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap()));
	PanelFiltersLayout.setVerticalGroup(PanelFiltersLayout
	        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelFiltersLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(ScrollPaneFilters, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(ComboBoxFilterType, javax.swing.GroupLayout.PREFERRED_SIZE,
	                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(ComboBoxProviderType, javax.swing.GroupLayout.PREFERRED_SIZE,
	                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(TextFieldFilterValues, javax.swing.GroupLayout.PREFERRED_SIZE,
	                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(12, 12, 12)
	                .addGroup(PanelFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                        .addComponent(ButtonRemoveFilter).addComponent(ButtonAddFilter))
	                .addContainerGap()));

	PanelOutput.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Output",
	        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
	        new java.awt.Font("Tahoma", 1, 11))); // NOI18N

	TextAreaOutput.setEditable(false);
	TextAreaOutput.setColumns(20);
	TextAreaOutput.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
	TextAreaOutput.setRows(5);
	ScrollPaneOutput.setViewportView(TextAreaOutput);

	javax.swing.GroupLayout PanelOutputLayout = new javax.swing.GroupLayout(PanelOutput);
	PanelOutput.setLayout(PanelOutputLayout);
	PanelOutputLayout.setHorizontalGroup(PanelOutputLayout
	        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(PanelOutputLayout
	                .createSequentialGroup().addContainerGap().addComponent(ScrollPaneOutput).addContainerGap()));
	PanelOutputLayout.setVerticalGroup(PanelOutputLayout
	        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(PanelOutputLayout.createSequentialGroup().addContainerGap()
	                .addComponent(ScrollPaneOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
	                .addContainerGap()));

	PanelActions.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Actions",
	        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
	        new java.awt.Font("Tahoma", 1, 11))); // NOI18N

	BtnStart.setText("Start sending");
	BtnStart.setName("ButtonRemoveAddressee"); // NOI18N
	BtnStart.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		BtnStartActionPerformed(evt);
	    }
	});

	BtnStop.setText("Stop sending");
	BtnStop.setEnabled(false);
	BtnStop.setMaximumSize(new java.awt.Dimension(97, 23));
	BtnStop.setMinimumSize(new java.awt.Dimension(97, 23));
	BtnStop.setName("ButtonRemoveAddressee"); // NOI18N
	BtnStop.setPreferredSize(new java.awt.Dimension(97, 23));
	BtnStop.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		BtnStopActionPerformed(evt);
	    }
	});

	BtnClearOutput.setText("Clear output");
	BtnClearOutput.setName("ButtonRemoveAddressee"); // NOI18N
	BtnClearOutput.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(java.awt.event.ActionEvent evt) {
		BtnClearOutputActionPerformed(evt);
	    }
	});

	menuBar = new JMenuBar();
	setJMenuBar(menuBar);

	mnNewMenu = new JMenu("Options");
	menuBar.add(mnNewMenu);

	chckbxmntmHideSkippedRecipients = new JCheckBoxMenuItem("Hide skipped recipients");
	chckbxmntmHideSkippedRecipients.addItemListener(new java.awt.event.ItemListener() {
	    public void itemStateChanged(java.awt.event.ItemEvent evt) {
		chckbxmntmHideSkippedRecipientsItemStateChanged(evt);
	    }
	});
	mnNewMenu.add(chckbxmntmHideSkippedRecipients);

	chckbxmntmRunIndefinitely = new JCheckBoxMenuItem("Run indefinitely");
	chckbxmntmRunIndefinitely.addItemListener(new java.awt.event.ItemListener() {
	    public void itemStateChanged(java.awt.event.ItemEvent evt) {
		chckbxmntmRunIndefinitelyItemStateChanged(evt);
	    }
	});

	chckbxmntmRefreshRecipientsAfter = new JCheckBoxMenuItem("Refresh recipients after every telegram");
	chckbxmntmRefreshRecipientsAfter.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		chckbxmntmRefreshRecipientsAfterItemStateChanged(e);
	    }
	});
	mnNewMenu.add(chckbxmntmRefreshRecipientsAfter);
	mnNewMenu.add(chckbxmntmRunIndefinitely);

	chckbxmntmStartSendingOn = new JCheckBoxMenuItem("Start sending on startup");
	chckbxmntmStartSendingOn.addItemListener(new java.awt.event.ItemListener() {
	    public void itemStateChanged(java.awt.event.ItemEvent evt) {
		chckbxmntmStartSendingOnItemStateChanged(evt);
	    }
	});
	mnNewMenu.add(chckbxmntmStartSendingOn);

	chckbxmntmStartMinimized = new JCheckBoxMenuItem("Start minimized");
	mnNewMenu.add(chckbxmntmStartMinimized);
	chckbxmntmStartMinimized.addItemListener(new java.awt.event.ItemListener() {
	    public void itemStateChanged(java.awt.event.ItemEvent evt) {
		chckbxmntmStartMinimizedOnItemStateChanged(evt);
	    }
	});

	javax.swing.GroupLayout PanelActionsLayout = new javax.swing.GroupLayout(PanelActions);
	PanelActions.setLayout(PanelActionsLayout);
	PanelActionsLayout
	        .setHorizontalGroup(PanelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                .addGroup(PanelActionsLayout.createSequentialGroup().addContainerGap().addComponent(BtnStart)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                        .addComponent(BtnClearOutput)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                        .addComponent(BtnStop, javax.swing.GroupLayout.PREFERRED_SIZE, 128,
	                                javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
	PanelActionsLayout.setVerticalGroup(PanelActionsLayout
	        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	        .addGroup(PanelActionsLayout.createSequentialGroup().addContainerGap()
	                .addGroup(PanelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                        .addComponent(BtnStart).addComponent(BtnClearOutput).addComponent(BtnStop,
	                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
	                                Short.MAX_VALUE))
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

	javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	layout.setHorizontalGroup(
	        layout.createParallelGroup(Alignment.LEADING)
	                .addGroup(layout.createSequentialGroup().addContainerGap()
	                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
	                                .addComponent(PanelFilters, GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
	                                .addComponent(PanelTelegram, GroupLayout.PREFERRED_SIZE,
	                                        GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	                        .addPreferredGap(ComponentPlacement.UNRELATED)
	                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
	                                .addComponent(PanelActions, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
	                                        Short.MAX_VALUE)
	                                .addComponent(PanelOutput, GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE))
	                        .addContainerGap()));
	layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING)
	        .addGroup(layout.createSequentialGroup().addContainerGap()
	                .addGroup(layout.createParallelGroup(Alignment.LEADING)
	                        .addGroup(layout.createSequentialGroup()
	                                .addComponent(PanelTelegram, GroupLayout.PREFERRED_SIZE, 202,
	                                        GroupLayout.PREFERRED_SIZE)
	                                .addPreferredGap(ComponentPlacement.RELATED)
	                                .addComponent(PanelFilters, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
	                        .addGroup(layout.createSequentialGroup()
	                                .addComponent(PanelOutput, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
	                                        GroupLayout.PREFERRED_SIZE)
	                                .addPreferredGap(ComponentPlacement.RELATED)
	                                .addComponent(PanelActions, GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)))
	                .addContainerGap()));
	layout.setAutoCreateContainerGaps(true);
	layout.setAutoCreateGaps(true);
	getContentPane().setLayout(layout);

	pack();
    }

    /**
     * Called when an item in the recipient list is selected. Enables the 'remove
     * recipient'-button.
     *
     * @param evt
     */
    private void JListFiltersValueChanged(javax.swing.event.ListSelectionEvent evt) {
	if (!evt.getValueIsAdjusting() && JListFilters.getSelectedIndex() != -1) {
	    ButtonRemoveFilter.setEnabled(true);
	}
    }

    /**
     * Utility function for printing messages to the output textarea that are
     * prefixed with a timestamp and suffixed with a newline. If called outside the
     * GUI thread, wrap this in SwingUtilities.invokeLater(...).
     *
     * @param msg
     * @param clear
     */
    public void printToOutput(String msg, boolean clear) {
	msg = "[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + msg + "\n";

	if (clear) {
	    TextAreaOutput.setText(msg);
	} else {
	    TextAreaOutput.append(msg);
	}
    }

    /**
     * Enables or disables the filters combo box according to the supplied type. If
     * the supplied type is null, then it is always disabled.
     *
     * @param type
     */
    private void setFilterComboBoxEnabled(RecipientsProviderType type, Status status) {
	// If type is null, always disable.
	if (status != Status.Idle || type == null) {
	    TextFieldFilterValues.setEditable(false);
	    return;
	}

	// Else, enable/disable according to type.
	switch (type) {
	case NATIONS_IN_EMBASSY_REGIONS:
	case NATIONS:
	case NATIONS_IN_REGIONS:
	case NATIONS_IN_REGIONS_WITH_TAGS:
	case NATIONS_IN_REGIONS_WITHOUT_TAGS:
	    TextFieldFilterValues.setEditable(true);
	    break;
	default:
	    TextFieldFilterValues.setEditable(false);
	}
    }

    /**
     * Sets the enable and text of the 'from region' textfield according to the
     * supplied telegram type.
     *
     * @param type
     */
    private void setFromRegionTextAndEnabled(TelegramType type, Status status) {
	if (status != Status.Idle) {
	    TxtFieldRegionFrom.setEditable(false);
	} else if (type == TelegramType.RECRUITMENT) {
	    TxtFieldRegionFrom.setEditable(true);
	} else {
	    TxtFieldRegionFrom.setEditable(false);
	    TxtFieldRegionFrom.setText("");
	}
    }

    /**
     * Sets the input field's tooltip.
     *
     * @param type
     */
    private void setInputHint(RecipientsProviderType type) {
	String hint = "";

	switch (type) {
	case NATIONS_IN_EMBASSY_REGIONS:
	case NATIONS_IN_REGIONS:
	    hint = "Insert region names, e.g. 'region1, region2'.";
	    break;
	case NATIONS:
	    hint = "Insert nation names, e.g. 'nation1, nation2'.";
	    break;
	case NATIONS_IN_REGIONS_WITH_TAGS:
	case NATIONS_IN_REGIONS_WITHOUT_TAGS:
	    hint = "Insert region tags, e.g. 'tag1, tag2'.";
	    break;
	default:
	    break;
	}
	this.TextFieldFilterValues.setHint(hint);
    }

    private void TxtFieldClientKeyKeyReleased(java.awt.event.KeyEvent evt) {
	properties.clientKey = TxtFieldClientKey.getText();
    }

    /**
     * Updates region from on key release.
     *
     * @param evt
     */
    private void TxtFieldRegionFromKeyReleased(java.awt.event.KeyEvent evt) {
	properties.fromRegion = TxtFieldRegionFrom.getText();
    }

    private void TxtFieldSecretKeyKeyReleased(java.awt.event.KeyEvent evt) {
	properties.secretKey = TxtFieldSecretKey.getText();
    }

    private void TxtFieldTelegramIdKeyReleased(java.awt.event.KeyEvent evt) {
	properties.telegramId = TxtFieldTelegramId.getText();

	// Update recipients list, because some recipients may be valid or invalid for
	// the new telegram id.
	TextAreaOutput.setText(duration());
    }
}
