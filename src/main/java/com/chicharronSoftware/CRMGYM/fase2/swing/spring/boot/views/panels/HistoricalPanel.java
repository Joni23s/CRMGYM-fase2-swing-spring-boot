package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.HistoricalPlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.ButtonFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.CardFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistoricalPanel extends JPanel implements Scrollable {

    private DefaultTableModel tableModelHistorical;
    private final ButtonFactory buttonFactory;

    // Componentes de interfaz
    private JTextField txtName;
    private JTextField txtLastName;
    private JTextField txtDni;
    private JTextField txtPhone1;
    private JComboBox<String> comboBoxPlan;
    private JTable tableListClients;

    private JRadioButton btnClientActive;
    private JRadioButton btnClientInactive;
    private JRadioButton btnPlanActive;
    private JRadioButton btnPlanInactive;

    private JRadioButton btnName;
    private JRadioButton btnLastName;
    private JRadioButton btnDni;
    private JRadioButton btnPhone;
    private JRadioButton btnPlan;

    private JLabel titleCharge;
    private JLabel titleList;

    private JButton btnSearch;
    private JButton btnCleanPanels;
    private JButton btnSelect;
    private JButton btnCleanTable;

    @Autowired
    public HistoricalPanel(ButtonFactory buttonFactory) {
        this.buttonFactory = buttonFactory;
        initComponentsHandCoded();
    }

    private void initComponentsHandCoded() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 1. SEARCH/FILTER PANEL (Oeste) ---
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setPreferredSize(new Dimension(340, 0));

        JPanel cardPanel = CardFactory.createCardPanel(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                "Filtros de Búsqueda", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), UIManager.getColor("Label.foreground")
            ),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(2, 0, 2, 0);

        int row = 0;

        titleCharge = new JLabel("Búsqueda");
        titleCharge.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleCharge.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = row++;
        cardPanel.add(titleCharge, gbc);

        // Section: Client Status
        JLabel titleClient = new JLabel("Por Estado de Cliente");
        titleClient.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = row++;
        gbc.insets = new Insets(8, 0, 2, 0);
        cardPanel.add(titleClient, gbc);

        JPanel clientStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        clientStatusPanel.setOpaque(false);
        btnClientActive = new JRadioButton("Activo");
        btnClientActive.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClientInactive = new JRadioButton("Inactivo");
        btnClientInactive.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        ButtonGroup groupClientStatus = new ButtonGroup();
        groupClientStatus.add(btnClientActive);
        groupClientStatus.add(btnClientInactive);
        
        clientStatusPanel.add(btnClientActive);
        clientStatusPanel.add(btnClientInactive);
        gbc.gridy = row++;
        gbc.insets = new Insets(2, 0, 2, 0);
        cardPanel.add(clientStatusPanel, gbc);

        // Fields with "Use" check
        gbc.gridy = row++;
        cardPanel.add(createFieldHeader("Nombre", btnName = new JRadioButton("Usar")), gbc);
        txtName = new JTextField();
        txtName.putClientProperty("JTextField.placeholderText", "Nombre a buscar");
        gbc.gridy = row++;
        cardPanel.add(txtName, gbc);

        gbc.gridy = row++;
        cardPanel.add(createFieldHeader("Apellido", btnLastName = new JRadioButton("Usar")), gbc);
        txtLastName = new JTextField();
        txtLastName.putClientProperty("JTextField.placeholderText", "Apellido a buscar");
        gbc.gridy = row++;
        cardPanel.add(txtLastName, gbc);

        gbc.gridy = row++;
        cardPanel.add(createFieldHeader("DNI", btnDni = new JRadioButton("Usar")), gbc);
        txtDni = new JTextField();
        txtDni.putClientProperty("JTextField.placeholderText", "DNI del cliente");
        gbc.gridy = row++;
        cardPanel.add(txtDni, gbc);

        gbc.gridy = row++;
        cardPanel.add(createFieldHeader("Celular", btnPhone = new JRadioButton("Usar")), gbc);
        txtPhone1 = new JTextField();
        txtPhone1.putClientProperty("JTextField.placeholderText", "Número de celular");
        gbc.gridy = row++;
        cardPanel.add(txtPhone1, gbc);

        // Section: Plan Status & ComboBox
        JLabel titlePlan = new JLabel("Por Plan");
        titlePlan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = row++;
        gbc.insets = new Insets(10, 0, 2, 0);
        cardPanel.add(titlePlan, gbc);

        JPanel planStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        planStatusPanel.setOpaque(false);
        btnPlanActive = new JRadioButton("Plan Activo");
        btnPlanActive.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPlanInactive = new JRadioButton("Plan Inactivo");
        btnPlanInactive.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        ButtonGroup groupPlanStatus = new ButtonGroup();
        groupPlanStatus.add(btnPlanActive);
        groupPlanStatus.add(btnPlanInactive);
        
        planStatusPanel.add(btnPlanActive);
        planStatusPanel.add(btnPlanInactive);
        gbc.gridy = row++;
        gbc.insets = new Insets(2, 0, 2, 0);
        cardPanel.add(planStatusPanel, gbc);

        gbc.gridy = row++;
        cardPanel.add(createFieldHeader("Seleccionar Plan", btnPlan = new JRadioButton("Usar")), gbc);
        comboBoxPlan = new JComboBox<>();
        gbc.gridy = row++;
        cardPanel.add(comboBoxPlan, gbc);

        // Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        actionPanel.setOpaque(false);

        btnSearch = buttonFactory.createPrimaryButton("🔍 Buscar");
        btnCleanPanels = buttonFactory.createSecondaryButton("🧹 Limpiar");

        actionPanel.add(btnCleanPanels);
        actionPanel.add(btnSearch);

        gbc.gridy = row++;
        gbc.insets = new Insets(15, 0, 0, 0);
        cardPanel.add(actionPanel, gbc);

        formPanel.add(cardPanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.WEST);

        // --- 2. LIST PANEL (Centro) ---
        JPanel listPanel = new JPanel(new BorderLayout(10, 10));

        JPanel listHeader = new JPanel(new BorderLayout());
        titleList = new JLabel("Resultados");
        titleList.setFont(new Font("Segoe UI", Font.BOLD, 16));
        listHeader.add(titleList, BorderLayout.WEST);

        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        
        btnCleanTable = buttonFactory.createSecondaryButton("🧹 Limpiar Tabla");
        btnSelect = buttonFactory.createPrimaryButton("🔎 Seleccionar Cliente");
        
        tableActions.add(btnCleanTable);
        tableActions.add(btnSelect);
        listHeader.add(tableActions, BorderLayout.EAST);

        listPanel.add(listHeader, BorderLayout.NORTH);

        // Table
        tableListClients = new JTable();
        tableListClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableListClients.setShowHorizontalLines(true);
        tableListClients.setShowVerticalLines(false);

        JScrollPane scrollPaneTable = new JScrollPane(tableListClients);
        scrollPaneTable.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true));
        listPanel.add(scrollPaneTable, BorderLayout.CENTER);

        add(listPanel, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            private Boolean isSmall = null;

            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = getWidth();
                boolean shouldBeSmall = width < 850;

                if (isSmall == null || shouldBeSmall != isSmall) {
                    isSmall = shouldBeSmall;
                    remove(formPanel);
                    remove(listPanel);
                    if (shouldBeSmall) {
                        formPanel.setPreferredSize(new Dimension(0, 480));
                        add(formPanel, BorderLayout.NORTH);
                        add(listPanel, BorderLayout.CENTER);
                    } else {
                        formPanel.setPreferredSize(new Dimension(340, 0));
                        add(formPanel, BorderLayout.WEST);
                        add(listPanel, BorderLayout.CENTER);
                    }
                    revalidate();
                    repaint();
                }
            }
        });
    }

    private JPanel createFieldHeader(String labelText, JRadioButton radioButton) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(label, BorderLayout.WEST);
        radioButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(radioButton, BorderLayout.EAST);
        return panel;
    }

    public void initHistoricalPlanTable() {
        tableModelHistorical = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableListClients.setModel(tableModelHistorical);
        tableModelHistorical.setColumnIdentifiers(new Object[]{
                "Id", "Inicio", "Fin", "Estado", "DNI", "Cliente", "Plan"
        });
    }

    public void showLoadingTable() {
        tableModelHistorical.setRowCount(0);
        tableModelHistorical.addRow(new Object[] {
                "", "", "Cargando datos...", "", "", "", ""
        });
    }

    public void updateTable(List<HistoricalPlanDTO> historicalPlanDTOS) {
        tableModelHistorical.setRowCount(0);
        for (HistoricalPlanDTO dto : historicalPlanDTOS) {
            tableModelHistorical.addRow(new Object[] {
                    dto.getIdHistorical(),
                    dto.getStartDate(),
                    dto.getEndDate(),
                    dto.getIsActive(),
                    dto.getDocumentId(),
                    dto.getClientName(),
                    dto.getPlanName()
            });
        }
    }

    public void clearTable() {
        tableModelHistorical.setRowCount(0);
    }

    public void populatePlansComboBox(List<PlanDTO> planes) {
        comboBoxPlan.removeAllItems();
        comboBoxPlan.addItem("Seleccione un Plan *");
        for (PlanDTO plan : planes) {
            comboBoxPlan.addItem(plan.toString());
        }
    }

    public void resetForm() {
        txtName.setText("");
        txtLastName.setText("");
        txtDni.setText("");
        txtPhone1.setText("");
        
        // Evitamos disparar eventos infinitos temporalmente o los ignoramos del lado del presenter.
        btnClientActive.setSelected(false);
        btnClientInactive.setSelected(false);
        btnPlanActive.setSelected(true);
        
        btnName.setSelected(false);
        btnLastName.setSelected(false);
        btnDni.setSelected(false);
        btnPhone.setSelected(false);
        btnPlan.setSelected(false);
        
        if (comboBoxPlan.getItemCount() > 0) {
            comboBoxPlan.setSelectedIndex(0);
        }
        
        titleCharge.setText("Búsqueda");

        // Restaurar bordes FlatLaf
        txtName.setBorder(UIManager.getBorder("TextField.border"));
        txtLastName.setBorder(UIManager.getBorder("TextField.border"));
        txtDni.setBorder(UIManager.getBorder("TextField.border"));
        txtPhone1.setBorder(UIManager.getBorder("TextField.border"));
        comboBoxPlan.setBorder(UIManager.getBorder("ComboBox.border"));
    }

    public void enableSearchButton(boolean enabled) {
        btnSearch.setEnabled(enabled);
    }

    public void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public void showSuccess(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Getters
    public JTextField getTxtName() { return txtName; }
    public JTextField getTxtLastName() { return txtLastName; }
    public JTextField getTxtDni() { return txtDni; }
    public JTextField getTxtPhone1() { return txtPhone1; }
    public JComboBox<String> getComboBoxPlan() { return comboBoxPlan; }
    public JTable getTableListClients() { return tableListClients; }

    public JRadioButton getBtnClientActive() { return btnClientActive; }
    public JRadioButton getBtnClientInactive() { return btnClientInactive; }
    public JRadioButton getBtnPlanActive() { return btnPlanActive; }
    public JRadioButton getBtnPlanInactive() { return btnPlanInactive; }
    public JRadioButton getBtnName() { return btnName; }
    public JRadioButton getBtnLastName() { return btnLastName; }
    public JRadioButton getBtnDni() { return btnDni; }
    public JRadioButton getBtnPhone() { return btnPhone; }
    public JRadioButton getBtnPlan() { return btnPlan; }

    public JLabel getTitleCharge() { return titleCharge; }
    public JLabel getTitleList() { return titleList; }

    public JButton getBtnSearch() { return btnSearch; }
    public JButton getBtnCleanPanels() { return btnCleanPanels; }
    public JButton getBtnSelect() { return btnSelect; }
    public JButton getBtnCleanTable() { return btnCleanTable; }

    // =========================================================================
    // IMPLEMENTACIÓN DE Scrollable
    // =========================================================================
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
        return 80;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getWidth() < 850) {
            return false;
        }
        if (getParent() instanceof JViewport) {
            return getPreferredSize().height <= ((JViewport) getParent()).getHeight();
        }
        return true;
    }
}
