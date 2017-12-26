package com.github.agadar.telegrammer.client;

import com.github.agadar.nationstates.INationStates;
import com.github.agadar.nationstates.enumerator.RegionTag;
import com.github.agadar.nationstates.event.TelegramSentEvent;

import com.github.agadar.telegrammer.client.runnable.AddFilterRunnable;

import com.github.agadar.telegrammer.core.enums.FilterType;
import com.github.agadar.telegrammer.core.enums.TelegramType;
import com.github.agadar.telegrammer.core.event.NoRecipientsFoundEvent;
import com.github.agadar.telegrammer.core.event.RecipientRemovedEvent;
import com.github.agadar.telegrammer.core.event.RecipientsRefreshedEvent;
import com.github.agadar.telegrammer.core.event.StoppedSendingEvent;
import com.github.agadar.telegrammer.core.event.TelegramManagerListener;
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
import com.github.agadar.telegrammer.core.manager.IHistoryManager;
import com.github.agadar.telegrammer.core.manager.IPropertiesManager;
import com.github.agadar.telegrammer.core.manager.ITelegramManager;
import com.github.agadar.telegrammer.core.util.IFilterCache;
import com.github.agadar.telegrammer.core.util.StringFunctions;

import java.awt.event.ItemEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultCaret;

/**
 * The main GUI of this application.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class NSTelegramForm extends javax.swing.JFrame implements TelegramManagerListener {

    public final static String FORM_TITLE = "Agadar's NationStates Telegrammer Client 1.5.0-alpha"; // Form title.  
    private final static String BORDER = "------------------------------------------";  // Border for output text.

    private Thread CompileRecipientsWorker;  // Thread used for compiling address lists.

    private final INationStates nationStates;
    private final ITelegramManager telegramManager;
    private final IHistoryManager historyManager;
    private final IPropertiesManager propertiesManager;
    private final IFilterCache filterCache;

    /**
     * Enumerator for different states.
     */
    public enum Status {
        Idle,
        CompilingRecipients,
        SendingTelegrams;
    }

    public NSTelegramForm(INationStates nationStates, ITelegramManager telegramManager,
            IHistoryManager historyManager, IPropertiesManager propertiesManager,
            IFilterCache filterCache) {
        initComponents();

        this.nationStates = nationStates;
        this.telegramManager = telegramManager;
        this.historyManager = historyManager;
        this.propertiesManager = propertiesManager;
        this.filterCache = filterCache;

        // Sets the output textarea such that it auto-scrolls down.
        ((DefaultCaret) TextAreaOutput.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        // Set fields according to values retrieved from properties file.
        TxtFieldClientKey.setText(propertiesManager.getClientKey());
        TxtFieldTelegramId.setText(propertiesManager.getTelegramId());
        TxtFieldSecretKey.setText(propertiesManager.getSecretKey());
        ComboBoxTelegramType.setSelectedItem(propertiesManager.getLastTelegramType());
        TxtFieldRegionFrom.setText(propertiesManager.getFromRegion());
        CheckBoxDryRun.setSelected(propertiesManager.getDoDryRun());

        updateGui(Status.Idle);                   // Update entire GUI in case we missed something in visual designer.
        TextAreaOutput.setText(duration()); // Set output textarea, for consistency's sake.

        // Set hint properly.
        setInputHint((FilterType) ComboBoxFilterType.getSelectedItem());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BtnGrpTelegramType = new javax.swing.ButtonGroup();
        PanelTelegram = new javax.swing.JPanel();
        LabelTelegramId = new javax.swing.JLabel();
        TxtFieldTelegramId = new javax.swing.JTextField();
        LabelSecretKey = new javax.swing.JLabel();
        TxtFieldSecretKey = new javax.swing.JTextField();
        LabelClientKey = new javax.swing.JLabel();
        LabelTelegramType = new javax.swing.JLabel();
        TxtFieldClientKey = new javax.swing.JTextField();
        LabelDryRun = new javax.swing.JLabel();
        CheckBoxDryRun = new javax.swing.JCheckBox();
        TxtFieldRegionFrom = new javax.swing.JTextField();
        LabelRegionFrom = new javax.swing.JLabel();
        ComboBoxTelegramType = new javax.swing.JComboBox<>();
        PanelFilters = new javax.swing.JPanel();
        ScrollPaneFilters = new javax.swing.JScrollPane();
        JListFilters = new javax.swing.JList<>();
        ButtonRemoveFilter = new javax.swing.JButton();
        ComboBoxFilterType = new javax.swing.JComboBox<>();
        ButtonAddFilter = new javax.swing.JButton();
        TextFieldFilterValues = new com.github.agadar.telegrammer.client.HintTextField();
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

        PanelTelegram.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Telegram", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

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

        LabelDryRun.setText("Dry Run:");
        LabelDryRun.setName("LabelSendAs"); // NOI18N

        CheckBoxDryRun.setText(" ");
        CheckBoxDryRun.setFocusPainted(false);
        CheckBoxDryRun.setMargin(new java.awt.Insets(0, -1, 0, 2));
        CheckBoxDryRun.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                CheckBoxDryRunItemStateChanged(evt);
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
        PanelTelegram.setLayout(PanelTelegramLayout);
        PanelTelegramLayout.setHorizontalGroup(
            PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTelegramLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LabelTelegramType)
                            .addComponent(LabelSecretKey)
                            .addComponent(LabelTelegramId)
                            .addComponent(LabelClientKey))
                        .addComponent(LabelDryRun, javax.swing.GroupLayout.Alignment.LEADING))
                    .addComponent(LabelRegionFrom))
                .addGap(52, 52, 52)
                .addGroup(PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(TxtFieldRegionFrom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                        .addComponent(TxtFieldClientKey, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                        .addComponent(TxtFieldTelegramId, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                        .addComponent(TxtFieldSecretKey, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                        .addComponent(ComboBoxTelegramType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(CheckBoxDryRun))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        PanelTelegramLayout.setVerticalGroup(
            PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTelegramLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelClientKey)
                    .addComponent(TxtFieldClientKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelTelegramId)
                    .addComponent(TxtFieldTelegramId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelSecretKey)
                    .addComponent(TxtFieldSecretKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LabelTelegramType)
                    .addComponent(ComboBoxTelegramType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TxtFieldRegionFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LabelRegionFrom))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelTelegramLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CheckBoxDryRun)
                    .addComponent(LabelDryRun))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelFilters.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Filters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
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

        ComboBoxFilterType.setModel(new DefaultComboBoxModel(FilterType.values()));
        ComboBoxFilterType.setName("ComboBoxFilterType"); // NOI18N
        ComboBoxFilterType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboBoxFilterTypeItemStateChanged(evt);
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

        javax.swing.GroupLayout PanelFiltersLayout = new javax.swing.GroupLayout(PanelFilters);
        PanelFilters.setLayout(PanelFiltersLayout);
        PanelFiltersLayout.setHorizontalGroup(
            PanelFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelFiltersLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TextFieldFilterValues, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ComboBoxFilterType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ScrollPaneFilters, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                    .addGroup(PanelFiltersLayout.createSequentialGroup()
                        .addComponent(ButtonRemoveFilter)
                        .addGap(18, 18, 18)
                        .addComponent(ButtonAddFilter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        PanelFiltersLayout.setVerticalGroup(
            PanelFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelFiltersLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ScrollPaneFilters, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ComboBoxFilterType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TextFieldFilterValues, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(PanelFiltersLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ButtonRemoveFilter)
                    .addComponent(ButtonAddFilter))
                .addContainerGap())
        );

        PanelOutput.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Output", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        TextAreaOutput.setEditable(false);
        TextAreaOutput.setColumns(20);
        TextAreaOutput.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TextAreaOutput.setRows(5);
        ScrollPaneOutput.setViewportView(TextAreaOutput);

        javax.swing.GroupLayout PanelOutputLayout = new javax.swing.GroupLayout(PanelOutput);
        PanelOutput.setLayout(PanelOutputLayout);
        PanelOutputLayout.setHorizontalGroup(
            PanelOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOutputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ScrollPaneOutput)
                .addContainerGap())
        );
        PanelOutputLayout.setVerticalGroup(
            PanelOutputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelOutputLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ScrollPaneOutput, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                .addContainerGap())
        );

        PanelActions.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Actions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

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

        javax.swing.GroupLayout PanelActionsLayout = new javax.swing.GroupLayout(PanelActions);
        PanelActions.setLayout(PanelActionsLayout);
        PanelActionsLayout.setHorizontalGroup(
            PanelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BtnStart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtnClearOutput)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtnStop, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelActionsLayout.setVerticalGroup(
            PanelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelActionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelActionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnStart)
                    .addComponent(BtnClearOutput)
                    .addComponent(BtnStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PanelTelegram, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelFilters, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PanelActions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelTelegram, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelFilters, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PanelOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelActions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Called by the Start button. Sets the GUI components properly and tells
     * the TelegramManager to start sending.
     *
     * @param evt
     */
    private void BtnStartActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BtnStartActionPerformed
    {//GEN-HEADEREND:event_BtnStartActionPerformed
        // Make sure there are no spaces in the fields.
        TxtFieldRegionFrom.setText(propertiesManager.getFromRegion().trim());
        TxtFieldClientKey.setText(propertiesManager.getClientKey().replace(" ", ""));
        TxtFieldSecretKey.setText(propertiesManager.getSecretKey().replace(" ", ""));
        TxtFieldTelegramId.setText(propertiesManager.getTelegramId().replace(" ", ""));

        updateGui(Status.SendingTelegrams);    // update GUI
        TextAreaOutput.setText(duration());

        try {
            telegramManager.startSending(true);  // start sending telegrams
        } catch (Exception ex) {
            // if something went wrong while starting sending telegrams, reset GUI
            TextAreaOutput.setText(ex.getMessage() + "\n");
            updateGui(Status.Idle);
        }
    }//GEN-LAST:event_BtnStartActionPerformed

    /**
     * Called by the Stop button. Sets the GUI components properly, and tells
     * the telegram manager to stop sending.
     *
     * @param evt
     */
    private void BtnStopActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BtnStopActionPerformed
    {//GEN-HEADEREND:event_BtnStopActionPerformed
        telegramManager.stopSending(); // Call telegram manager to stop sending.
    }//GEN-LAST:event_BtnStopActionPerformed

    /**
     * Called when an item in the filter-type combo box has been selected.
     * Properly enables or disables the textfield for nation/region names.
     *
     * @param evt
     */
    private void ComboBoxFilterTypeItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_ComboBoxFilterTypeItemStateChanged
    {//GEN-HEADEREND:event_ComboBoxFilterTypeItemStateChanged
        // Only run this code if something was SELECTED.
        if (evt.getStateChange() != ItemEvent.SELECTED) {
            return;
        }

        setInputHint((FilterType) evt.getItem()); // Set the tooltip.
        TextFieldFilterValues.setText("");  // Clear the textfield in question.
        setFilterComboBoxEnabled((FilterType) evt.getItem(), Status.Idle);
    }//GEN-LAST:event_ComboBoxFilterTypeItemStateChanged

    /**
     * Called when anywhere in the form was clicked. Used for de-selecting an
     * addressee in the addressees list and disabling the remove-button.
     *
     * @param evt
     */
    private void formMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_formMouseClicked
    {//GEN-HEADEREND:event_formMouseClicked
        JListFilters.clearSelection();
        ButtonRemoveFilter.setEnabled(false);
    }//GEN-LAST:event_formMouseClicked

    /**
     * Called when the 'add addressee' button was clicked. Retrieves from the
     * server the nation names corresponding to the addressees to add (if
     * applicable), tells the telegram manager to add these to its sending list,
     * and updates the GUI to reflect the added addressees.
     *
     * @param evt
     */
    private void ButtonAddFilterActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ButtonAddFilterActionPerformed
    {//GEN-HEADEREND:event_ButtonAddFilterActionPerformed
        TextAreaOutput.setText("compiling recipient list...\n");    // Inform user, as this might take a while.
        updateGui(Status.CompilingRecipients);
        final String filterValues = TextFieldFilterValues.getText();
        TextFieldFilterValues.setText("");

        final FilterType filter = (FilterType) ComboBoxFilterType.getSelectedItem();
        String textForList = filter.toString(); // Used for the text in the visual filter list.
        Filter f;                               // The filter to add to the telegram manager.

        // Set above variables according to addressees type selected.
        switch (filter) {
            case ALL:
                f = new FilterAll(nationStates, historyManager, filterCache);
                break;
            case DELEGATES_EXCL:
                f = new FilterDelegates(nationStates, historyManager, filterCache, false);
                break;
            case DELEGATES_INCL:
                f = new FilterDelegates(nationStates, historyManager, filterCache, true);
                break;
            case DELEGATES_NEW:
                f = new FilterDelegatesNew(nationStates, historyManager, filterCache);
                break;
            case DELEGATES_NEW_MAX: {
                int amount = StringFunctions.stringToUInt(filterValues);
                f = new FilterDelegatesNewFinite(nationStates, historyManager, filterCache, amount);
                textForList += " (" + amount + ")";
                break;
            }
            case EMBASSIES_EXCL: {
                Set<String> addressees = StringFunctions.stringToStringList(filterValues);
                f = new FilterEmbassies(nationStates, historyManager, filterCache, addressees, false);
                textForList += ": " + addressees;
                break;
            }
            case EMBASSIES_INCL: {
                Set<String> addressees = StringFunctions.stringToStringList(filterValues);
                f = new FilterEmbassies(nationStates, historyManager, filterCache, addressees, true);
                textForList += ": " + addressees;
                break;
            }
            case NATIONS_EXCL: {
                Set<String> addressees = StringFunctions.stringToStringList(filterValues);
                f = new FilterNations(nationStates, historyManager, filterCache, addressees, false);
                textForList += ": " + addressees;
                break;
            }
            case NATIONS_INCL: {
                Set<String> addressees = StringFunctions.stringToStringList(filterValues);
                f = new FilterNations(nationStates, historyManager, filterCache, addressees, true);
                textForList += ": " + addressees;
                break;
            }
            case NATIONS_NEW_MAX: {
                int amount = StringFunctions.stringToUInt(filterValues);
                f = new FilterNationsNewFinite(nationStates, historyManager, filterCache, amount);
                textForList += " (" + amount + ")";
                break;
            }
            case NATIONS_NEW:
                f = new FilterNationsNew(nationStates, historyManager, filterCache);
                break;
            case NATIONS_REFOUNDED_MAX: {
                int amount = StringFunctions.stringToUInt(filterValues);
                f = new FilterNationsRefoundedFinite(nationStates, historyManager, filterCache, amount);
                textForList += " (" + amount + ")";
                break;
            }
            case NATIONS_REFOUNDED:
                f = new FilterNationsRefounded(nationStates, historyManager, filterCache);
                break;
            case NATIONS_EJECTED_MAX: {
                int amount = StringFunctions.stringToUInt(filterValues);
                f = new FilterNationsEjectedFinite(nationStates, historyManager, filterCache, amount);
                textForList += " (" + amount + ")";
                break;
            }
            case NATIONS_EJECTED:
                f = new FilterNationsEjected(nationStates, historyManager, filterCache);
                break;
            case REGIONS_EXCL: {
                Set<String> addressees = StringFunctions.stringToStringList(filterValues);
                f = new FilterRegions(nationStates, historyManager, filterCache, addressees, false);
                textForList += ": " + addressees;
                break;
            }
            case REGIONS_INCL: {
                Set<String> addressees = StringFunctions.stringToStringList(filterValues);
                f = new FilterRegions(nationStates, historyManager, filterCache, addressees, true);
                textForList += ": " + addressees;
                break;
            }
            case REGIONS_WITH_TAGS_EXCL: {
                Set<RegionTag> recipients = StringFunctions.stringsToRegionTags(
                        StringFunctions.stringToStringList(filterValues));

                if (recipients.size() < 1) {
                    updateGui(Status.Idle);
                    TextAreaOutput.setText("No valid region tags recognized!\n");
                    return;
                }
                f = new FilterRegionsWithTags(nationStates, historyManager, filterCache, recipients, false);
                textForList += ": " + recipients;
                break;
            }
            case REGIONS_WITH_TAGS_INCL: {
                Set<RegionTag> recipients = StringFunctions.stringsToRegionTags(
                        StringFunctions.stringToStringList(filterValues));

                if (recipients.size() < 1) {
                    updateGui(Status.Idle);
                    TextAreaOutput.setText("No valid region tags recognized!\n");
                    return;
                }
                f = new FilterRegionsWithTags(nationStates, historyManager, filterCache, recipients, true);
                textForList += ": " + recipients;
                break;
            }
            case REGIONS_WO_TAGS_EXCL: {
                Set<RegionTag> recipients = StringFunctions.stringsToRegionTags(
                        StringFunctions.stringToStringList(filterValues));

                if (recipients.size() < 1) {
                    updateGui(Status.Idle);
                    TextAreaOutput.setText("No valid region tags recognized!\n");
                    return;
                }
                f = new FilterRegionsWithoutTags(nationStates, historyManager, filterCache, recipients, false);
                textForList += ": " + recipients;
                break;
            }
            case REGIONS_WO_TAGS_INCL: {
                Set<RegionTag> recipients = StringFunctions.stringsToRegionTags(
                        StringFunctions.stringToStringList(filterValues));

                if (recipients.size() < 1) {
                    updateGui(Status.Idle);
                    TextAreaOutput.setText("No valid region tags recognized!\n");
                    return;
                }
                f = new FilterRegionsWithoutTags(nationStates, historyManager, filterCache, recipients, true);
                textForList += ": " + recipients;
                break;
            }
            case WA_MEMBERS_EXCL:
                f = new FilterWAMembers(nationStates, historyManager, filterCache, false);
                break;
            case WA_MEMBERS_INCL:
                f = new FilterWAMembers(nationStates, historyManager, filterCache, true);
                break;
            case WA_MEMBERS_NEW_MAX: {
                int amount = StringFunctions.stringToUInt(filterValues);
                f = new FilterWAMembersNewFinite(nationStates, historyManager, filterCache, amount);
                textForList += " (" + amount + ")";
                break;
            }
            case WA_MEMBERS_NEW:
                f = new FilterWAMembersNew(nationStates, historyManager, filterCache);
                break;
            default:
                return;
        }

        // Check to make sure the thread is not already running to prevent synchronization issues.
        if (CompileRecipientsWorker != null && CompileRecipientsWorker.isAlive()) {
            TextAreaOutput.setText("Compile recipient list thread already running!\n");
            return;
        }

        // Prepare thread, then run it.
        CompileRecipientsWorker = new Thread(new AddFilterRunnable(this, telegramManager, f, textForList));
        CompileRecipientsWorker.start();
    }//GEN-LAST:event_ButtonAddFilterActionPerformed

    /**
     * Called when the 'remove addressee' button was clicked. Tells the telegram
     * manager to remove the selected addressees, and updates the GUI to reflect
     * the change.
     *
     * @param evt
     */
    private void ButtonRemoveFilterActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_ButtonRemoveFilterActionPerformed
    {//GEN-HEADEREND:event_ButtonRemoveFilterActionPerformed
        // Retrieve selected index, remove filter from telegram manager.
        int index = JListFilters.getSelectedIndex();
        telegramManager.removeFilterAt(index);

        // Remove filter from GUI, try select preceding filter.
        ((DefaultListModel) JListFilters.getModel()).remove(index);
        JListFilters.setSelectedIndex(Math.max(0, --index));

        // Update rest of GUI.
        ButtonRemoveFilter.setEnabled(!JListFilters.isSelectionEmpty());
        TextAreaOutput.setText(duration());
    }//GEN-LAST:event_ButtonRemoveFilterActionPerformed

    /**
     * Called when the application is closing. Makes sure the properties file is
     * updated with the new values in the textboxes.
     *
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        // Store relevant variables to properties and history files.
        propertiesManager.saveProperties();
    }//GEN-LAST:event_formWindowClosing

    /**
     * Called when an item in the recipient list is selected. Enables the
     * 'remove recipient'-button.
     *
     * @param evt
     */
    private void JListFiltersValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_JListFiltersValueChanged
    {//GEN-HEADEREND:event_JListFiltersValueChanged
        if (!evt.getValueIsAdjusting() && JListFilters.getSelectedIndex() != -1) {
            ButtonRemoveFilter.setEnabled(true);
        }
    }//GEN-LAST:event_JListFiltersValueChanged

    /**
     * Called when the 'clear output' button has been clicked.
     *
     * @param evt
     */
    private void BtnClearOutputActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BtnClearOutputActionPerformed
    {//GEN-HEADEREND:event_BtnClearOutputActionPerformed
        TextAreaOutput.setText("");
    }//GEN-LAST:event_BtnClearOutputActionPerformed

    /**
     * Called when the value of the telegram type combo box has changed.
     *
     * @param evt
     */
    private void ComboBoxTelegramTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboBoxTelegramTypeItemStateChanged
        // Only run this code if something was SELECTED.
        if (evt.getStateChange() != ItemEvent.SELECTED) {
            return;
        }

        // Enable or disable TxtFieldRegionFrom.
        final TelegramType selected = (TelegramType) evt.getItem();
        setFromRegionTextAndEnabled(selected, Status.Idle);
        propertiesManager.setLastTelegramType(selected);

        TextAreaOutput.setText(duration()); // Print new duration to output textarea.
    }//GEN-LAST:event_ComboBoxTelegramTypeItemStateChanged

    /**
     * Updates region from on key release.
     *
     * @param evt
     */
    private void TxtFieldRegionFromKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtFieldRegionFromKeyReleased
        propertiesManager.setFromRegion(TxtFieldRegionFrom.getText());
    }//GEN-LAST:event_TxtFieldRegionFromKeyReleased

    /**
     * Called when selected status of looping checkbox has changed.
     *
     * @param evt
     */
    private void CheckBoxDryRunItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_CheckBoxDryRunItemStateChanged
        propertiesManager.setDoDryRun(CheckBoxDryRun.isSelected());
    }//GEN-LAST:event_CheckBoxDryRunItemStateChanged

    private void TxtFieldClientKeyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtFieldClientKeyKeyReleased
        propertiesManager.setClientKey(TxtFieldClientKey.getText());
    }//GEN-LAST:event_TxtFieldClientKeyKeyReleased

    private void TxtFieldSecretKeyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtFieldSecretKeyKeyReleased
        propertiesManager.setSecretKey(TxtFieldSecretKey.getText());
    }//GEN-LAST:event_TxtFieldSecretKeyKeyReleased

    private void TxtFieldTelegramIdKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtFieldTelegramIdKeyReleased
        propertiesManager.setTelegramId(TxtFieldTelegramId.getText());

        // Update recipients list, because some recipients may be valid or invalid for the new telegram id.
        telegramManager.resetAndReapplyFilters();
        TextAreaOutput.setText(duration());
    }//GEN-LAST:event_TxtFieldTelegramIdKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnClearOutput;
    private javax.swing.ButtonGroup BtnGrpTelegramType;
    public javax.swing.JButton BtnStart;
    private javax.swing.JButton BtnStop;
    public javax.swing.JButton ButtonAddFilter;
    private javax.swing.JButton ButtonRemoveFilter;
    private javax.swing.JCheckBox CheckBoxDryRun;
    private javax.swing.JComboBox<FilterType> ComboBoxFilterType;
    private javax.swing.JComboBox<TelegramType> ComboBoxTelegramType;
    public javax.swing.JList<String> JListFilters;
    private javax.swing.JLabel LabelClientKey;
    private javax.swing.JLabel LabelDryRun;
    private javax.swing.JLabel LabelRegionFrom;
    private javax.swing.JLabel LabelSecretKey;
    private javax.swing.JLabel LabelTelegramId;
    private javax.swing.JLabel LabelTelegramType;
    private javax.swing.JPanel PanelActions;
    private javax.swing.JPanel PanelFilters;
    private javax.swing.JPanel PanelOutput;
    private javax.swing.JPanel PanelTelegram;
    private javax.swing.JScrollPane ScrollPaneFilters;
    private javax.swing.JScrollPane ScrollPaneOutput;
    public javax.swing.JTextArea TextAreaOutput;
    private com.github.agadar.telegrammer.client.HintTextField TextFieldFilterValues;
    private javax.swing.JTextField TxtFieldClientKey;
    private javax.swing.JTextField TxtFieldRegionFrom;
    private javax.swing.JTextField TxtFieldSecretKey;
    private javax.swing.JTextField TxtFieldTelegramId;
    // End of variables declaration//GEN-END:variables

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
        CheckBoxDryRun.setEnabled(status == Status.Idle);
        ComboBoxFilterType.setEnabled(status == Status.Idle);
        BtnStop.setEnabled(status == Status.SendingTelegrams);
        ButtonRemoveFilter.setEnabled(status == Status.Idle && JListFilters.getSelectedValue() != null);
        setFilterComboBoxEnabled((FilterType) ComboBoxFilterType.getSelectedItem(), status);
        setFromRegionTextAndEnabled((TelegramType) ComboBoxTelegramType.getSelectedItem(), status);
    }

    /**
     * Enables or disables the filters combo box according to the supplied type.
     * If the supplied type is null, then it is always disabled.
     *
     * @param type
     */
    private void setFilterComboBoxEnabled(FilterType type, Status status) {
        // If type is null, always disable.
        if (status != Status.Idle || type == null) {
            TextFieldFilterValues.setEditable(false);
            return;
        }

        // Else, enable/disable according to type.
        switch (type) {
            case EMBASSIES_INCL:
            case EMBASSIES_EXCL:
            case NATIONS_INCL:
            case NATIONS_EXCL:
            case REGIONS_INCL:
            case REGIONS_EXCL:
            case REGIONS_WITH_TAGS_INCL:
            case REGIONS_WITH_TAGS_EXCL:
            case REGIONS_WO_TAGS_INCL:
            case REGIONS_WO_TAGS_EXCL:
            case DELEGATES_NEW_MAX:
            case NATIONS_NEW_MAX:
            case NATIONS_EJECTED_MAX:
            case NATIONS_REFOUNDED_MAX:
            case WA_MEMBERS_NEW_MAX:
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
     * Calculates the estimated duration of sending all the telegrams, and
     * returns it in a formatted string.
     *
     * @return
     */
    public final String duration() {
        if (!telegramManager.potentiallyInfinite()) {
            int estimatedDuration = Math.max(telegramManager.numberOfRecipients() - 1, 0)
                    * ((ComboBoxTelegramType.getSelectedItem() == TelegramType.RECRUITMENT
                    ? 180050 : 30050) / 1000);
            int hours = estimatedDuration / 3600;
            int minutes = estimatedDuration % 3600 / 60;
            int seconds = estimatedDuration % 3600 % 60;
            return String.format(BORDER + "%naddressees selected: %s%nestimated duration: "
                    + "%s hours, %s minutes, %s seconds%n" + BORDER + "%n", telegramManager.numberOfRecipients(), hours, minutes, seconds);
        } else {
            return String.format(BORDER + "%naddressees selected: ∞%nestimated duration: "
                    + "∞%n" + BORDER + "%n");
        }
    }

    /**
     * Utility function for printing messages to the output textarea that are
     * prefixed with a timestamp and suffixed with a newline. If called outside
     * the GUI thread, wrap this in SwingUtilities.invokeLater(...).
     *
     * @param msg
     * @param clear
     */
    public void printToOutput(String msg, boolean clear) {
        msg = "[" + LocalTime.now().format(DateTimeFormatter
                .ofPattern("HH:mm:ss")) + "] " + msg + "\n";

        if (clear) {
            TextAreaOutput.setText(msg);
        } else {
            TextAreaOutput.append(msg);
        }
    }

    /**
     * Sets the input field's tooltip.
     *
     * @param type
     */
    private void setInputHint(FilterType type) {
        String hint = "";

        switch (type) {
            case DELEGATES_NEW_MAX:
            case NATIONS_NEW_MAX:
            case NATIONS_REFOUNDED_MAX:
            case NATIONS_EJECTED_MAX:
            case WA_MEMBERS_NEW_MAX:
                hint = "Insert number of recipients, e.g. '45'.";
                break;
            case EMBASSIES_EXCL:
            case EMBASSIES_INCL:
            case REGIONS_EXCL:
            case REGIONS_INCL:
                hint = "Insert region names, e.g. 'region1, region2'.";
                break;
            case NATIONS_EXCL:
            case NATIONS_INCL:
                hint = "Insert nation names, e.g. 'nation1, nation2'.";
                break;
            case REGIONS_WITH_TAGS_EXCL:
            case REGIONS_WITH_TAGS_INCL:
            case REGIONS_WO_TAGS_EXCL:
            case REGIONS_WO_TAGS_INCL:
                hint = "Insert region tags, e.g. 'tag1, tag2'.";
                break;
        }
        this.TextFieldFilterValues.setHint(hint);
    }

    @Override
    public void handleTelegramSent(TelegramSentEvent event) {
        // Print info to output.
        SwingUtilities.invokeLater(()
                -> {
            if (event.queued) {
                printToOutput("queued telegram for '" + event.recipient + "'", false);
            } else {
                printToOutput("failed to queue telegram for '" + event.recipient + "':\n"
                        + event.errorMessage, false);
            }
        });
    }

    @Override
    public void handleNoRecipientsFound(NoRecipientsFoundEvent event) {
        SwingUtilities.invokeLater(()
                -> {
            printToOutput("no new recipients found, timing out for "
                    + event.TimeOut / 1000 + " seconds...", false);
        });
    }

    @Override
    public void handleStoppedSending(StoppedSendingEvent event) {
        SwingUtilities.invokeLater(()
                -> {
            updateGui(Status.Idle);
            final String message = BORDER + "\nfinished" + (event.CausedByError ? " with error: "
                    + event.ErrorMsg + "\n" : " without fatal errors\n")
                    + "telegrams queued: " + event.QueuedSucces + "\n"
                    + "blocked by category: " + event.RecipientIsBlocking + "\n"
                    + "recipients not found: " + event.RecipientDidntExist + "\n"
                    + "failed b/c other reasons: " + event.DisconnectOrOtherReason + "\n" + BORDER + "\n";
            TextAreaOutput.append(message);
        });
    }

    @Override
    public void handleRecipientRemoved(RecipientRemovedEvent event) {
        SwingUtilities.invokeLater(()
                -> {
            printToOutput("skipping recipient '" + event.Recipient + "': " + event.Reason, false);
        });
    }

    @Override
    public void handleRecipientsRefreshed(RecipientsRefreshedEvent event) {
        SwingUtilities.invokeLater(()
                -> {
            printToOutput("out of recipients, refreshing recipients list...", false);
        });
    }
}
