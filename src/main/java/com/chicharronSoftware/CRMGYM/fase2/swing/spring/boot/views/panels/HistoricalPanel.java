package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.HistoricalPlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.HistoricalPlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.ButtonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HistoricalPanel extends JPanel implements Scrollable {

    private DefaultTableModel tableModelHistorical;
    private final ClientService clientService;
    private final PlanService planService;
    private final HistoricalPlanService historicalPlanService;
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
    public HistoricalPanel(ClientService clientService, PlanService planService, HistoricalPlanService historicalPlanService, ButtonFactory buttonFactory) {
        this.clientService = clientService;
        this.planService = planService;
        this.historicalPlanService = historicalPlanService;
        this.buttonFactory = buttonFactory;

        initComponentsHandCoded();
        initHistoricalPlanTable();
        initRadioButtonsClients();
        initRadioButtonsPlans();
        resetForm();
    }

    private void initComponentsHandCoded() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 1. SEARCH/FILTER PANEL (Oeste) ---
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setPreferredSize(new Dimension(340, 0));

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true),
                "Filtros de Búsqueda", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), UIManager.getColor("Label.foreground")
            ),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        cardPanel.putClientProperty("FlatLaf.style", "arc: 12; background: $Component.background;");

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
        btnSearch.addActionListener(this::btnSearchActionPerformed);
        
        btnCleanPanels = buttonFactory.createSecondaryButton("🧹 Limpiar");
        btnCleanPanels.addActionListener(this::btnCleanPanelsActionPerformed);

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
        btnCleanTable.addActionListener(this::btnCleanTableActionPerformed);
        
        btnSelect = buttonFactory.createPrimaryButton("🔎 Seleccionar Cliente");
        btnSelect.addActionListener(this::btnSelectActionPerformed);
        
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

    private void initHistoricalPlanTable() {
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
        loadHistoricalPlanToTable(clientService.findAll());
    }

    private void loadHistoricalPlanToTable(List<Client> clients) {
        tableModelHistorical.setRowCount(0);
        // [MEJORA JUNIOR] Evitamos el problema N+1 haciendo una sola consulta masiva
        // en lugar de iterar cliente por cliente (lo que hacía una consulta por cada uno).
        List<HistoricalPlanDTO> historicalPlanDTOS = historicalPlanService.findByClientsWithDetails(clients);

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

    private void initRadioButtonsPlans() {
        ButtonGroup groupPlanStatus = new ButtonGroup();
        groupPlanStatus.add(btnPlanActive);
        groupPlanStatus.add(btnPlanInactive);
        btnPlanActive.setSelected(true);
        loadPlansToComboBox(true);

        btnPlanActive.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadPlansToComboBox(true);
            }
        });

        btnPlanInactive.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadPlansToComboBox(false);
            }
        });
    }

    private void initRadioButtonsClients() {
        ButtonGroup groupClientStatus = new ButtonGroup();
        groupClientStatus.add(btnClientActive);
        groupClientStatus.add(btnClientInactive);

        btnClientActive.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadHistoricalPlanToTable(clientService.findByIsActive(true));
            }
        });

        btnClientInactive.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadHistoricalPlanToTable(clientService.findByIsActive(false));
            }
        });
    }

    private void loadPlansToComboBox(boolean active) {
        comboBoxPlan.removeAllItems();
        comboBoxPlan.addItem("Seleccione un Plan *");

        List<PlanDTO> planes = planService.findByIsActiveDTO(active);
        if (planes != null) {
            for (PlanDTO plan : planes) {
                comboBoxPlan.addItem(plan.toString());
            }
        }
    }

    private void resetForm() {
        txtName.setText("");
        txtLastName.setText("");
        txtDni.setText("");
        txtPhone1.setText("");
        
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

    private void btnSearchActionPerformed(ActionEvent evt) {
        Set<Client> clients = new HashSet<>();

        if (btnName.isSelected()) {
            String name = txtName.getText().trim();
            if (!name.isEmpty()) {
                clients.addAll(clientService.findByName(name));
            }
        }

        if (btnLastName.isSelected()) {
            String lastName = txtLastName.getText().trim();
            if (!lastName.isEmpty()) {
                clients.addAll(clientService.findByLastName(lastName));
            }
        }

        if (btnDni.isSelected()) {
            String dniText = txtDni.getText().trim();
            if (!dniText.isEmpty()) {
                try {
                    int dni = Integer.parseInt(dniText);
                    clientService.findById(dni).ifPresent(clients::add);
                } catch (NumberFormatException ignored) {}
            }
        }

        if (btnPhone.isSelected()) {
            String phone = txtPhone1.getText().trim();
            if (!phone.isEmpty()) {
                clients.addAll(clientService.findByPhoneNumber(phone));
            }
        }

        if (btnPlan.isSelected()) {
            if (comboBoxPlan.getSelectedIndex() != 0) {
                String selectedPlan = comboBoxPlan.getSelectedItem().toString();
                clients.addAll(clientService.findByCurrentPlan(selectedPlan));
            }
        }

        titleList.setText("Lista de Historial: Buscados");
        loadHistoricalPlanToTable(new ArrayList<>(clients));
    }

    private void btnCleanPanelsActionPerformed(ActionEvent evt) {
        resetForm();
    }

    private void btnSelectActionPerformed(ActionEvent evt) {
        int filaSelected = tableListClients.getSelectedRow();
        if (filaSelected == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona a un cliente primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String idStr = tableListClients.getValueAt(filaSelected, 4).toString();
            int clientId = Integer.parseInt(idStr);

            Optional<Client> clientOpt = clientService.findById(clientId);
            if (clientOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró el cliente con ID: " + clientId, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Client client = clientOpt.get();
            List<Client> clients = new ArrayList<>();
            clients.add(client);

            titleList.setText("Historial de Cliente: " + client.getDocumentId());
            loadHistoricalPlanToTable(clients);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El valor de ID no es válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al obtener el cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnCleanTableActionPerformed(ActionEvent evt) {
        tableModelHistorical.setRowCount(0);
        titleList.setText("Lista de Historial");
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE Scrollable: Permite scroll vertical adaptativo
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
        return true; // Llenar el ancho del viewport
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
