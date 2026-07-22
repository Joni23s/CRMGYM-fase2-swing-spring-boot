package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.presenter;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.HistoricalPlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.HistoricalPlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.HistoricalPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class HistoricalPresenter extends BasePresenter {

    private final HistoricalPanel view;
    private final ClientService clientService;
    private final PlanService planService;
    private final HistoricalPlanService historicalPlanService;

    @Autowired
    public HistoricalPresenter(HistoricalPanel view, ClientService clientService, PlanService planService, HistoricalPlanService historicalPlanService) {
        this.view = view;
        this.clientService = clientService;
        this.planService = planService;
        this.historicalPlanService = historicalPlanService;
    }

    @PostConstruct
    public void init() {
        // Wiring events for buttons
        view.getBtnSearch().addActionListener(e -> onSearch());
        view.getBtnCleanPanels().addActionListener(e -> view.resetForm());
        view.getBtnSelect().addActionListener(e -> onSelectClient());
        view.getBtnCleanTable().addActionListener(e -> {
            view.clearTable();
            view.getTitleList().setText("Lista de Historial");
        });

        // Wiring events for radio buttons (Plans)
        view.getBtnPlanActive().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadPlansToComboBox(true);
            }
        });
        view.getBtnPlanInactive().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadPlansToComboBox(false);
            }
        });

        // Wiring events for radio buttons (Clients)
        view.getBtnClientActive().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                AsyncDataLoader.loadData(
                    () -> clientService.findByIsActive(true),
                    new AsyncDataLoader.DataLoadCallback<List<Client>>() {
                        @Override public void onSuccess(List<Client> clients) { loadHistoricalPlanToTableAsync(clients); }
                        @Override public void onError(Exception ex) { view.showError("Error: " + ex.getMessage(), "Error"); }
                    }
                );
            }
        });

        view.getBtnClientInactive().addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                AsyncDataLoader.loadData(
                    () -> clientService.findByIsActive(false),
                    new AsyncDataLoader.DataLoadCallback<List<Client>>() {
                        @Override public void onSuccess(List<Client> clients) { loadHistoricalPlanToTableAsync(clients); }
                        @Override public void onError(Exception ex) { view.showError("Error: " + ex.getMessage(), "Error"); }
                    }
                );
            }
        });

        // Initialize UI
        view.initHistoricalPlanTable();
        view.getBtnPlanActive().setSelected(true);
        loadPlansToComboBox(true);
        view.resetForm();
        loadAllHistoricalPlanToTableAsync();
    }

    private void loadPlansToComboBox(boolean active) {
        view.getComboBoxPlan().setEnabled(false);
        AsyncDataLoader.loadData(
            () -> planService.findByIsActiveDTO(active),
            new AsyncDataLoader.DataLoadCallback<List<PlanDTO>>() {
                @Override
                public void onSuccess(List<PlanDTO> planes) {
                    view.getComboBoxPlan().setEnabled(true);
                    view.populatePlansComboBox(planes != null ? planes : new ArrayList<>());
                }

                @Override
                public void onError(Exception ex) {
                    view.getComboBoxPlan().setEnabled(true);
                    view.showError("Error al cargar planes: " + ex.getMessage(), "Error");
                }
            }
        );
    }

    private void onSearch() {
        String name = view.getTxtName().getText().trim();
        String lastName = view.getTxtLastName().getText().trim();
        String dniText = view.getTxtDni().getText().trim();
        String phone = view.getTxtPhone1().getText().trim();
        int planSelectedIndex = view.getComboBoxPlan().getSelectedIndex();
        String selectedPlan = planSelectedIndex != 0 ? view.getComboBoxPlan().getSelectedItem().toString() : null;

        boolean isNameSelected = view.getBtnName().isSelected();
        boolean isLastNameSelected = view.getBtnLastName().isSelected();
        boolean isDniSelected = view.getBtnDni().isSelected();
        boolean isPhoneSelected = view.getBtnPhone().isSelected();
        boolean isPlanSelected = view.getBtnPlan().isSelected();

        view.getBtnSearch().setEnabled(false);

        AsyncDataLoader.loadData(
            () -> {
                Set<Client> clients = new HashSet<>();

                if (isNameSelected && !name.isEmpty()) {
                    clients.addAll(clientService.findByName(name));
                }

                if (isLastNameSelected && !lastName.isEmpty()) {
                    clients.addAll(clientService.findByLastName(lastName));
                }

                if (isDniSelected && !dniText.isEmpty()) {
                    try {
                        int dni = Integer.parseInt(dniText);
                        clientService.findById(dni).ifPresent(clients::add);
                    } catch (NumberFormatException ignored) {}
                }

                if (isPhoneSelected && !phone.isEmpty()) {
                    clients.addAll(clientService.findByPhoneNumber(phone));
                }

                if (isPlanSelected && selectedPlan != null) {
                    clients.addAll(clientService.findByCurrentPlan(selectedPlan));
                }

                return new ArrayList<>(clients);
            },
            new AsyncDataLoader.DataLoadCallback<List<Client>>() {
                @Override
                public void onSuccess(List<Client> clients) {
                    view.getBtnSearch().setEnabled(true);
                    view.getTitleList().setText("Lista de Historial: Buscados");
                    loadHistoricalPlanToTableAsync(clients);
                }

                @Override
                public void onError(Exception ex) {
                    view.getBtnSearch().setEnabled(true);
                    view.showError("Error en la búsqueda de clientes: " + ex.getMessage(), "Error");
                }
            }
        );
    }

    private void onSelectClient() {
        int filaSelected = view.getTableListClients().getSelectedRow();
        if (filaSelected == -1) {
            view.showWarning("Selecciona a un cliente primero.", "Advertencia");
            return;
        }

        try {
            String idStr = view.getTableListClients().getValueAt(filaSelected, 4).toString();
            int clientId = Integer.parseInt(idStr);

            view.getBtnSelect().setEnabled(false);

            AsyncDataLoader.loadData(
                () -> clientService.findById(clientId),
                new AsyncDataLoader.DataLoadCallback<Optional<Client>>() {
                    @Override
                    public void onSuccess(Optional<Client> clientOpt) {
                        view.getBtnSelect().setEnabled(true);
                        if (clientOpt.isEmpty()) {
                            view.showError("No se encontró el cliente con ID: " + clientId, "Error");
                            return;
                        }

                        Client client = clientOpt.get();
                        List<Client> clients = new ArrayList<>();
                        clients.add(client);

                        view.getTitleList().setText("Historial de Cliente: " + client.getDocumentId());
                        loadHistoricalPlanToTableAsync(clients);
                    }

                    @Override
                    public void onError(Exception ex) {
                        view.getBtnSelect().setEnabled(true);
                        view.showError("Ocurrió un error al obtener el cliente: " + ex.getMessage(), "Error");
                    }
                }
            );

        } catch (NumberFormatException e) {
            view.showError("El valor de ID no es válido.", "Error de formato");
        } catch (Exception e) {
            view.showError("Ocurrió un error al obtener el cliente: " + e.getMessage(), "Error");
        }
    }

    private void loadAllHistoricalPlanToTableAsync() {
        view.showLoadingTable();
        view.enableSearchButton(false);

        AsyncDataLoader.loadData(
            () -> historicalPlanService.findAllWithDetails(),
            new AsyncDataLoader.DataLoadCallback<List<HistoricalPlanDTO>>() {
                @Override
                public void onSuccess(List<HistoricalPlanDTO> result) {
                    view.updateTable(result);
                    view.enableSearchButton(true);
                }

                @Override
                public void onError(Exception ex) {
                    view.enableSearchButton(true);
                    view.showError("Error cargando historial: " + ex.getMessage(), "Error");
                }
            }
        );
    }

    private void loadHistoricalPlanToTableAsync(List<Client> clients) {
        if (clients == null || clients.isEmpty()) {
            view.clearTable();
            return;
        }
        view.showLoadingTable();
        view.enableSearchButton(false);

        AsyncDataLoader.loadData(
            () -> historicalPlanService.findByClientsWithDetails(clients),
            new AsyncDataLoader.DataLoadCallback<List<HistoricalPlanDTO>>() {
                @Override
                public void onSuccess(List<HistoricalPlanDTO> result) {
                    view.updateTable(result);
                    view.enableSearchButton(true);
                }

                @Override
                public void onError(Exception ex) {
                    view.enableSearchButton(true);
                    view.showError("Error cargando filtrado: " + ex.getMessage(), "Error");
                }
            }
        );
    }
}
