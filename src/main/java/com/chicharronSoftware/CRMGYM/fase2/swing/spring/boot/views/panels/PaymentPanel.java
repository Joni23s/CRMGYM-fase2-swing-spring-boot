package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PaymentMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PaymentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentPanel extends JPanel implements Scrollable {

    private DefaultTableModel tableModelPays;
    private final PaymentService paymentService;
    private final ClientService clientService;

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
    private JButton btnOverdue;

    @Autowired
    public PaymentPanel(PaymentService paymentService, ClientService clientService) {
        this.paymentService = paymentService;
        this.clientService = clientService;

        initComponentsHandCoded();
        initPayTable();
        loadPaymentMethod();
        loadPaymentStatus();
        resetForm();
    }

    private void initComponentsHandCoded() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- 1. FORM PANEL (Oeste) ---
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setPreferredSize(new Dimension(320, 0));

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor"), 1, true),
                "Registro de Pago", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), UIManager.getColor("Label.foreground")
            ),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        cardPanel.putClientProperty("FlatLaf.style", "arc: 12; background: $Component.background;");

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

        btnSave = new JButton("💾 Guardar");
        btnSave.putClientProperty("JButton.buttonType", "accent");
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> btnSaveActionPerformed(e));

        btnSearch = new JButton("🔍 Buscar DNI");
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(e -> btnSearchActionPerformed(e));

        actionPanel.add(btnSave);
        actionPanel.add(btnSearch);

        gbc.gridy = row++;
        gbc.insets = new Insets(12, 0, 4, 0);
        cardPanel.add(actionPanel, gbc);

        btnClean = new JButton("🧹 Limpiar Campos");
        btnClean.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClean.addActionListener(e -> btnCleanActionPerformed(e));
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

        btnAll = new JButton("Todos");
        btnAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAll.addActionListener(e -> btnAllActionPerformed(e));

        btnConfirmed = new JButton("Confirmados");
        btnConfirmed.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmed.addActionListener(e -> btnConfirmedActionPerformed(e));

        btnPending = new JButton("Pendientes");
        btnPending.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPending.addActionListener(e -> btnPendingActionPerformed(e));

        btnModify = new JButton("📝 Modificar");
        btnModify.putClientProperty("JButton.buttonType", "accent");
        btnModify.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnModify.addActionListener(e -> btnModifyActionPerformed(e));

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

    private void initPayTable() {
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
        loadPaymentsToTable(paymentService.getAllPaymentsDTO());
    }

    private static final java.time.format.DateTimeFormatter DATE_FORMATTER = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "";
        return "$ " + amount.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private void loadPaymentsToTable(List<PaymentDTO> payments) {
        tableModelPays.setRowCount(0);
        for (PaymentDTO dto : payments) {
            tableModelPays.addRow(new Object[] {
                    dto.getIdPayment() != null ? dto.getIdPayment().toString() : "",
                    dto.getDocumentId() != null ? dto.getDocumentId().toString() : "",
                    dto.getNameClient(),
                    dto.getNamePlan(),
                    formatDate(dto.getPeriod()),
                    formatDate(dto.getPaymentDate()),
                    formatMoney(dto.getBaseAmount()),
                    formatMoney(dto.getDiscountApplied()),
                    formatMoney(dto.getFinalAmount()),
                    dto.getPaymentMethod(),
                    dto.getPaymentStatus()
            });
        }
    }

    private void loadPaymentMethod() {
        comboBoxPayment_method.removeAllItems();
        comboBoxPayment_method.addItem("Seleccione un Método *");
        for (PaymentMethod method : PaymentMethod.values()) {
            comboBoxPayment_method.addItem(method.getDescripcion());
        }
    }

    private void loadPaymentStatus() {
        comboBoxPayment_status.removeAllItems();
        comboBoxPayment_status.addItem("Seleccione un Estado *");
        for (PaymentStatus status : PaymentStatus.values()) {
            comboBoxPayment_status.addItem(status.getDescripcion());
        }
    }

    private void resetForm() {
        txtDni.setText("");
        txtBase_amount.setText("");
        txtDiscount.setText("");

        jDatePeriod.setDate(null);
        jDatePay.setDate(null);

        comboBoxPayment_method.setSelectedIndex(0);
        comboBoxPayment_status.setSelectedIndex(0);

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

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            String dniText = txtDni.getText().trim();
            Date periodDate = jDatePeriod.getDate();
            Date paymentDate = jDatePay.getDate();
            String baseAmountText = txtBase_amount.getText().trim();
            String discountText = txtDiscount.getText().trim();
            String paymentMethodDesc = (String) comboBoxPayment_method.getSelectedItem();
            String paymentStatusDesc = (String) comboBoxPayment_status.getSelectedItem();

            if (dniText.isEmpty() || periodDate == null || paymentDate == null || baseAmountText.isEmpty() ||
                comboBoxPayment_method.getSelectedIndex() == 0 || comboBoxPayment_status.getSelectedIndex() == 0) {
                
                // Resaltar campos vacíos en rojo
                if (dniText.isEmpty()) txtDni.setBorder(BorderFactory.createLineBorder(Color.RED));
                if (baseAmountText.isEmpty()) txtBase_amount.setBorder(BorderFactory.createLineBorder(Color.RED));
                if (comboBoxPayment_method.getSelectedIndex() == 0) comboBoxPayment_method.setBorder(BorderFactory.createLineBorder(Color.RED));
                if (comboBoxPayment_status.getSelectedIndex() == 0) comboBoxPayment_status.setBorder(BorderFactory.createLineBorder(Color.RED));
                
                JOptionPane.showMessageDialog(this, "Por favor completá todos los campos obligatorios (*).", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int dni;
            try {
                dni = Integer.parseInt(dniText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El DNI debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Optional<Client> clientOpt = clientService.findById(dni);
            if (clientOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró un cliente con DNI " + dni, "Cliente no encontrado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Client client = clientOpt.get();

            baseAmountText = baseAmountText.replace(",", ".").replaceAll("[^0-9.]", "");
            discountText = discountText.replace(",", ".").replaceAll("[^0-9.]", "");

            BigDecimal baseAmount;
            BigDecimal discount;

            try {
                baseAmount = new BigDecimal(baseAmountText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El monto base no es válido.", "Error en monto base", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                discount = discountText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(discountText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El descuento no es válido.", "Error en descuento", JOptionPane.ERROR_MESSAGE);
                return;
            }

            PaymentMethod paymentMethod = Arrays.stream(PaymentMethod.values())
                    .filter(pm -> pm.getDescripcion().equals(paymentMethodDesc))
                    .findFirst()
                    .orElse(PaymentMethod.EFECTIVO);

            PaymentStatus paymentStatus = Arrays.stream(PaymentStatus.values())
                    .filter(ps -> ps.getDescripcion().equals(paymentStatusDesc))
                    .findFirst()
                    .orElse(PaymentStatus.PENDIENTE);

            BigDecimal finalAmount = baseAmount.subtract(discount);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "El monto final no puede ser negativo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate period = periodDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate payment = paymentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Payment paymentEntity;
            if (isEditMode && editingPaymentId != null) {
                paymentEntity = paymentService.findById(editingPaymentId)
                        .orElseThrow(() -> new RuntimeException("No se encontró el pago a modificar."));
                paymentEntity.setPeriod(period);
                paymentEntity.setPaymentDate(payment);
                paymentEntity.setBaseAmount(baseAmount);
                paymentEntity.setDiscountApplied(discount);
                paymentEntity.setFinalAmount(finalAmount);
                paymentEntity.setPaymentMethod(paymentMethod);
                paymentEntity.setPaymentStatus(paymentStatus);
                paymentEntity.setClient(client);
            } else {
                paymentEntity = Payment.builder()
                        .period(period)
                        .paymentDate(payment)
                        .baseAmount(baseAmount)
                        .discountApplied(discount)
                        .finalAmount(finalAmount)
                        .paymentMethod(paymentMethod)
                        .paymentStatus(paymentStatus)
                        .client(client)
                        .build();
            }

            paymentService.save(paymentEntity);
            
            String msg = isEditMode ? "Pago actualizado correctamente." : "Pago guardado correctamente.";
            JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al guardar el pago: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        resetForm();
        loadPaymentsToTable(paymentService.getAllPaymentsDTO());
    }

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {
        String dniText = txtDni.getText().trim();
        if (dniText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un DNI válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int dniNumber = Integer.parseInt(dniText);
            Optional<Client> client = clientService.findById(dniNumber);

            if (client.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró un cliente con ese DNI.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Optional<Payment> lastPayment = paymentService.findLastPaymentByClientId(client.get().getDocumentId());

            if (lastPayment.isPresent()) {
                txtBase_amount.setText(String.valueOf(lastPayment.get().getBaseAmount()));
                
                // FIX: discountApplied en vez de lastPayment.getClass()
                txtDiscount.setText(lastPayment.get().getDiscountApplied() != null ? String.valueOf(lastPayment.get().getDiscountApplied()) : "0");
                
                comboBoxPayment_method.setSelectedItem(lastPayment.get().getPaymentMethod().getDescripcion());
                comboBoxPayment_status.setSelectedItem(lastPayment.get().getPaymentStatus().getDescripcion());
                
                jDatePeriod.setDate(java.sql.Date.valueOf(lastPayment.get().getPeriod()));
                jDatePay.setDate(java.sql.Date.valueOf(lastPayment.get().getPaymentDate()));
            } else {
                // Si no hay pagos, precargar el costo de su plan actual
                if (client.get().getCurrentPlan() != null) {
                    txtBase_amount.setText(String.valueOf(client.get().getCurrentPlan().getValue()));
                }
                txtDiscount.setText("0");
                jDatePay.setDate(new Date());
                jDatePeriod.setDate(new Date());
                JOptionPane.showMessageDialog(this, "El cliente no tiene pagos registrados. Se precargó el costo de su plan actual.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }

            List<Payment> payments = paymentService.findAllByClientId(client.get().getDocumentId());
            loadPaymentsToTable(payments.stream()
                    .map(PaymentMapper::toDTO)
                    .collect(Collectors.toList()));

            titleCharge.setText("Pago de " + client.get().getName());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El DNI debe ser numérico.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar el cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void btnModifyActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tableListPayments.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un pago primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long paymentId = Long.parseLong(tableListPayments.getValueAt(selectedRow, 0).toString());
        Payment payment = paymentService.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("No se encontró el pago."));

        txtDni.setText(String.valueOf(payment.getClient().getDocumentId()));
        txtBase_amount.setText(String.valueOf(payment.getBaseAmount()));
        txtDiscount.setText(payment.getDiscountApplied() != null ? payment.getDiscountApplied().toString() : "0");
        comboBoxPayment_method.setSelectedItem(payment.getPaymentMethod().getDescripcion());
        comboBoxPayment_status.setSelectedItem(payment.getPaymentStatus().getDescripcion());

        jDatePeriod.setDate(java.sql.Date.valueOf(payment.getPeriod()));
        jDatePay.setDate(java.sql.Date.valueOf(payment.getPaymentDate()));

        isEditMode = true;
        // FIX: guarda el ID del pago que se edita para actualizar el mismo registro
        editingPaymentId = paymentId;
        txtDni.setEnabled(false);
        titleCharge.setText("Modificar Pago");
    }

    private void btnAllActionPerformed(java.awt.event.ActionEvent evt) {
        titleList.setText("Todos los Pagos");
        loadPaymentsToTable(paymentService.getAllPaymentsDTO());
    }

    private void btnConfirmedActionPerformed(java.awt.event.ActionEvent evt) {
        titleList.setText("Pagos Confirmados");
        loadPaymentsToTable(paymentService.findByStatus(PaymentStatus.CONFIRMADO).stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList()));
    }

    private void btnPendingActionPerformed(java.awt.event.ActionEvent evt) {
        titleList.setText("Pagos Pendientes");
        loadPaymentsToTable(paymentService.findByStatus(PaymentStatus.PENDIENTE).stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList()));
    }

    private void btnCleanActionPerformed(java.awt.event.ActionEvent evt) {
        resetForm();
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
