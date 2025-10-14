package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PaymentMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PaymentService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.utils.PromptSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PaymentPanel extends javax.swing.JPanel {

    private DefaultTableModel tableModelPays;
    private final PaymentService paymentService;
    private final ClientService clientService;

    private Border defaultBorder;
    private boolean isEditMode = false;
    private int editingPaymentId = -1;

    @Autowired
    public PaymentPanel(PaymentService paymentService, ClientService clientService) {
        this.paymentService = paymentService;
        this.clientService = clientService;
        initComponents();
        initPayTable();
        loadPayment_method();
        loadPayment_status();
    }

    private void initPayTable() {
        tableModelPays = new DefaultTableModel();
        tableListPayments.setModel(tableModelPays);
        // Definir cabeceras de la tabla
        tableModelPays.setColumnIdentifiers(new Object[]{
                "ID", "DNI", "Cliente", "Plan", "Periodo", "Fecha de Pago", "Precio Base", "Descuento", "Precio Final",
                "Método de Pago", "Estado"
        });
        loadPaymentsToTable(paymentService.getAllPaymentsDTO());
    }

    /**
     * Carga la lista de clientes en la tabla.
     */
    private void loadPaymentsToTable(List<PaymentDTO> payments) {
        //Pagos desde el service
        tableModelPays.setRowCount(0);

        for (PaymentDTO dto : payments) {
            tableModelPays.addRow(new Object[] {
                    dto.getIdPayment(),
                    dto.getDocumentId(),
                    dto.getNameClient(),
                    dto.getNamePlan(),
                    dto.getPeriod(),
                    dto.getPaymentDate(),
                    dto.getBaseAmount(),
                    dto.getDiscountApplied(),
                    dto.getFinalAmount(),
                    dto.getPaymentMethod(),
                    dto.getPaymentStatus()
            });
        }
    }

    //Carga todos los metodos de pagos disponibles en el comboBox.
        private void loadPayment_method() {
        comboBoxPayment_method.removeAllItems();
        comboBoxPayment_method.addItem("Seleccione un Método *");
        for (PaymentMethod method : PaymentMethod.values()) {
            comboBoxPayment_method.addItem(method.getDescripcion());
        }
    }

    //Carga todos los estados de pagos disponibles en el comboBox.
    private void loadPayment_status() {
        comboBoxPayment_status.removeAllItems();
        comboBoxPayment_status.addItem("Seleccione un Estado *");
        for (PaymentStatus status : PaymentStatus.values()) {
            comboBoxPayment_status.addItem(status.getDescripcion());
        }
    }

    private void resetForm() {
        PromptSupport.setPrompt("Ingrese el DNI *", txtDni);
        PromptSupport.setPrompt("Monto del Plan en uso", txtBase_amount);
        PromptSupport.setPrompt("Ingrese el descuento expresado en $", txtDiscount);

        // Reiniciar los JDateChooser
        jDatePeriod.setDate(null);
        jDatePay.setDate(null);

        // Reiniciar los combos
        comboBoxPayment_method.setSelectedIndex(0);
        comboBoxPayment_status.setSelectedIndex(0);

        // Resetear título y estados
        titleCharge.setText("Nuevo Pago");
        isEditMode = false;

        // Restaurar bordes por defecto
        txtDni.setBorder(defaultBorder);
        txtBase_amount.setBorder(defaultBorder);
        txtDiscount.setBorder(defaultBorder);
        comboBoxPayment_method.setBorder(defaultBorder);
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelMainPay = new javax.swing.JPanel();
        titleMain = new javax.swing.JLabel();
        panelNewPay = new javax.swing.JPanel();
        titleCharge = new javax.swing.JLabel();
        labelName = new javax.swing.JLabel();
        txtDni = new javax.swing.JTextField();
        labelPeriod = new javax.swing.JLabel();
        labelDatePay = new javax.swing.JLabel();
        labelPayment_method = new javax.swing.JLabel();
        txtBase_amount = new javax.swing.JTextField();
        labelBase_amount = new javax.swing.JLabel();
        txtDiscount = new javax.swing.JTextField();
        labelDiscount = new javax.swing.JLabel();
        btnSave = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        btnClean = new javax.swing.JButton();
        comboBoxPayment_method = new javax.swing.JComboBox<>();
        jDatePay = new com.toedter.calendar.JDateChooser();
        jDatePeriod = new com.toedter.calendar.JDateChooser();
        labelPayment_status = new javax.swing.JLabel();
        comboBoxPayment_status = new javax.swing.JComboBox<>();
        panelTable = new javax.swing.JPanel();
        scrollPaneTable = new javax.swing.JScrollPane();
        tableListPayments = new javax.swing.JTable();
        titleList = new javax.swing.JLabel();
        btnPending = new javax.swing.JButton();
        btnConfirmed = new javax.swing.JButton();
        btnOverdue = new javax.swing.JButton();
        btnModify = new javax.swing.JButton();
        btnAll = new javax.swing.JButton();
        btnGenerate = new javax.swing.JButton();

        panelMainPay.setBackground(new java.awt.Color(102, 102, 102));

        titleMain.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        titleMain.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleMain.setText("Menú de Pagos");

        titleCharge.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        titleCharge.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleCharge.setText("Nuevo Pago");

        labelName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelName.setText("Cliente (DNI)");

        txtDni.setText("Ingrese el DNI *");

        labelPeriod.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPeriod.setText("Período");

        labelDatePay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDatePay.setText("Fecha Pago");

        labelPayment_method.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPayment_method.setText("Método de Pago");

        txtBase_amount.setText("Monto del Plan en uso");

        labelBase_amount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelBase_amount.setText("Monto Base");

        txtDiscount.setText("Ingrese el descuento expresado en $");

        labelDiscount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDiscount.setText("Descuento");

        btnSave.setText("Guardar");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnSearch.setText("Buscar");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnClean.setText("Limpiar");
        btnClean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCleanActionPerformed(evt);
            }
        });

        comboBoxPayment_method.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        labelPayment_status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelPayment_status.setText("Estado de Pago");

        comboBoxPayment_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout panelNewPayLayout = new javax.swing.GroupLayout(panelNewPay);
        panelNewPay.setLayout(panelNewPayLayout);
        panelNewPayLayout.setHorizontalGroup(
            panelNewPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNewPayLayout.createSequentialGroup()
                .addGroup(panelNewPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelNewPayLayout.createSequentialGroup()
                        .addGap(77, 77, 77)
                        .addComponent(btnClean)
                        .addGap(67, 67, 67))
                    .addGroup(panelNewPayLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelNewPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(titleCharge, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelPeriod, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelDatePay, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDni, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelPayment_method, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelBase_amount, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtBase_amount, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(labelDiscount, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDiscount, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(comboBoxPayment_method, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panelNewPayLayout.createSequentialGroup()
                                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jDatePay, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jDatePeriod, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(labelPayment_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(comboBoxPayment_status, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panelNewPayLayout.setVerticalGroup(
            panelNewPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelNewPayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleCharge, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDni, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelPeriod)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDatePeriod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelDatePay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDatePay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelBase_amount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBase_amount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelDiscount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelPayment_method)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxPayment_method, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labelPayment_status)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxPayment_status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addGroup(panelNewPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnClean)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        tableListPayments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scrollPaneTable.setViewportView(tableListPayments);

        titleList.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        titleList.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleList.setText("Lista de Pagos");

        btnPending.setText("Pendientes");
        btnPending.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPendingActionPerformed(evt);
            }
        });

        btnConfirmed.setText("Confirmados");
        btnConfirmed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmedActionPerformed(evt);
            }
        });

        btnOverdue.setText("Vencidos");

        btnModify.setText("Modificar");
        btnModify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModifyActionPerformed(evt);
            }
        });

        btnAll.setText("Todos");
        btnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAllActionPerformed(evt);
            }
        });

        btnGenerate.setText("Generar");
        btnGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTableLayout = new javax.swing.GroupLayout(panelTable);
        panelTable.setLayout(panelTableLayout);
        panelTableLayout.setHorizontalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTableLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(titleList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPaneTable, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
                .addGap(185, 185, 185))
            .addGroup(panelTableLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelTableLayout.createSequentialGroup()
                        .addComponent(btnAll, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnModify, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelTableLayout.createSequentialGroup()
                        .addComponent(btnConfirmed)
                        .addGap(18, 18, 18)
                        .addComponent(btnPending, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOverdue, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        panelTableLayout.setVerticalGroup(
            panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleList, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(scrollPaneTable, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmed, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPending, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnOverdue, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelTableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAll, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModify, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGenerate, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelMainPayLayout = new javax.swing.GroupLayout(panelMainPay);
        panelMainPay.setLayout(panelMainPayLayout);
        panelMainPayLayout.setHorizontalGroup(
            panelMainPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelMainPayLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelNewPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelMainPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleMain, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                    .addGroup(panelMainPayLayout.createSequentialGroup()
                        .addComponent(panelTable, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelMainPayLayout.setVerticalGroup(
            panelMainPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMainPayLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMainPayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelNewPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelMainPayLayout.createSequentialGroup()
                        .addComponent(titleMain)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMainPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelMainPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        try {
            // --- Captura de datos ---
            String dniText = txtDni.getText().trim();
            Date periodDate = jDatePeriod.getDate();
            Date paymentDate = jDatePay.getDate();
            String baseAmountText = txtBase_amount.getText().trim();
            String discountText = txtDiscount.getText().trim();
            String paymentMethodDesc = (String) comboBoxPayment_method.getSelectedItem();
            String paymentStatusDesc = (String) comboBoxPayment_status.getSelectedItem();

            // --- Validaciones básicas ---
            if (dniText.isEmpty() || periodDate == null || paymentDate == null || baseAmountText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Por favor completá todos los campos obligatorios (*).",
                        "Campos incompletos",
                        JOptionPane.WARNING_MESSAGE);
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

            // --- Limpieza y validación de montos ---
            baseAmountText = baseAmountText.replace(",", ".").replaceAll("[^0-9.]", "");
            discountText = discountText.replace(",", ".").replaceAll("[^0-9.]", "");

            BigDecimal baseAmount;
            BigDecimal discount;

            try {
                baseAmount = new BigDecimal(baseAmountText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El monto base no es válido. Solo se permiten números (por ejemplo: 1200.50)", "Error en monto base", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                discount = discountText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(discountText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El descuento no es válido. Solo se permiten números (por ejemplo: 100 o 50.25)", "Error en descuento", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // --- Conversión de enums desde descripción ---
            PaymentMethod paymentMethod = Arrays.stream(PaymentMethod.values())
                    .filter(pm -> pm.getDescripcion().equals(paymentMethodDesc))
                    .findFirst()
                    .orElse(PaymentMethod.EFECTIVO);

            PaymentStatus paymentStatus = Arrays.stream(PaymentStatus.values())
                    .filter(ps -> ps.getDescripcion().equals(paymentStatusDesc))
                    .findFirst()
                    .orElse(PaymentStatus.PENDIENTE);

            // --- Cálculo de monto final ---
            BigDecimal finalAmount = baseAmount.subtract(discount != null ? discount : BigDecimal.ZERO);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "El monto final no puede ser negativo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // --- Conversión de fechas ---
            LocalDate period = periodDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate payment = paymentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // --- Crear y guardar Payment ---
            Payment paymentEntity = Payment.builder()
                    .period(period)
                    .paymentDate(payment)
                    .baseAmount(baseAmount)
                    .discountApplied(discount)
                    .finalAmount(finalAmount)
                    .paymentMethod(paymentMethod)
                    .paymentStatus(paymentStatus)
                    .client(client)
                    .build();

            paymentService.save(paymentEntity);

            JOptionPane.showMessageDialog(this, "Pago guardado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // --- Reset ---
            resetForm();
            titleCharge.setText("Nuevo Pago");
            isEditMode = false;

            // Actualizar tabla
            loadPaymentsToTable(paymentService.getAllPaymentsDTO());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al guardar el pago: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnSearchActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String dni = txtDni.getText().trim();

        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un DNI válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int dniNumber = Integer.parseInt(txtDni.getText().trim()) ;
        try {
            // Buscar cliente
            Optional<Client> client = clientService.findById(dniNumber);

            if (client.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró un cliente con ese DNI.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2️⃣ Buscar último pago
            Optional<Payment> lastPayment = paymentService.findLastPaymentByClientId(client.get().getDocumentId());

            if (lastPayment.isPresent()) {
                // Poblar campos
                txtBase_amount.setText(String.valueOf(lastPayment.get().getBaseAmount()));
                txtDiscount.setText(String.valueOf(lastPayment.getClass()));
                comboBoxPayment_method.setSelectedItem(lastPayment.get().getPaymentMethod());
                comboBoxPayment_status.setSelectedItem(lastPayment.get().getPaymentStatus());
                jDatePeriod.setDate(java.sql.Date.valueOf(lastPayment.get().getPeriod()));
                jDatePay.setDate(java.sql.Date.valueOf(lastPayment.get().getPaymentDate()));

            } else {
                JOptionPane.showMessageDialog(this, "El cliente no tiene pagos registrados.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
            }

            // 3️⃣ Cargar historial de pagos
            List<Payment> payments = paymentService.findAllByClientId(client.get().getDocumentId());
            loadPaymentsToTable(payments.stream()
                    .map(PaymentMapper::toDTO)
                    .collect(Collectors.toList()));

            titleCharge.setText("Pago de " + client.get().getName());
            isEditMode = true;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar el cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnCleanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCleanActionPerformed
        resetForm();
    }//GEN-LAST:event_btnCleanActionPerformed

    private void btnPendingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPendingActionPerformed
        titleList.setText("Pagos Pendientes");
        loadPaymentsToTable(paymentService.findByStatus(PaymentStatus.PENDIENTE).stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList()));
    }//GEN-LAST:event_btnPendingActionPerformed

    private void btnConfirmedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmedActionPerformed
        titleList.setText("Pagos Confirmados");
        loadPaymentsToTable(paymentService.findByStatus(PaymentStatus.CONFIRMADO).stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList()));
    }//GEN-LAST:event_btnConfirmedActionPerformed

    private void btnModifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModifyActionPerformed
        int selectedRow = tableListPayments.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un pago primero.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener datos de la tabla
        Long paymentId = Long.parseLong(tableListPayments.getValueAt(selectedRow, 0).toString());

        // Buscar el pago
        Payment payment = paymentService.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("No se encontró el pago."));

        // Cargar campos en el formulario
        txtDni.setText(String.valueOf(payment.getClient().getDocumentId()));
        txtBase_amount.setText(String.valueOf(payment.getBaseAmount()));
        txtDiscount.setText(payment.getDiscountApplied() != null ? payment.getDiscountApplied().toString() : "");
        comboBoxPayment_method.setSelectedItem(payment.getPaymentMethod());
        comboBoxPayment_status.setSelectedItem(payment.getPaymentStatus());

        jDatePeriod.setDate(java.sql.Date.valueOf(payment.getPeriod()));
        jDatePay.setDate(java.sql.Date.valueOf(payment.getPaymentDate()));

        // Activar modo edición
        isEditMode = true;
//        editingPaymentId = paymentId; // (declaralo como variable de instancia)
        titleCharge.setText("Modificar Pago");
    }//GEN-LAST:event_btnModifyActionPerformed

    private void btnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAllActionPerformed
        titleList.setText("Todos los Pagos");
        loadPaymentsToTable(paymentService.findAll().stream()
                .map(PaymentMapper::toDTO)
                .collect(Collectors.toList()));
    }//GEN-LAST:event_btnAllActionPerformed

    private void btnGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGenerateActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAll;
    private javax.swing.JButton btnClean;
    private javax.swing.JButton btnConfirmed;
    private javax.swing.JButton btnGenerate;
    private javax.swing.JButton btnModify;
    private javax.swing.JButton btnOverdue;
    private javax.swing.JButton btnPending;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> comboBoxPayment_method;
    private javax.swing.JComboBox<String> comboBoxPayment_status;
    private com.toedter.calendar.JDateChooser jDatePay;
    private com.toedter.calendar.JDateChooser jDatePeriod;
    private javax.swing.JLabel labelBase_amount;
    private javax.swing.JLabel labelDatePay;
    private javax.swing.JLabel labelDiscount;
    private javax.swing.JLabel labelName;
    private javax.swing.JLabel labelPayment_method;
    private javax.swing.JLabel labelPayment_status;
    private javax.swing.JLabel labelPeriod;
    private javax.swing.JPanel panelMainPay;
    private javax.swing.JPanel panelNewPay;
    private javax.swing.JPanel panelTable;
    private javax.swing.JScrollPane scrollPaneTable;
    private javax.swing.JTable tableListPayments;
    private javax.swing.JLabel titleCharge;
    private javax.swing.JLabel titleList;
    private javax.swing.JLabel titleMain;
    private javax.swing.JTextField txtBase_amount;
    private javax.swing.JTextField txtDiscount;
    private javax.swing.JTextField txtDni;
    // End of variables declaration//GEN-END:variables
}
