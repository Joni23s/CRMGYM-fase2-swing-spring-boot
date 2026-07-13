package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.ButtonFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.CardFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Component
public class PlansPanel extends JPanel implements Scrollable {

    private DefaultTableModel tableModelPlans;
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
    private final ButtonFactory buttonFactory;

    @Autowired
    public PlansPanel(ButtonFactory buttonFactory) {
        this.buttonFactory = buttonFactory;

        initComponentsHandCoded();
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
        btnSearch = buttonFactory.createSecondaryButton("🔍 Buscar");
        btnActivate = buttonFactory.createSecondaryButton("✅ Activar");
        btnDeactivate = buttonFactory.createSecondaryButton("❌ Desactivar");

        actionPanel.add(btnSave);
        actionPanel.add(btnSearch);
        actionPanel.add(btnActivate);
        actionPanel.add(btnDeactivate);

        gbc.gridy = row++;
        gbc.insets = new Insets(15, 0, 5, 0);
        cardPanel.add(actionPanel, gbc);

        btnClean = buttonFactory.createSecondaryButton("🧹 Limpiar Campos");
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
        btnActive = buttonFactory.createSecondaryButton("Activos");
        btnInactive = buttonFactory.createSecondaryButton("Inactivos");
        btnModify = buttonFactory.createPrimaryButton("📝 Modificar");

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

    public void initPlanTable() {
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
    }

    public void resetForm() {
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

    public void showLoadingTable() {
        tableModelPlans.setRowCount(0);
        tableModelPlans.addRow(new Object[] {
                "", "", "Cargando datos...", "", "", "", ""
        });
        btnSearch.setEnabled(false);
    }

    public void updateTable(List<PlanDTO> plans) {
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
        btnSearch.setEnabled(true);
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
    public JTextField getTxtCost() { return txtCost; }
    public JSpinner getSpinnerDays() { return spinnerDays; }
    public JSpinner getSpinnerHours() { return spinnerHours; }
    public JTextArea getTxtANotes() { return txtANotes; }
    public JTable getTableListPlans() { return tableListPlans; }
    
    public JButton getBtnSave() { return btnSave; }
    public JButton getBtnSearch() { return btnSearch; }
    public JButton getBtnClean() { return btnClean; }
    public JButton getBtnActivate() { return btnActivate; }
    public JButton getBtnDeactivate() { return btnDeactivate; }
    public JButton getBtnAll() { return btnAll; }
    public JButton getBtnActive() { return btnActive; }
    public JButton getBtnInactive() { return btnInactive; }
    public JButton getBtnModify() { return btnModify; }
    
    public JLabel getTitleCharge() { return titleCharge; }
    public JLabel getTitleList() { return titleList; }

    public boolean isEditMode() { return isEditMode; }
    public void setEditMode(boolean editMode) { isEditMode = editMode; }
    
    public int getEditingPlanId() { return editingPlanId; }
    public void setEditingPlanId(int id) { editingPlanId = id; }

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
