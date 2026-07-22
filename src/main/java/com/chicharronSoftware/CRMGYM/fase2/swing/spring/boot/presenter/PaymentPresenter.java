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

            view.getBtnSave().setEnabled(false);

            BigDecimal baseAmountVal;
            BigDecimal discountVal;
            try {
                String cleanBase = baseAmountText.replace(",", ".").replaceAll("[^0-9.]", "");
                baseAmountVal = new BigDecimal(cleanBase);
                String cleanDiscount = discountText.replace(",", ".").replaceAll("[^0-9.]", "");
                discountVal = cleanDiscount.isEmpty() ? BigDecimal.ZERO : new BigDecimal(cleanDiscount);
            } catch (NumberFormatException ex) {
                view.getBtnSave().setEnabled(true);
                view.showError("Los montos ingresados no son válidos.", "Error en monto");
                return;
            }

            BigDecimal finalAmount = baseAmountVal.subtract(discountVal);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                view.getBtnSave().setEnabled(true);
                view.showError("El monto final no puede ser negativo.", "Error");
                return;
            }

            LocalDate period = periodDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate payment = paymentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            PaymentMethod paymentMethod = Arrays.stream(PaymentMethod.values())
                    .filter(pm -> pm.getDescripcion().equals(paymentMethodDesc))
                    .findFirst()
                    .orElse(PaymentMethod.EFECTIVO);

            PaymentStatus paymentStatus = Arrays.stream(PaymentStatus.values())
                    .filter(ps -> ps.getDescripcion().equals(paymentStatusDesc))
                    .findFirst()
                    .orElse(PaymentStatus.PENDIENTE);

            AsyncDataLoader.loadData(
                () -> {
                    Client client = clientService.findById(dni)
                            .orElseThrow(() -> new IllegalArgumentException("No se encontró un cliente con DNI " + dni));

                    Payment paymentEntity;
                    if (view.isEditMode() && view.getEditingPaymentId() != null) {
                        paymentEntity = paymentService.findById(view.getEditingPaymentId())
                                .orElseThrow(() -> new IllegalArgumentException("No se encontró el pago a modificar."));
                        paymentEntity.setPeriod(period);
                        paymentEntity.setPaymentDate(payment);
                        paymentEntity.setBaseAmount(baseAmountVal);
                        paymentEntity.setDiscountApplied(discountVal);
                        paymentEntity.setFinalAmount(finalAmount);
                        paymentEntity.setPaymentMethod(paymentMethod);
                        paymentEntity.setPaymentStatus(paymentStatus);
                        paymentEntity.setClient(client);
                        if (paymentEntity.getPlan() == null) {
                            paymentEntity.setPlan(client.getCurrentPlan());
                        }
                    } else {
                        paymentEntity = Payment.builder()
                                .period(period)
                                .paymentDate(payment)
                                .baseAmount(baseAmountVal)
                                .discountApplied(discountVal)
                                .finalAmount(finalAmount)
                                .paymentMethod(paymentMethod)
                                .paymentStatus(paymentStatus)
                                .client(client)
                                .plan(client.getCurrentPlan())
                                .build();
                    }

                    paymentService.save(paymentEntity);
                    return paymentEntity;
                },
                new AsyncDataLoader.DataLoadCallback<Payment>() {
                    @Override
                    public void onSuccess(Payment result) {
                        view.getBtnSave().setEnabled(true);
                        String msg = view.isEditMode() ? "Pago actualizado correctamente." : "Pago guardado correctamente.";
                        view.showSuccess(msg, "Éxito");
                        view.resetForm();
                        loadPaymentsToTableAsync(() -> paymentService.getAllPaymentsDTO());
                    }

                    @Override
                    public void onError(Exception ex) {
                        view.getBtnSave().setEnabled(true);
                        if (ex instanceof IllegalArgumentException) {
                            view.showError(ex.getMessage(), "Error");
                        } else {
                            view.showError("Ocurrió un error al guardar el pago: " + ex.getMessage(), "Error");
                        }
                    }
                }
            );

        } catch (Exception ex) {
            view.showError("Ocurrió un error al procesar el pago: " + ex.getMessage(), "Error");
        }
    }

    private void onSearchDni() {
        String dniText = view.getTxtDni().getText().trim();
        if (dniText.isEmpty()) {
            view.showWarning("Ingrese un DNI válido.", "Advertencia");
            return;
        }

        int dniNumber;
        try {
            dniNumber = Integer.parseInt(dniText);
        } catch (NumberFormatException ex) {
            view.showError("El DNI debe ser numérico.", "Error");
            return;
        }

        view.getBtnSearch().setEnabled(false);

        class SearchResult {
            final Client client;
            final Payment lastPayment;
            final List<Payment> payments;
            SearchResult(Client client, Payment lastPayment, List<Payment> payments) {
                this.client = client;
                this.lastPayment = lastPayment;
                this.payments = payments;
            }
        }

        AsyncDataLoader.loadData(
            () -> {
                Client client = clientService.findById(dniNumber)
                        .orElseThrow(() -> new IllegalArgumentException("No se encontró un cliente con ese DNI."));
                Payment lastPayment = paymentService.findLastPaymentByClientId(client.getDocumentId()).orElse(null);
                List<Payment> payments = paymentService.findAllByClientId(client.getDocumentId());
                return new SearchResult(client, lastPayment, payments);
            },
            new AsyncDataLoader.DataLoadCallback<SearchResult>() {
                @Override
                public void onSuccess(SearchResult result) {
                    view.getBtnSearch().setEnabled(true);
                    Client client = result.client;
                    Payment lastPayment = result.lastPayment;

                    if (lastPayment != null) {
                        view.getTxtBase_amount().setText(String.valueOf(lastPayment.getBaseAmount()));
                        view.getTxtDiscount().setText(lastPayment.getDiscountApplied() != null ? String.valueOf(lastPayment.getDiscountApplied()) : "0");
                        view.getComboBoxPayment_method().setSelectedItem(lastPayment.getPaymentMethod().getDescripcion());
                        view.getComboBoxPayment_status().setSelectedItem(lastPayment.getPaymentStatus().getDescripcion());
                        
                        view.getjDatePeriod().setDate(java.sql.Date.valueOf(lastPayment.getPeriod()));
                        view.getjDatePay().setDate(java.sql.Date.valueOf(lastPayment.getPaymentDate()));
                    } else {
                        if (client.getCurrentPlan() != null) {
                            view.getTxtBase_amount().setText(String.valueOf(client.getCurrentPlan().getValue()));
                        }
                        view.getTxtDiscount().setText("0");
                        view.getjDatePay().setDate(new Date());
                        view.getjDatePeriod().setDate(new Date());
                        view.showSuccess("El cliente no tiene pagos registrados. Se precargó el costo de su plan actual.", "Aviso");
                    }

                    loadPaymentsToTableAsync(() -> result.payments.stream()
                            .map(PaymentMapper::toDTO)
                            .collect(java.util.stream.Collectors.toList()));

                    view.getTitleCharge().setText("Pago de " + client.getName());
                }

                @Override
                public void onError(Exception ex) {
                    view.getBtnSearch().setEnabled(true);
                    if (ex instanceof IllegalArgumentException) {
                        view.showError(ex.getMessage(), "Error");
                    } else {
                        view.showError("Error al buscar el cliente: " + ex.getMessage(), "Error");
                    }
                }
            }
        );
    }

    private void onModify() {
        int selectedRow = view.getTableListPayments().getSelectedRow();
        if (selectedRow == -1) {
            view.showWarning("Selecciona un pago primero.", "Advertencia");
            return;
        }

        Long paymentId = Long.parseLong(view.getTableListPayments().getValueAt(selectedRow, 0).toString());

        view.getBtnModify().setEnabled(false);

        AsyncDataLoader.loadData(
            () -> paymentService.findById(paymentId)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el pago.")),
            new AsyncDataLoader.DataLoadCallback<Payment>() {
                @Override
                public void onSuccess(Payment payment) {
                    view.getBtnModify().setEnabled(true);
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

                @Override
                public void onError(Exception ex) {
                    view.getBtnModify().setEnabled(true);
                    view.showError("Error al cargar pago: " + ex.getMessage(), "Error");
                }
            }
        );
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
