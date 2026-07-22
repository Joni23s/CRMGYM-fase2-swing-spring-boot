package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.presenter;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.ClientDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.validations.ClientValidation;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.ClientPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ClientPresenter extends BasePresenter {

    private final ClientPanel view;
    private final ClientService clientService;
    private final PlanService planService;
    private final ClientValidation clientValidation;

    @Autowired
    public ClientPresenter(ClientPanel view, ClientService clientService, PlanService planService, ClientValidation clientValidation) {
        this.view = view;
        this.clientService = clientService;
        this.planService = planService;
        this.clientValidation = clientValidation;
    }

    @PostConstruct
    public void init() {
        // Wiring events
        view.getBtnSave().addActionListener(e -> onSave());
        view.getBtnSearch().addActionListener(e -> onSearch());
        view.getBtnClean().addActionListener(e -> view.resetForm());
        
        view.getBtnAll().addActionListener(e -> onAll());
        view.getBtnActive().addActionListener(e -> onActive());
        view.getBtnInactive().addActionListener(e -> onInactive());
        
        view.getBtnModify().addActionListener(e -> onModify());
        view.getBtnActivate().addActionListener(e -> onActivate());
        view.getBtnDeactivate().addActionListener(e -> onDeactivate());

        // Initialize view structures and load initial data
        view.initClientTable();
        loadPlansToComboBox();
        view.resetForm();
        loadClientsToTableAsync(() -> clientService.getAllClientsDTO());
    }

    private void onSave() {
        try {
            String name = view.getTxtName().getText().trim();
            String lastName = view.getTxtLastName().getText().trim();
            String dniText = view.getTxtDni().getText().trim();
            String phoneText = view.getTxtPhone().getText().trim();
            String email = view.getTxtMail().getText().trim();
            String selectedPlanName = (String) view.getComboBoxPlan().getSelectedItem();

            if (!isFormValid()) {
                view.showWarning("Por favor completá todos los campos obligatorios.", "Campos incompletos");
                return;
            }

            int dni = Integer.parseInt(dniText);

            final ClientDTO clientDTO = ClientDTO.builder()
                    .documentId(dni)
                    .name(name)
                    .lastName(lastName)
                    .email(email)
                    .phoneNumber(phoneText.isEmpty() ? null : phoneText)
                    .status("Activo")
                    .namePlan(selectedPlanName)
                    .build();

            executeAsync(
                () -> {
                    if (!view.isEditMode() && !clientValidation.isDniAvailable(dni)) {
                        throw new IllegalArgumentException("Ya existe un cliente con DNI: " + dni + "\nIntente con un DNI diferente.");
                    }

                    if (!email.isEmpty() && !clientValidation.isValidEmail(email)) {
                        throw new IllegalArgumentException("El email ingresado no tiene un formato válido.");
                    }

                    String phone = phoneText.isEmpty() ? null : phoneText;
                    if (phone != null && !clientValidation.isValidPhone(phone)) {
                        throw new IllegalArgumentException("El número de celular ingresado no es válido.");
                    }

                    return clientService.saveDTO(clientDTO);
                },
                savedClient -> {
                    String msg = view.isEditMode() ? "Cliente actualizado correctamente." : "Cliente guardado correctamente.";
                    view.showSuccess(msg, "Éxito");
                    view.resetForm();
                    loadClientsToTableAsync(() -> clientService.getAllClientsDTO());
                },
                view.getBtnSave()
            );

        } catch (NumberFormatException ex) {
            view.showError("El DNI debe ser un número válido.", "Error");
        } catch (Exception ex) {
            view.showError("Ocurrió un error al procesar el cliente: " + ex.getMessage(), "Error");
        }
    }

    private void onModify() {
        int filaSelected = view.getTableListClients().getSelectedRow();
        if (filaSelected == -1) {
            view.showWarning("Selecciona a un cliente primero.", "Advertencia");
            return;
        }

        String documentId = view.getTableListClients().getValueAt(filaSelected, 0).toString();
        String name = view.getTableListClients().getValueAt(filaSelected, 1).toString();
        String lastName = view.getTableListClients().getValueAt(filaSelected, 2).toString();
        String email = view.getTableListClients().getValueAt(filaSelected, 3) != null ? view.getTableListClients().getValueAt(filaSelected, 3).toString() : "";
        String phone = view.getTableListClients().getValueAt(filaSelected, 4) != null ? view.getTableListClients().getValueAt(filaSelected, 4).toString() : "";
        String namePlan = view.getTableListClients().getValueAt(filaSelected, 6).toString();

        view.getTxtDni().setText(documentId);
        view.getTxtName().setText(name);
        view.getTxtLastName().setText(lastName);
        view.getTxtMail().setText(email);
        view.getTxtPhone().setText(phone);
        view.getComboBoxPlan().setSelectedItem(namePlan);

        view.setEditMode(true);
        view.setEditingDni(Integer.parseInt(documentId));
        view.getTxtDni().setEnabled(false);
        view.getTitleCharge().setText("Modificar Cliente");
    }

    private void onAll() {
        view.getTitleList().setText("Lista de Clientes");
        loadClientsToTableAsync(() -> clientService.getAllClientsDTO());
    }

    private void onActive() {
        view.getTitleList().setText("Lista de Clientes: Activos");
        loadClientsToTableAsync(() -> clientService.findByIsActiveDTO(true));
    }

    private void onInactive() {
        view.getTitleList().setText("Lista de Clientes: Inactivos");
        loadClientsToTableAsync(() -> clientService.findByIsActiveDTO(false));
    }

    private void onActivate() {
        modifyStatus(true);
    }

    private void onDeactivate() {
        modifyStatus(false);
    }

    private void modifyStatus(boolean status) {
        int filaSelected = view.getTableListClients().getSelectedRow();
        if (filaSelected == -1) {
            view.showWarning("Selecciona a un cliente primero.", "Advertencia");
            return;
        }

        int dni = Integer.parseInt(view.getTableListClients().getValueAt(filaSelected, 0).toString());

        executeAsync(
            () -> clientService.changeStatusDTO(dni, status),
            result -> {
                if (result) {
                    view.showSuccess("Estado actualizado correctamente.", "Éxito");
                    view.resetForm();
                    loadClientsToTableAsync(() -> clientService.getAllClientsDTO());
                } else {
                    view.showError("No se encontró el cliente.", "Error");
                }
            },
            view.getBtnActivate(), view.getBtnDeactivate()
        );
    }

    private void onSearch() {
        String name = view.getTxtName().getText().trim();
        String lastName = view.getTxtLastName().getText().trim();
        String dniText = view.getTxtDni().getText().trim();
        String phone = view.getTxtPhone().getText().trim();
        String email = view.getTxtMail().getText().trim();
        String selectedPlan = (view.getComboBoxPlan().getSelectedIndex() != 0) ? view.getComboBoxPlan().getSelectedItem().toString() : null;

        view.getTitleList().setText("Lista de Clientes: Buscados");
        loadClientsToTableAsync(() -> clientService.searchClientsDTO(name, lastName, dniText, phone, email, selectedPlan));
    }

    private boolean isFormValid() {
        boolean valid = true;

        if (!clientValidation.isValidName(view.getTxtName().getText().trim())) {
            view.getTxtName().putClientProperty("JComponent.outline", "error");
            valid = false;
        } else {
            view.getTxtName().putClientProperty("JComponent.outline", null);
        }

        if (!clientValidation.isValidName(view.getTxtLastName().getText().trim())) {
            view.getTxtLastName().putClientProperty("JComponent.outline", "error");
            valid = false;
        } else {
            view.getTxtLastName().putClientProperty("JComponent.outline", null);
        }

        String dniText = view.getTxtDni().getText().trim();
        if (dniText.isEmpty()) {
            view.getTxtDni().putClientProperty("JComponent.outline", "error");
            valid = false;
        } else {
            try {
                Integer.parseInt(dniText);
                view.getTxtDni().putClientProperty("JComponent.outline", null);
            } catch (NumberFormatException e) {
                view.getTxtDni().putClientProperty("JComponent.outline", "error");
                valid = false;
            }
        }

        if (view.getComboBoxPlan().getSelectedIndex() <= 0) {
            view.getComboBoxPlan().putClientProperty("JComponent.outline", "error");
            valid = false;
        } else {
            view.getComboBoxPlan().putClientProperty("JComponent.outline", null);
        }

        return valid;
    }

    private void loadPlansToComboBox() {
        view.getComboBoxPlan().removeAllItems();
        view.getComboBoxPlan().addItem("Seleccione un Plan *");
        executeAsync(
            () -> planService.findByIsActiveDTO(true),
            planes -> {
                if (planes != null) {
                    for (PlanDTO plan : planes) {
                        view.getComboBoxPlan().addItem(plan.toString());
                    }
                }
            },
            view.getComboBoxPlan()
        );
    }

    private void loadClientsToTableAsync(java.util.concurrent.Callable<List<ClientDTO>> loader) {
        view.showLoadingTable();
        executeAsync(
            loader,
            clients -> {
                view.updateTable(clients);
                view.enableButtons(true);
            }
        );
    }
}
