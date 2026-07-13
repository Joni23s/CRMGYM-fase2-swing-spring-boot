package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.presenter;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PaymentDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PaymentMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Payment;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentMethod;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.enums.PaymentStatus;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PaymentService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.PaymentPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import java.awt.Color;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class PaymentPresenter {

    private final PaymentPanel view;
    private final PaymentService paymentService;
    private final ClientService clientService;

    @Autowired
    public PaymentPresenter(PaymentPanel view, PaymentService paymentService, ClientService clientService) {
        this.view = view;
        this.paymentService = paymentService;
        this.clientService = clientService;
    }

    @PostConstruct
    public void init() {
        // Wiring events
        view.getBtnSave().addActionListener(e -> onSave());
        view.getBtnSearch().addActionListener(e -> onSearchDni());
        view.getBtnClean().addActionListener(e -> view.resetForm());
        
        view.getBtnAll().addActionListener(e -> onAll());
        view.getBtnConfirmed().addActionListener(e -> onConfirmed());
        view.getBtnPending().addActionListener(e -> onPending());
        view.getBtnModify().addActionListener(e -> onModify());

        // Initialize view structures and load initial data
        view.initPayTable();
        loadPaymentMethod();
        loadPaymentStatus();
        view.resetForm();
        loadPaymentsToTableAsync(() -> paymentService.getAllPaymentsDTO());
    }

    private void onSave() {
        try {
            String dniText = view.getTxtDni().getText().trim();
            Date periodDate = view.getjDatePeriod().getDate();
            Date paymentDate = view.getjDatePay().getDate();
            String baseAmountText = view.getTxtBase_amount().getText().trim();
            String discountText = view.getTxtDiscount().getText().trim();
            String paymentMethodDesc = (String) view.getComboBoxPayment_method().getSelectedItem();
            String paymentStatusDesc = (String) view.getComboBoxPayment_status().getSelectedItem();

            if (dniText.isEmpty() || periodDate == null || paymentDate == null || baseAmountText.isEmpty() ||
                view.getComboBoxPayment_method().getSelectedIndex() == 0 || view.getComboBoxPayment_status().getSelectedIndex() == 0) {
                
                // Resaltar campos vacíos en rojo
                if (dniText.isEmpty()) view.getTxtDni().setBorder(BorderFactory.createLineBorder(Color.RED));
                if (baseAmountText.isEmpty()) view.getTxtBase_amount().setBorder(BorderFactory.createLineBorder(Color.RED));
                if (view.getComboBoxPayment_method().getSelectedIndex() == 0) view.getComboBoxPayment_method().setBorder(BorderFactory.createLineBorder(Color.RED));
                if (view.getComboBoxPayment_status().getSelectedIndex() == 0) view.getComboBoxPayment_status().setBorder(BorderFactory.createLineBorder(Color.RED));
                
                view.showWarning("Por favor completá todos los campos obligatorios (*).", "Campos incompletos");
                return;
            }

            int dni;
            try {
                dni = Integer.parseInt(dniText);
            } catch (NumberFormatException ex) {
                view.showError("El DNI debe ser un número válido.", "Error");
                return;
            }

            Optional<Client> clientOpt = clientService.findById(dni);
            if (clientOpt.isEmpty()) {
                view.showError("No se encontró un cliente con DNI " + dni, "Cliente no encontrado");
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
                view.showError("El monto base no es válido.", "Error en monto base");
                return;
            }

            try {
                discount = discountText.isEmpty() ? BigDecimal.ZERO : new BigDecimal(discountText);
            } catch (NumberFormatException ex) {
                view.showError("El descuento no es válido.", "Error en descuento");
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
                view.showError("El monto final no puede ser negativo.", "Error");
                return;
            }

            LocalDate period = periodDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate payment = paymentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Payment paymentEntity;
            if (view.isEditMode() && view.getEditingPaymentId() != null) {
                paymentEntity = paymentService.findById(view.getEditingPaymentId())
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
            
            String msg = view.isEditMode() ? "Pago actualizado correctamente." : "Pago guardado correctamente.";
            view.showSuccess(msg, "Éxito");

        } catch (Exception ex) {
            view.showError("Ocurrió un error al guardar el pago: " + ex.getMessage(), "Error");
        }

        view.resetForm();
        loadPaymentsToTableAsync(() -> paymentService.getAllPaymentsDTO());
    }

    private void onSearchDni() {
        String dniText = view.getTxtDni().getText().trim();
        if (dniText.isEmpty()) {
            view.showWarning("Ingrese un DNI válido.", "Advertencia");
            return;
        }

        try {
            int dniNumber = Integer.parseInt(dniText);
            Optional<Client> client = clientService.findById(dniNumber);

            if (client.isEmpty()) {
                view.showError("No se encontró un cliente con ese DNI.", "Error");
                return;
            }

            Optional<Payment> lastPayment = paymentService.findLastPaymentByClientId(client.get().getDocumentId());

            if (lastPayment.isPresent()) {
                view.getTxtBase_amount().setText(String.valueOf(lastPayment.get().getBaseAmount()));
                view.getTxtDiscount().setText(lastPayment.get().getDiscountApplied() != null ? String.valueOf(lastPayment.get().getDiscountApplied()) : "0");
                view.getComboBoxPayment_method().setSelectedItem(lastPayment.get().getPaymentMethod().getDescripcion());
                view.getComboBoxPayment_status().setSelectedItem(lastPayment.get().getPaymentStatus().getDescripcion());
                
                view.getjDatePeriod().setDate(java.sql.Date.valueOf(lastPayment.get().getPeriod()));
                view.getjDatePay().setDate(java.sql.Date.valueOf(lastPayment.get().getPaymentDate()));
            } else {
                if (client.get().getCurrentPlan() != null) {
                    view.getTxtBase_amount().setText(String.valueOf(client.get().getCurrentPlan().getValue()));
                }
                view.getTxtDiscount().setText("0");
                view.getjDatePay().setDate(new Date());
                view.getjDatePeriod().setDate(new Date());
                view.showSuccess("El cliente no tiene pagos registrados. Se precargó el costo de su plan actual.", "Aviso");
            }

            List<Payment> payments = paymentService.findAllByClientId(client.get().getDocumentId());
            loadPaymentsToTableAsync(() -> payments.stream()
                    .map(PaymentMapper::toDTO)
                    .collect(java.util.stream.Collectors.toList()));

            view.getTitleCharge().setText("Pago de " + client.get().getName());

        } catch (NumberFormatException ex) {
            view.showError("El DNI debe ser numérico.", "Error");
        } catch (Exception ex) {
            view.showError("Error al buscar el cliente: " + ex.getMessage(), "Error");
        }
    }

    private void onModify() {
        int selectedRow = view.getTableListPayments().getSelectedRow();
        if (selectedRow == -1) {
            view.showWarning("Selecciona un pago primero.", "Advertencia");
            return;
        }

        Long paymentId = Long.parseLong(view.getTableListPayments().getValueAt(selectedRow, 0).toString());
        Payment payment = paymentService.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("No se encontró el pago."));

        view.getTxtDni().setText(String.valueOf(payment.getClient().getDocumentId()));
        view.getTxtBase_amount().setText(String.valueOf(payment.getBaseAmount()));
        view.getTxtDiscount().setText(payment.getDiscountApplied() != null ? payment.getDiscountApplied().toString() : "0");
        view.getComboBoxPayment_method().setSelectedItem(payment.getPaymentMethod().getDescripcion());
        view.getComboBoxPayment_status().setSelectedItem(payment.getPaymentStatus().getDescripcion());

        view.getjDatePeriod().setDate(java.sql.Date.valueOf(payment.getPeriod()));
        view.getjDatePay().setDate(java.sql.Date.valueOf(payment.getPaymentDate()));

        view.setEditMode(true);
        view.setEditingPaymentId(paymentId);
        view.getTxtDni().setEnabled(false);
        view.getTitleCharge().setText("Modificar Pago");
    }

    private void onAll() {
        view.getTitleList().setText("Todos los Pagos");
        loadPaymentsToTableAsync(() -> paymentService.getAllPaymentsDTO());
    }

    private void onConfirmed() {
        view.getTitleList().setText("Pagos Confirmados");
        loadPaymentsToTableAsync(() -> paymentService.findByStatus(PaymentStatus.CONFIRMADO).stream()
                .map(PaymentMapper::toDTO)
                .collect(java.util.stream.Collectors.toList()));
    }

    private void onPending() {
        view.getTitleList().setText("Pagos Pendientes");
        loadPaymentsToTableAsync(() -> paymentService.findByStatus(PaymentStatus.PENDIENTE).stream()
                .map(PaymentMapper::toDTO)
                .collect(java.util.stream.Collectors.toList()));
    }

    private void loadPaymentMethod() {
        view.getComboBoxPayment_method().removeAllItems();
        view.getComboBoxPayment_method().addItem("Seleccione un Método *");
        for (PaymentMethod method : PaymentMethod.values()) {
            view.getComboBoxPayment_method().addItem(method.getDescripcion());
        }
    }

    private void loadPaymentStatus() {
        view.getComboBoxPayment_status().removeAllItems();
        view.getComboBoxPayment_status().addItem("Seleccione un Estado *");
        for (PaymentStatus status : PaymentStatus.values()) {
            view.getComboBoxPayment_status().addItem(status.getDescripcion());
        }
    }

    private void loadPaymentsToTableAsync(java.util.concurrent.Callable<List<PaymentDTO>> loader) {
        view.showLoadingTable();
        
        AsyncDataLoader.loadData(
            loader,
            new AsyncDataLoader.DataLoadCallback<List<PaymentDTO>>() {
                @Override
                public void onSuccess(List<PaymentDTO> payments) {
                    view.updateTable(payments);
                    view.enableSearchButton(true);
                }

                @Override
                public void onError(Exception ex) {
                    view.enableSearchButton(true);
                    view.showError("Error cargando pagos: " + ex.getMessage(), "Error");
                }
            }
        );
    }
}
