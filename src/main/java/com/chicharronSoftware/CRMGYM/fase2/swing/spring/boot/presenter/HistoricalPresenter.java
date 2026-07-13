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
public class HistoricalPresenter {

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
        List<PlanDTO> planes = planService.findByIsActiveDTO(active);
        view.populatePlansComboBox(planes != null ? planes : new ArrayList<>());
    }

    private void onSearch() {
        Set<Client> clients = new HashSet<>();

        if (view.getBtnName().isSelected()) {
            String name = view.getTxtName().getText().trim();
            if (!name.isEmpty()) {
                clients.addAll(clientService.findByName(name));
            }
        }

        if (view.getBtnLastName().isSelected()) {
            String lastName = view.getTxtLastName().getText().trim();
            if (!lastName.isEmpty()) {
                clients.addAll(clientService.findByLastName(lastName));
            }
        }

        if (view.getBtnDni().isSelected()) {
            String dniText = view.getTxtDni().getText().trim();
            if (!dniText.isEmpty()) {
                try {
                    int dni = Integer.parseInt(dniText);
                    clientService.findById(dni).ifPresent(clients::add);
                } catch (NumberFormatException ignored) {}
            }
        }

        if (view.getBtnPhone().isSelected()) {
            String phone = view.getTxtPhone1().getText().trim();
            if (!phone.isEmpty()) {
                clients.addAll(clientService.findByPhoneNumber(phone));
            }
        }

        if (view.getBtnPlan().isSelected()) {
            if (view.getComboBoxPlan().getSelectedIndex() != 0) {
                String selectedPlan = view.getComboBoxPlan().getSelectedItem().toString();
                clients.addAll(clientService.findByCurrentPlan(selectedPlan));
            }
        }

        view.getTitleList().setText("Lista de Historial: Buscados");
        loadHistoricalPlanToTableAsync(new ArrayList<>(clients));
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

            Optional<Client> clientOpt = clientService.findById(clientId);
            if (clientOpt.isEmpty()) {
                view.showError("No se encontró el cliente con ID: " + clientId, "Error");
                return;
            }

            Client client = clientOpt.get();
            List<Client> clients = new ArrayList<>();
            clients.add(client);

            view.getTitleList().setText("Historial de Cliente: " + client.getDocumentId());
            loadHistoricalPlanToTableAsync(clients);

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
