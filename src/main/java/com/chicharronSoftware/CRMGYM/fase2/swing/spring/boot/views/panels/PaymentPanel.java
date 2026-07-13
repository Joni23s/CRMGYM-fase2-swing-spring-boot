package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.utils.FormatterUtils;
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
public class PaymentPanel extends JPanel implements Scrollable {

    private DefaultTableModel tableModelPays;
    private final ButtonFactory buttonFactory;

    private boolean isEditMode = false;
    private Long editingPaymentId = null;

    // Componentes de interfaz
    private JTextField txtDni;
    private JTextField txtBase_amount;
    private JTextField txtDiscount;
    private JComboBox<String> comboBoxPayment_method;
    private JComboBox<String> comboBoxPayment_status;
    private com.toedter.calendar.JDateChooser jDatePay;
    private com.toedter.calendar.JDateChooser jDatePeriod;
    private JTable tableListPayments;

    private JLabel titleCharge;
    private JLabel titleList;

    private JButton btnSave;
    private JButton btnSearch;
    private JButton btnClean;
    private JButton btnModify;
    
    private JButton btnAll;
    private JButton btnPending;
    private JButton btnConfirmed;

    @Autowired
    public PaymentPanel(ButtonFactory buttonFactory) {
        this.buttonFactory = buttonFactory;
        initComponentsHandCoded();
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
                BorderFactory.createEmptyBorder(),
                "Registro de Pago", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), UIManager.getColor("Label.foreground")
            ),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(3, 0, 3, 0);

        int row = 0;

        titleCharge = new JLabel("Nuevo Pago");
        titleCharge.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleCharge.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = row++;
        cardPanel.add(titleCharge, gbc);

        // Form Fields
        gbc.gridy = row++;
        cardPanel.add(new JLabel("Cliente (DNI) *"), gbc);
        txtDni = new JTextField();
        txtDni.putClientProperty("JTextField.placeholderText", "Ingrese el DNI");
        gbc.gridy = row++;
        cardPanel.add(txtDni, gbc);

        gbc.gridy = row++;
        cardPanel.add(new JLabel("Período *"), gbc);
        jDatePeriod = new com.toedter.calendar.JDateChooser();
        jDatePeriod.setDateFormatString("yyyy-MM-dd");
        gbc.gridy = row++;
        cardPanel.add(jDatePeriod, gbc);

        gbc.gridy = row++;
        cardPanel.add(new JLabel("Fecha de Pago *"), gbc);
        jDatePay = new com.toedter.calendar.JDateChooser();
        jDatePay.setDateFormatString("yyyy-MM-dd");
        gbc.gridy = row++;
        cardPanel.add(jDatePay, gbc);

        gbc.gridy = row++;
        cardPanel.add(new JLabel("Monto Base *"), gbc);
        txtBase_amount = new JTextField();
        txtBase_amount.putClientProperty("JTextField.placeholderText", "Costo base del plan");
        gbc.gridy = row++;
        cardPanel.add(txtBase_amount, gbc);

        gbc.gridy = row++;
        cardPanel.add(new JLabel("Descuento ($)"), gbc);
        txtDiscount = new JTextField();
        txtDiscount.putClientProperty("JTextField.placeholderText", "Descuento aplicado en $");
        gbc.gridy = row++;
        cardPanel.add(txtDiscount, gbc);

        gbc.gridy = row++;
        cardPanel.add(new JLabel("Método de Pago *"), gbc);
        comboBoxPayment_method = new JComboBox<>();
        gbc.gridy = row++;
        cardPanel.add(comboBoxPayment_method, gbc);

        gbc.gridy = row++;
        cardPanel.add(new JLabel("Estado de Pago *"), gbc);
        comboBoxPayment_status = new JComboBox<>();
        gbc.gridy = row++;
        cardPanel.add(comboBoxPayment_status, gbc);

        // Form Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        actionPanel.setOpaque(false);

        btnSave = buttonFactory.createPrimaryButton("💾 Guardar");
        btnSearch = buttonFactory.createSecondaryButton("🔍 Buscar DNI");

        actionPanel.add(btnSave);
        actionPanel.add(btnSearch);

        gbc.gridy = row++;
        gbc.insets = new Insets(12, 0, 4, 0);
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
        titleList = new JLabel("Lista de Pagos");
        titleList.setFont(new Font("Segoe UI", Font.BOLD, 16));
        listHeader.add(titleList, BorderLayout.WEST);

        // Filter actions top-right
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

        btnAll = buttonFactory.createSecondaryButton("Todos");
        btnConfirmed = buttonFactory.createSecondaryButton("Confirmados");
        btnPending = buttonFactory.createSecondaryButton("Pendientes");
        btnModify = buttonFactory.createPrimaryButton("📝 Modificar");

        filterPanel.add(btnAll);
        filterPanel.add(btnConfirmed);
        filterPanel.add(btnPending);
        filterPanel.add(btnModify);
        listHeader.add(filterPanel, BorderLayout.EAST);

        listPanel.add(listHeader, BorderLayout.NORTH);

        // Table
        tableListPayments = new JTable();
        tableListPayments.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableListPayments.setShowHorizontalLines(true);
        tableListPayments.setShowVerticalLines(false);

        JScrollPane scrollPaneTable = new JScrollPane(tableListPayments);
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

    public void initPayTable() {
        tableModelPays = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableListPayments.setModel(tableModelPays);
        tableModelPays.setColumnIdentifiers(new Object[]{
                "ID", "DNI", "Cliente", "Plan", "Periodo", "Fecha de Pago", "Precio Base", "Descuento", "Precio Final",
                "Método de Pago", "Estado"
        });
    }

    public void resetForm() {
        txtDni.setText("");
        txtBase_amount.setText("");
        txtDiscount.setText("");

        jDatePeriod.setDate(null);
        jDatePay.setDate(null);

        if (comboBoxPayment_method.getItemCount() > 0) comboBoxPayment_method.setSelectedIndex(0);
        if (comboBoxPayment_status.getItemCount() > 0) comboBoxPayment_status.setSelectedIndex(0);

        titleCharge.setText("Nuevo Pago");
        isEditMode = false;
        editingPaymentId = null;
        txtDni.setEnabled(true);

        // Restaurar bordes FlatLaf
        txtDni.setBorder(UIManager.getBorder("TextField.border"));
        txtBase_amount.setBorder(UIManager.getBorder("TextField.border"));
        txtDiscount.setBorder(UIManager.getBorder("TextField.border"));
        comboBoxPayment_method.setBorder(UIManager.getBorder("ComboBox.border"));
        comboBoxPayment_status.setBorder(UIManager.getBorder("ComboBox.border"));
    }

    public void showLoadingTable() {
        tableModelPays.setRowCount(0);
        tableModelPays.addRow(new Object[] {
                "", "", "Cargando datos...", "", "", "", "", "", "", "", ""
        });
        enableSearchButton(false);
    }

    public void updateTable(List<PaymentDTO> payments) {
        tableModelPays.setRowCount(0);
        for (PaymentDTO dto : payments) {
            tableModelPays.addRow(new Object[] {
                    dto.getIdPayment() != null ? dto.getIdPayment().toString() : "",
                    dto.getDocumentId() != null ? dto.getDocumentId().toString() : "",
                    dto.getNameClient(),
                    dto.getNamePlan(),
                    FormatterUtils.formatDate(dto.getPeriod()),
                    FormatterUtils.formatDate(dto.getPaymentDate()),
                    FormatterUtils.formatCurrency(dto.getBaseAmount()),
                    FormatterUtils.formatCurrency(dto.getDiscountApplied()),
                    FormatterUtils.formatCurrency(dto.getFinalAmount()),
                    dto.getPaymentMethod(),
                    dto.getPaymentStatus()
            });
        }
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

    // Getters y Setters
    public JTextField getTxtDni() { return txtDni; }
    public JTextField getTxtBase_amount() { return txtBase_amount; }
    public JTextField getTxtDiscount() { return txtDiscount; }
    public JComboBox<String> getComboBoxPayment_method() { return comboBoxPayment_method; }
    public JComboBox<String> getComboBoxPayment_status() { return comboBoxPayment_status; }
    
    public com.toedter.calendar.JDateChooser getjDatePay() { return jDatePay; }
    public com.toedter.calendar.JDateChooser getjDatePeriod() { return jDatePeriod; }
    public JTable getTableListPayments() { return tableListPayments; }
    
    public JLabel getTitleCharge() { return titleCharge; }
    public JLabel getTitleList() { return titleList; }
    
    public JButton getBtnSave() { return btnSave; }
    public JButton getBtnSearch() { return btnSearch; }
    public JButton getBtnClean() { return btnClean; }
    public JButton getBtnModify() { return btnModify; }
    public JButton getBtnAll() { return btnAll; }
    public JButton getBtnPending() { return btnPending; }
    public JButton getBtnConfirmed() { return btnConfirmed; }

    public boolean isEditMode() { return isEditMode; }
    public void setEditMode(boolean editMode) { isEditMode = editMode; }
    
    public Long getEditingPaymentId() { return editingPaymentId; }
    public void setEditingPaymentId(Long editingPaymentId) { this.editingPaymentId = editingPaymentId; }

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
