package com.github.agadar.telegrammer.client.view;

import java.awt.event.ItemEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.DefaultCaret;

import com.github.agadar.telegrammer.client.viewmodel.TelegrammerViewModel;
import com.github.agadar.telegrammer.client.viewmodel.TelegrammerViewModelListener;

/**
 * A view representing the TelegrammerViewModel, implemented using the Swing
 * technologies.
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public final class TelegrammerView extends javax.swing.JFrame implements TelegrammerViewModelListener {

    private static final long serialVersionUID = 1L;

    private final TelegrammerViewModel viewModel;

    private javax.swing.JTextArea TextAreaOutput;
    private javax.swing.JList<String> JListFilters;
    private javax.swing.JButton BtnStart;
    private javax.swing.JButton BtnClearOutput;
    private javax.swing.JButton BtnStop;
    private javax.swing.JButton ButtonAddFilter;
    private javax.swing.JButton ButtonRemoveFilter;
    private javax.swing.JButton ButtonRefreshFilters;
    private JCheckBoxMenuItem chckbxmntmHideSkippedRecipients;
    private JCheckBoxMenuItem chckbxmntmRunIndefinitely;
    private JCheckBoxMenuItem chckbxmntmStartSendingOn;
    private JCheckBoxMenuItem chckbxmntmStartMinimized;
    private JCheckBoxMenuItem chckbxmntmRefreshRecipientsAfter;
    private javax.swing.JComboBox<String> ComboBoxFilterType;
    private javax.swing.JComboBox<String> ComboBoxProviderType;
    private javax.swing.JComboBox<String> ComboBoxTelegramType;
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
    private HintTextField TextFieldFilterValues;
    private javax.swing.JTextField TxtFieldClientKey;
    private javax.swing.JTextField TxtFieldRegionFrom;
    private javax.swing.JTextField TxtFieldSecretKey;
    private javax.swing.JTextField TxtFieldTelegramId;

    public TelegrammerView(TelegrammerViewModel viewModel) {
        this.viewModel = viewModel;

        initComponents();
        setTitle(viewModel.getTitle());
        ((DefaultCaret) TextAreaOutput.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        ComboBoxFilterType.setModel(new DefaultComboBoxModel<String>(viewModel.getAvailableFilterTypes()));
        ComboBoxProviderType.setModel(new DefaultComboBoxModel<String>(viewModel.getAvailableProviderTypes()));
        ComboBoxTelegramType.setModel(new DefaultComboBoxModel<String>(viewModel.getAvailableTelegramTypes()));

        if (viewModel.getStartMinimized()) {
            this.setExtendedState(java.awt.Frame.ICONIFIED);
        }
        viewModel.setListener(this);
    }

    @Override
    public void refreshOutput() {
        if (java.awt.EventQueue.isDispatchThread()) {
            TextAreaOutput.setText(viewModel.getOutputText());
        } else {
            java.awt.EventQueue.invokeLater(() -> TextAreaOutput.setText(viewModel.getOutputText()));
        }
    }

    @Override
    public void refreshEverything() {
        if (java.awt.EventQueue.isDispatchThread()) {
            updateGuiComponents();
        } else {
            java.awt.EventQueue.invokeLater(() -> updateGuiComponents());
        }
    }

    private void updateGuiComponents() {
        TextAreaOutput.setText(viewModel.getOutputText());
        var configuredFilters = (DefaultListModel<String>) JListFilters.getModel();
        configuredFilters.clear();
        configuredFilters.addAll(viewModel.getConfiguredRecipientsFilters());
        JListFilters.setEnabled(viewModel.isConfiguredRecipientsFiltersEnabled());
        JListFilters.setSelectedIndex(viewModel.getSelectedConfiguredRecipientsFilterIndex());
        BtnStart.setEnabled(viewModel.isStartSendingButtonEnabled());
        BtnStop.setEnabled(viewModel.isStopSendingButtonEnabled());
        ButtonAddFilter.setEnabled(viewModel.isAddFilterButtonEnabled());
        ButtonRemoveFilter.setEnabled(viewModel.isRemoveFilterButtonEnabled());
        ButtonRefreshFilters.setEnabled(viewModel.isRefreshFiltersButtonEnabled());
        chckbxmntmHideSkippedRecipients.setEnabled(viewModel.isOptionsMenuEnabled());
        chckbxmntmHideSkippedRecipients.setSelected(viewModel.getHideSkippedRecipients());
        chckbxmntmRunIndefinitely.setEnabled(viewModel.isOptionsMenuEnabled());
        chckbxmntmRunIndefinitely.setSelected(viewModel.getRunIndefinitely());
        chckbxmntmStartSendingOn.setEnabled(viewModel.isOptionsMenuEnabled());
        chckbxmntmStartSendingOn.setSelected(viewModel.getStartSendingOnStartup());
        chckbxmntmStartMinimized.setEnabled(viewModel.isOptionsMenuEnabled());
        chckbxmntmStartMinimized.setSelected(viewModel.getStartMinimized());
        chckbxmntmRefreshRecipientsAfter.setEnabled(viewModel.isOptionsMenuEnabled());
        chckbxmntmRefreshRecipientsAfter.setSelected(viewModel.getRefreshRecipientsAfterEveryTelegram());
        ComboBoxFilterType.setEnabled(viewModel.isAvailableFilterTypesInputEnabled());
        ComboBoxFilterType.setSelectedIndex(viewModel.getSelectedFilterTypeIndex());
        ComboBoxProviderType.setEnabled(viewModel.isAvailableProviderTypesInputEnabled());
        ComboBoxProviderType.setSelectedIndex(viewModel.getSelectedProviderTypeIndex());
        ComboBoxTelegramType.setEnabled(viewModel.isAvailableTelegramTypesEnabled());
        ComboBoxTelegramType.setSelectedIndex(viewModel.getSelectedTelegramTypeIndex());
        TextFieldFilterValues.setEditable(viewModel.isFilterParametersInputEnabled());
        TextFieldFilterValues.setText(viewModel.getFilterParameters());
        TextFieldFilterValues.setHint(viewModel.getFilterParametersHint());
        TxtFieldClientKey.setEditable(viewModel.isClientKeyInputEnabled());
        TxtFieldClientKey.setText(viewModel.getClientKey());
        TxtFieldRegionFrom.setEditable(viewModel.isForRegionInputEnabled());
        TxtFieldRegionFrom.setText(viewModel.getForRegion());
        TxtFieldSecretKey.setEditable(viewModel.isSecretKeyInputEnabled());
        TxtFieldSecretKey.setText(viewModel.getSecretKey());
        TxtFieldTelegramId.setEditable(viewModel.isTelegramIdInputEnabled());
        TxtFieldTelegramId.setText(viewModel.getTelegramId());
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
        ButtonRefreshFilters = new javax.swing.JButton();
        ComboBoxProviderType = new javax.swing.JComboBox<>();
        ButtonAddFilter = new javax.swing.JButton();
        TextFieldFilterValues = new com.github.agadar.telegrammer.client.view.HintTextField();
        ComboBoxFilterType = new javax.swing.JComboBox<>();
        PanelOutput = new javax.swing.JPanel();
        ScrollPaneOutput = new javax.swing.JScrollPane();
        TextAreaOutput = new javax.swing.JTextArea();
        PanelActions = new javax.swing.JPanel();
        BtnStart = new javax.swing.JButton();
        BtnStop = new javax.swing.JButton();
        BtnClearOutput = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("NSTelegramFrame"); // NOI18N
        setResizable(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (JListFilters.getSelectedIndex() > -1) {
                    viewModel.unsetSelectedConfiguredRecipientsFilter();
                }
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                viewModel.performPreCloseActions();
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
                viewModel.setTelegramId(TxtFieldTelegramId.getText());
            }
        });

        LabelSecretKey.setLabelFor(TxtFieldSecretKey);
        LabelSecretKey.setText("Secret Key:");
        LabelSecretKey.setName("LabelSecretKey"); // NOI18N

        TxtFieldSecretKey.setName("TxtFieldSecretKey"); // NOI18N
        TxtFieldSecretKey.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                viewModel.setSecretKey(TxtFieldSecretKey.getText());
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
                viewModel.setClientKey(TxtFieldClientKey.getText());
            }
        });

        TxtFieldRegionFrom.setEditable(false);
        TxtFieldRegionFrom.setName("TxtFieldSecretKey"); // NOI18N
        TxtFieldRegionFrom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                viewModel.setForRegion(TxtFieldRegionFrom.getText());
            }
        });

        LabelRegionFrom.setText("For region:");
        LabelRegionFrom.setName("LabelRecruiting"); // NOI18N

        ComboBoxTelegramType.addItemListener((event) -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                viewModel.setSelectedTelegramTypeIndex(ComboBoxTelegramType.getSelectedIndex());
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

        JListFilters.setModel(new DefaultListModel<String>());
        JListFilters.setName("JListFilters"); // NOI18N
        JListFilters.addListSelectionListener((event) -> {
            if (!event.getValueIsAdjusting() && JListFilters.getSelectedIndex() > -1) {
                viewModel.setSelectedConfiguredRecipientsFilter(JListFilters.getSelectedIndex());
            }
        });
        ScrollPaneFilters.setViewportView(JListFilters);

        ButtonRemoveFilter.setText("Remove");
        ButtonRemoveFilter.setEnabled(false);
        ButtonRemoveFilter.setName("ButtonRemoveFilter"); // NOI18N
        ButtonRemoveFilter.addActionListener((event) -> viewModel.removeSelectedFilter());

        ComboBoxProviderType.setName("ComboBoxProviderType"); // NOI18N
        ComboBoxProviderType.addItemListener((event) -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                viewModel.setSelectedProviderTypeIndex(ComboBoxProviderType.getSelectedIndex());
            }
        });

        ButtonAddFilter.setText("Add");
        ButtonAddFilter.setName("ButtonAddFilter"); // NOI18N
        ButtonAddFilter.addActionListener((event) -> viewModel.addNewFilter());

        ButtonRefreshFilters.setText("Refresh");
        ButtonRefreshFilters.setName("ButtonRefreshFilters");
        ButtonRefreshFilters.addActionListener((event) -> viewModel.refreshFilters());

        TextFieldFilterValues.setHint("");
        TextFieldFilterValues.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                viewModel.setFilterParameters(TextFieldFilterValues.getText());
            }
        });

        ComboBoxFilterType.setName("ComboBoxFilterType"); // NOI18N
        ComboBoxFilterType.addItemListener((event) -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                viewModel.setSelectedFilterTypeIndex(ComboBoxFilterType.getSelectedIndex());
            }
        });

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
                        .addGroup(PanelFiltersLayout.createSequentialGroup().addComponent(ButtonRemoveFilter, javax.swing.GroupLayout.DEFAULT_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18).addComponent(ButtonAddFilter, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(18, 18, 18)
                                .addComponent(ButtonRefreshFilters, javax.swing.GroupLayout.DEFAULT_SIZE,
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
                                .addComponent(ButtonRemoveFilter).addComponent(ButtonAddFilter)
                                .addComponent(ButtonRefreshFilters))
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
        BtnStart.addActionListener((event) -> viewModel.startSendingTelegrams());

        BtnStop.setText("Stop sending");
        BtnStop.setEnabled(false);
        BtnStop.setMaximumSize(new java.awt.Dimension(97, 23));
        BtnStop.setMinimumSize(new java.awt.Dimension(97, 23));
        BtnStop.setName("ButtonRemoveAddressee"); // NOI18N
        BtnStop.setPreferredSize(new java.awt.Dimension(97, 23));
        BtnStop.addActionListener((event) -> viewModel.stopSendingTelegrams());

        BtnClearOutput.setText("Clear output");
        BtnClearOutput.setName("ButtonRemoveAddressee"); // NOI18N
        BtnClearOutput.addActionListener((event) -> viewModel.clearOutput());

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        mnNewMenu = new JMenu("Options");
        menuBar.add(mnNewMenu);

        chckbxmntmHideSkippedRecipients = new JCheckBoxMenuItem("Hide skipped recipients");
        chckbxmntmHideSkippedRecipients.addItemListener(
                (event) -> viewModel.setHideSkippedRecipients(chckbxmntmHideSkippedRecipients.isSelected()));
        mnNewMenu.add(chckbxmntmHideSkippedRecipients);

        chckbxmntmRunIndefinitely = new JCheckBoxMenuItem("Run indefinitely");
        chckbxmntmRunIndefinitely
                .addItemListener((event) -> viewModel.setRunIndefinitely(chckbxmntmRunIndefinitely.isSelected()));

        chckbxmntmRefreshRecipientsAfter = new JCheckBoxMenuItem("Refresh recipients after every telegram");
        chckbxmntmRefreshRecipientsAfter.addItemListener((event) -> viewModel
                .setRefreshRecipientsAfterEveryTelegram(chckbxmntmRefreshRecipientsAfter.isSelected()));
        mnNewMenu.add(chckbxmntmRefreshRecipientsAfter);
        mnNewMenu.add(chckbxmntmRunIndefinitely);

        chckbxmntmStartSendingOn = new JCheckBoxMenuItem("Start sending on startup");
        chckbxmntmStartSendingOn
                .addItemListener((event) -> viewModel.setStartSendingOnStartup(chckbxmntmStartSendingOn.isSelected()));

        chckbxmntmStartMinimized = new JCheckBoxMenuItem("Start minimized");
        mnNewMenu.add(chckbxmntmStartMinimized);
        chckbxmntmStartMinimized
                .addItemListener((event) -> viewModel.setStartMinimized(chckbxmntmStartMinimized.isSelected()));
        mnNewMenu.add(chckbxmntmStartSendingOn);

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
}
