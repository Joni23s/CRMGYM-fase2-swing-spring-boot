package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.presenter;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.ClientDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.ClientMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
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
public class ClientPresenter {

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

            view.getBtnSave().setEnabled(false);

            AsyncDataLoader.loadData(
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

                    Plan plan = planService.findByNamePlanIgnoreCase(selectedPlanName)
                            .orElseThrow(() -> new IllegalArgumentException("No se encontró el plan seleccionado."));

                    Client client = new Client(dni, name, lastName, email, phone, true, plan);
                    clientService.save(client);
                    return client;
                },
                new AsyncDataLoader.DataLoadCallback<Client>() {
                    @Override
                    public void onSuccess(Client savedClient) {
                        view.getBtnSave().setEnabled(true);
                        String msg = view.isEditMode() ? "Cliente actualizado correctamente." : "Cliente guardado correctamente.";
                        view.showSuccess(msg, "Éxito");
                        view.resetForm();
                        loadClientsToTableAsync(() -> clientService.getAllClientsDTO());
                    }

                    @Override
                    public void onError(Exception ex) {
                        view.getBtnSave().setEnabled(true);
                        if (ex instanceof IllegalArgumentException) {
                            view.showWarning(ex.getMessage(), "Validación fallida");
                        } else {
                            view.showError("Ocurrió un error al guardar el cliente: " + ex.getMessage(), "Error");
                        }
                    }
                }
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

        view.getBtnActivate().setEnabled(false);
        view.getBtnDeactivate().setEnabled(false);

        AsyncDataLoader.loadData(
            () -> {
                Optional<Client> clientOpt = clientService.findById(dni);
                if (clientOpt.isPresent()) {
                    Client client = clientOpt.get();
                    client.setIsActive(status);
                    clientService.save(client);
                    return true;
                }
                return false;
            },
            new AsyncDataLoader.DataLoadCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    view.getBtnActivate().setEnabled(true);
                    view.getBtnDeactivate().setEnabled(true);
                    if (result) {
                        view.showSuccess("Estado actualizado correctamente.", "Éxito");
                        view.resetForm();
                        loadClientsToTableAsync(() -> clientService.getAllClientsDTO());
                    } else {
                        view.showError("No se encontró el cliente.", "Error");
                    }
                }

                @Override
                public void onError(Exception ex) {
                    view.getBtnActivate().setEnabled(true);
                    view.getBtnDeactivate().setEnabled(true);
                    view.showError("Error al actualizar estado: " + ex.getMessage(), "Error");
                }
            }
        );
    }

    private void onSearch() {
        Set<Client> clients = new HashSet<>();

        String name = view.getTxtName().getText().trim();
        if (!name.isEmpty()) {
            clients.addAll(clientService.findByName(name));
        }

        String lastName = view.getTxtLastName().getText().trim();
        if (!lastName.isEmpty()) {
            clients.addAll(clientService.findByLastName(lastName));
        }

        String dniText = view.getTxtDni().getText().trim();
        if (!dniText.isEmpty()) {
            try {
                int dni = Integer.parseInt(dniText);
                clientService.findById(dni).ifPresent(clients::add);
            } catch (NumberFormatException ignored) {}
        }

        String phone = view.getTxtPhone().getText().trim();
        if (!phone.isEmpty()) {
            clients.addAll(clientService.findByPhoneNumber(phone));
        }

        String email = view.getTxtMail().getText().trim();
        if (!email.isEmpty()) {
            clients.addAll(clientService.findByEmail(email));
        }

        if (view.getComboBoxPlan().getSelectedIndex() != 0) {
            String selectedPlan = view.getComboBoxPlan().getSelectedItem().toString();
            clients.addAll(clientService.findByCurrentPlan(selectedPlan));
        }

        view.getTitleList().setText("Lista de Clientes: Buscados");
        loadClientsToTableAsync(() -> clients.stream()
                .map(ClientMapper::toDTO)
                .collect(Collectors.toList()));
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
        view.getComboBoxPlan().setEnabled(false);
        AsyncDataLoader.loadData(
            () -> planService.findByIsActiveDTO(true),
            new AsyncDataLoader.DataLoadCallback<List<PlanDTO>>() {
                @Override
                public void onSuccess(List<PlanDTO> planes) {
                    view.getComboBoxPlan().setEnabled(true);
                    if (planes != null) {
                        for (PlanDTO plan : planes) {
                            view.getComboBoxPlan().addItem(plan.toString());
                        }
                    }
                }

                @Override
                public void onError(Exception ex) {
                    view.getComboBoxPlan().setEnabled(true);
                    view.showError("Error al cargar planes: " + ex.getMessage(), "Error");
                }
            }
        );
    }

    private void loadClientsToTableAsync(java.util.concurrent.Callable<List<ClientDTO>> loader) {
        view.showLoadingTable();
        
        AsyncDataLoader.loadData(
            loader,
            new AsyncDataLoader.DataLoadCallback<List<ClientDTO>>() {
                @Override
                public void onSuccess(List<ClientDTO> clients) {
                    view.updateTable(clients);
                    view.enableButtons(true);
                }

                @Override
                public void onError(Exception ex) {
                    view.enableButtons(true);
                    view.showError("Error cargando clientes: " + ex.getMessage(), "Error");
                }
            }
        );
    }
}
