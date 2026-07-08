package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PlanMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.validations.PlanValidation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.ButtonFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.CardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlansPanel extends JPanel implements Scrollable {

    private DefaultTableModel tableModelPlans;
    private final PlanService planService;
    private final ClientService clientService;
    private final PlanValidation planValidation;
    private boolean isEditMode = false;
    private int editingPlanId = -1;

    // Componentes de interfaz
    private JTextField txtName;
    private JTextField txtCost;
    private JSpinner spinnerDays;
    private JSpinner spinnerHours;
    private JTextArea txtANotes;
    private JTable tableListPlans;

    private JLabel titleCharge;
    private JLabel titleList;

    private JButton btnSave;
    private JButton btnSearch;
    private JButton btnClean;
    private JButton btnActivate;
    private JButton btnDeactivate;

    private JButton btnAll;
    private JButton btnActive;
    private JButton btnInactive;
    private JButton btnModify;
    private final ButtonFactory buttonFactory; // [MEJORA JUNIOR] Se inyecta ButtonFactory

    @Autowired
    public PlansPanel(PlanService planService, PlanValidation planValidation, ClientService clientService, ButtonFactory buttonFactory) {
        this.planService = planService;
        this.planValidation = planValidation;
        this.clientService = clientService;
        this.buttonFactory = buttonFactory;

        initComponentsHandCoded();
        initPlanTable();
        resetForm();
    }

    private void initComponentsHandCoded() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 1. FORM PANEL (Oeste) ---
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setPreferredSize(new Dimension(320, 0));

        JPanel cardPanel = CardFactory.createCardPanel(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), // Removemos el LineBorder manual para usar el de CardFactory
                "Registro de Plan", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), UIManager.getColor("Label.foreground")
            ),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(4, 0, 4, 0);

        int row = 0;

        titleCharge = new JLabel("Nuevo Plan");
        titleCharge.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleCharge.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = row++;
        cardPanel.add(titleCharge, gbc);

        // Form Fields
        gbc.gridy = row++;
        cardPanel.add(new JLabel("Nombre del Plan *"), gbc);
        txtName = new JTextField();
        txtName.putClientProperty("JTextField.placeholderText", "Nombre del plan");
        gbc.gridy = row++;
        cardPanel.add(txtName, gbc);

        gbc.gridy = row++;
        cardPanel.add(new JLabel("Nº de días semanales *"), gbc);
        spinnerDays = new JSpinner(new SpinnerNumberModel(0, 0, 7, 1));
        gbc.gridy = row++;
        cardPanel.add(spinnerDays, gbc);

        gbc.gridy = row++;
        cardPanel.add(new JLabel("Nº de horas semanales *"), gbc);
        spinnerHours = new JSpinner(new SpinnerNumberModel(0, 0, 168, 1));
        gbc.gridy = row++;
        cardPanel.add(spinnerHours, gbc);

        gbc.gridy = row++;
        cardPanel.add(new JLabel("Valor ($) *"), gbc);
        txtCost = new JTextField();
        txtCost.putClientProperty("JTextField.placeholderText", "Costo (ej: 2500.50)");
        gbc.gridy = row++;
        cardPanel.add(txtCost, gbc);

        gbc.gridy = row++;
        cardPanel.add(new JLabel("Notas"), gbc);
        txtANotes = new JTextArea(4, 20);
        txtANotes.setLineWrap(true);
        txtANotes.setWrapStyleWord(true);
        JScrollPane scrollNotes = new JScrollPane(txtANotes);
        scrollNotes.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true));
        gbc.gridy = row++;
        cardPanel.add(scrollNotes, gbc);

        // Form Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        actionPanel.setOpaque(false);

        btnSave = buttonFactory.createPrimaryButton("💾 Guardar");
        btnSave.addActionListener(e -> btnSaveActionPerformed(e));

        btnSearch = buttonFactory.createSecondaryButton("🔍 Buscar");
        btnSearch.addActionListener(e -> btnSearchActionPerformed(e));

        btnActivate = buttonFactory.createSecondaryButton("✅ Activar");
        btnActivate.addActionListener(e -> btnActivateActionPerformed(e));

        btnDeactivate = buttonFactory.createSecondaryButton("❌ Desactivar");
        btnDeactivate.addActionListener(e -> btnDeactivateActionPerformed(e));

        actionPanel.add(btnSave);
        actionPanel.add(btnSearch);
        actionPanel.add(btnActivate);
        actionPanel.add(btnDeactivate);

        gbc.gridy = row++;
        gbc.insets = new Insets(15, 0, 5, 0);
        cardPanel.add(actionPanel, gbc);

        btnClean = buttonFactory.createSecondaryButton("🧹 Limpiar Campos");
        btnClean.addActionListener(e -> btnCleanActionPerformed(e));
        gbc.gridy = row++;
        gbc.insets = new Insets(4, 0, 4, 0);
        cardPanel.add(btnClean, gbc);

        formPanel.add(cardPanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.WEST);

        // --- 2. LIST PANEL (Centro) ---
        JPanel listPanel = new JPanel(new BorderLayout(10, 10));

        JPanel listHeader = new JPanel(new BorderLayout());
        titleList = new JLabel("Lista de Planes");
        titleList.setFont(new Font("Segoe UI", Font.BOLD, 16));
        listHeader.add(titleList, BorderLayout.WEST);

        // Filter actions top-right
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        btnAll = buttonFactory.createSecondaryButton("Todos");
        btnAll.addActionListener(e -> btnAllActionPerformed(e));

        btnActive = buttonFactory.createSecondaryButton("Activos");
        btnActive.addActionListener(e -> btnActiveActionPerformed(e));

        btnInactive = buttonFactory.createSecondaryButton("Inactivos");
        btnInactive.addActionListener(e -> btnInactiveActionPerformed(e));

        btnModify = buttonFactory.createPrimaryButton("📝 Modificar");
        btnModify.addActionListener(e -> btnModifyActionPerformed(e));

        filterPanel.add(btnAll);
        filterPanel.add(btnActive);
        filterPanel.add(btnInactive);
        filterPanel.add(btnModify);
        listHeader.add(filterPanel, BorderLayout.EAST);

        listPanel.add(listHeader, BorderLayout.NORTH);

        // Table
        tableListPlans = new JTable();
        tableListPlans.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableListPlans.setShowHorizontalLines(true);
        tableListPlans.setShowVerticalLines(false);

        JScrollPane scrollPaneTable = new JScrollPane(tableListPlans);
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
                        formPanel.setPreferredSize(new Dimension(320, 0));
                        add(formPanel, BorderLayout.WEST);
                        add(listPanel, BorderLayout.CENTER);
                    }
                    revalidate();
                    repaint();
                }
            }
        });
    }

    private void initPlanTable() {
        tableModelPlans = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableListPlans.setModel(tableModelPlans);
        tableModelPlans.setColumnIdentifiers(new Object[]{
                "Id", "Nombre", "Días hábiles", "Horas hábiles", "Valor", "Notas", "Estado"
        });
        loadPlansToTable(planService.getAllPlansDTO());
    }

    private void loadPlansToTable(List<PlanDTO> plans) {
        tableModelPlans.setRowCount(0);
        for (PlanDTO dto : plans) {
            tableModelPlans.addRow(new Object[] {
                    dto.getIdPlan(),
                    dto.getNamePlan(),
                    dto.getDaysEnabled(),
                    dto.getHoursEnabled(),
                    dto.getValue(),
                    dto.getNotes(),
                    dto.getStatus()
            });
        }
    }

    private void resetForm() {
        txtName.setText("");
        txtCost.setText("");
        txtANotes.setText("");
        spinnerHours.setValue(0);
        spinnerDays.setValue(0);
        
        titleCharge.setText("Nuevo Plan");
        isEditMode = false;
        editingPlanId = -1;
        txtName.setEnabled(true);
        
        // Restaurar bordes FlatLaf
        txtName.setBorder(UIManager.getBorder("TextField.border"));
        txtCost.setBorder(UIManager.getBorder("TextField.border"));
        txtANotes.setBorder(UIManager.getBorder("TextArea.border"));
        spinnerDays.setBorder(UIManager.getBorder("Spinner.border"));
        spinnerHours.setBorder(UIManager.getBorder("Spinner.border"));
    }

    private boolean isFormValid() {
        boolean valid = true;

        if (!isEditMode) {
            String name = txtName.getText().trim();
            if (name.isEmpty() || !planValidation.isValidName(name)) {
                txtName.setBorder(BorderFactory.createLineBorder(Color.RED));
                valid = false;
            } else {
                txtName.setBorder(UIManager.getBorder("TextField.border"));
            }
        }

        String costText = txtCost.getText().trim();
        if (costText.isEmpty()) {
            txtCost.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        } else {
            try {
                BigDecimal costValue = new BigDecimal(costText.replace(",", "."));
                if (!planValidation.isValidCost(costValue)) {
                    txtCost.setBorder(BorderFactory.createLineBorder(Color.RED));
                    valid = false;
                } else {
                    txtCost.setBorder(UIManager.getBorder("TextField.border"));
                }
            } catch (NumberFormatException e) {
                txtCost.setBorder(BorderFactory.createLineBorder(Color.RED));
                valid = false;
            }
        }

        int days = (int) spinnerDays.getValue();
        if (!planValidation.isValidDays(days)) {
            spinnerDays.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        } else {
            spinnerDays.setBorder(UIManager.getBorder("Spinner.border"));
        }

        int hours = (int) spinnerHours.getValue();
        if (!planValidation.isValidHours(hours)) {
            spinnerHours.setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        } else {
            spinnerHours.setBorder(UIManager.getBorder("Spinner.border"));
        }

        return valid;
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (!isFormValid()) {
                JOptionPane.showMessageDialog(this, "Verifique los datos ingresados.", "Validación fallida", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String name = txtName.getText().trim();
            String costText = txtCost.getText().trim().replace(",", ".");
            BigDecimal cost = new BigDecimal(costText);
            String notes = txtANotes.getText().trim();
            Integer selectedHours = (Integer) spinnerHours.getValue();
            Integer selectedDays = (Integer) spinnerDays.getValue();

            if (isEditMode) {
                Plan plan = new Plan(editingPlanId, name, selectedDays, selectedHours, cost, notes, true);
                planService.save(plan);
            } else {
                Plan plan = new Plan(name, selectedDays, selectedHours, cost, notes, true);
                planService.save(plan);
            }

            String msg = isEditMode ? "Plan actualizado correctamente." : "Plan guardado correctamente.";
            JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El valor ingresado no es válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el plan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        resetForm();
        loadPlansToTable(planService.getAllPlansDTO());
    }

    private void btnModifyActionPerformed(java.awt.event.ActionEvent evt) {
        int filaSelected = tableListPlans.getSelectedRow();
        if (filaSelected == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un plan primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = tableListPlans.getValueAt(filaSelected, 0).toString();
        String name = tableListPlans.getValueAt(filaSelected, 1).toString();
        String days = tableListPlans.getValueAt(filaSelected, 2).toString();
        String hours = tableListPlans.getValueAt(filaSelected, 3).toString();
        String cost = tableListPlans.getValueAt(filaSelected, 4).toString();
        String notes = tableListPlans.getValueAt(filaSelected, 5) != null ? tableListPlans.getValueAt(filaSelected, 5).toString() : "";

        txtName.setText(name);
        spinnerDays.setValue(Integer.parseInt(days));
        spinnerHours.setValue(Integer.parseInt(hours));
        txtCost.setText(cost);
        txtANotes.setText(notes);

        isEditMode = true;
        editingPlanId = Integer.parseInt(id);
        titleCharge.setText("Modificar Plan");
    }

    private void modifyStatus(boolean status) {
        int filaSelected = tableListPlans.getSelectedRow();
        if (filaSelected == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un plan primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(tableListPlans.getValueAt(filaSelected, 0).toString());
        planService.changeStatusWithClients(id, status);

        JOptionPane.showMessageDialog(this, "Estado actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        resetForm();
        loadPlansToTable(planService.getAllPlansDTO());
    }

    private void btnActivateActionPerformed(java.awt.event.ActionEvent evt) {
        modifyStatus(true);
    }

    private void btnDeactivateActionPerformed(java.awt.event.ActionEvent evt) {
        modifyStatus(false);
    }

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {
        Set<Plan> plans = new HashSet<>();

        String name = txtName.getText().trim();
        if (!name.isEmpty()) {
            planService.findByNamePlanIgnoreCase(name).ifPresent(plans::add);
        }

        int hours = (int) spinnerHours.getValue();
        if (hours != 0) {
            plans.addAll(planService.findByHoursEnabled(hours));
        }

        int days = (int) spinnerDays.getValue();
        if (days != 0) {
            // FIX: usa 'days' en vez de 'hours'
            plans.addAll(planService.findByDaysEnabled(days));
        }

        String costText = txtCost.getText().trim();
        if (!costText.isEmpty()) {
            try {
                BigDecimal cost = new BigDecimal(costText.replace(",", "."));
                if (cost.compareTo(BigDecimal.ZERO) != 0) {
                    plans.addAll(planService.findByValue(cost));
                }
            } catch (NumberFormatException ignored) {}
        }

        titleList.setText("Lista de Planes: Buscados");
        loadPlansToTable(plans.stream()
                .map(PlanMapper::toDTO)
                .collect(Collectors.toList()));
    }

    private void btnCleanActionPerformed(java.awt.event.ActionEvent evt) {
        resetForm();
    }

    private void btnAllActionPerformed(java.awt.event.ActionEvent evt) {
        titleList.setText("Lista de Planes");
        loadPlansToTable(planService.getAllPlansDTO());
    }

    private void btnActiveActionPerformed(java.awt.event.ActionEvent evt) {
        titleList.setText("Lista de Planes: Activos");
        loadPlansToTable(planService.findByIsActiveDTO(true));
    }

    private void btnInactiveActionPerformed(java.awt.event.ActionEvent evt) {
        titleList.setText("Lista de Planes: Inactivos");
        loadPlansToTable(planService.findByIsActiveDTO(false));
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
