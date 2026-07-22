package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.presenter;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.PlanMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.validations.PlanValidation;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels.PlansPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.swing.*;
import java.awt.Color;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlansPresenter {

    private final PlansPanel view;
    private final PlanService planService;
    private final PlanValidation planValidation;

    @Autowired
    public PlansPresenter(PlansPanel view, PlanService planService, PlanValidation planValidation) {
        this.view = view;
        this.planService = planService;
        this.planValidation = planValidation;
    }

    @PostConstruct
    public void init() {
        // Wiring events
        view.getBtnSave().addActionListener(e -> onSave());
        view.getBtnSearch().addActionListener(e -> onSearch());
        view.getBtnClean().addActionListener(e -> view.resetForm());
        view.getBtnActivate().addActionListener(e -> onActivate());
        view.getBtnDeactivate().addActionListener(e -> onDeactivate());
        
        view.getBtnAll().addActionListener(e -> onAll());
        view.getBtnActive().addActionListener(e -> onActive());
        view.getBtnInactive().addActionListener(e -> onInactive());
        view.getBtnModify().addActionListener(e -> onModify());

        // Initialize table data
        view.initPlanTable();
        loadPlansToTableAsync(() -> planService.getAllPlansDTO());
    }

    private void onSave() {
        try {
            if (!isFormValid()) {
                view.showWarning("Verifique los datos ingresados.", "Validación fallida");
                return;
            }
            
            String name = view.getTxtName().getText().trim();
            String costText = view.getTxtCost().getText().trim().replace(",", ".");
            BigDecimal cost = new BigDecimal(costText);
            String notes = view.getTxtANotes().getText().trim();
            Integer selectedHours = (Integer) view.getSpinnerHours().getValue();
            Integer selectedDays = (Integer) view.getSpinnerDays().getValue();

            final Plan plan;
            if (view.isEditMode()) {
                plan = new Plan(view.getEditingPlanId(), name, selectedDays, selectedHours, cost, notes, true);
            } else {
                plan = new Plan(name, selectedDays, selectedHours, cost, notes, true);
            }

            view.getBtnSave().setEnabled(false);

            com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader.loadData(
                () -> {
                    planService.save(plan);
                    return plan;
                },
                new com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader.DataLoadCallback<Plan>() {
                    @Override
                    public void onSuccess(Plan savedPlan) {
                        view.getBtnSave().setEnabled(true);
                        String msg = view.isEditMode() ? "Plan actualizado correctamente." : "Plan guardado correctamente.";
                        view.showSuccess(msg, "Éxito");
                        view.resetForm();
                        loadPlansToTableAsync(() -> planService.getAllPlansDTO());
                    }

                    @Override
                    public void onError(Exception ex) {
                        view.getBtnSave().setEnabled(true);
                        view.showError("Error al guardar el plan: " + ex.getMessage(), "Error");
                    }
                }
            );

        } catch (NumberFormatException ex) {
            view.showError("El valor ingresado no es válido.", "Error de formato");
        } catch (Exception ex) {
            view.showError("Error al guardar el plan: " + ex.getMessage(), "Error");
        }
    }

    private void onModify() {
        int filaSelected = view.getTableListPlans().getSelectedRow();
        if (filaSelected == -1) {
            view.showWarning("Selecciona un plan primero.", "Advertencia");
            return;
        }

        String id = view.getTableListPlans().getValueAt(filaSelected, 0).toString();
        String name = view.getTableListPlans().getValueAt(filaSelected, 1).toString();
        String days = view.getTableListPlans().getValueAt(filaSelected, 2).toString();
        String hours = view.getTableListPlans().getValueAt(filaSelected, 3).toString();
        String cost = view.getTableListPlans().getValueAt(filaSelected, 4).toString();
        String notes = view.getTableListPlans().getValueAt(filaSelected, 5) != null ? view.getTableListPlans().getValueAt(filaSelected, 5).toString() : "";

        view.getTxtName().setText(name);
        view.getSpinnerDays().setValue(Integer.parseInt(days));
        view.getSpinnerHours().setValue(Integer.parseInt(hours));
        view.getTxtCost().setText(cost);
        view.getTxtANotes().setText(notes);

        view.setEditMode(true);
        view.setEditingPlanId(Integer.parseInt(id));
        view.getTitleCharge().setText("Modificar Plan");
    }

    private void modifyStatus(boolean status) {
        int filaSelected = view.getTableListPlans().getSelectedRow();
        if (filaSelected == -1) {
            view.showWarning("Selecciona un plan primero.", "Advertencia");
            return;
        }

        int id = Integer.parseInt(view.getTableListPlans().getValueAt(filaSelected, 0).toString());

        view.getBtnActivate().setEnabled(false);
        view.getBtnDeactivate().setEnabled(false);

        com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader.loadData(
            () -> {
                planService.changeStatusWithClients(id, status);
                return null;
            },
            new com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader.DataLoadCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    view.getBtnActivate().setEnabled(true);
                    view.getBtnDeactivate().setEnabled(true);
                    view.showSuccess("Estado actualizado correctamente.", "Éxito");
                    view.resetForm();
                    loadPlansToTableAsync(() -> planService.getAllPlansDTO());
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

    private void onActivate() {
        modifyStatus(true);
    }

    private void onDeactivate() {
        modifyStatus(false);
    }

    private void onSearch() {
        Set<Plan> plans = new HashSet<>();

        String name = view.getTxtName().getText().trim();
        if (!name.isEmpty()) {
            planService.findByNamePlanIgnoreCase(name).ifPresent(plans::add);
        }

        int hours = (int) view.getSpinnerHours().getValue();
        if (hours != 0) {
            plans.addAll(planService.findByHoursEnabled(hours));
        }

        int days = (int) view.getSpinnerDays().getValue();
        if (days != 0) {
            plans.addAll(planService.findByDaysEnabled(days));
        }

        String costText = view.getTxtCost().getText().trim();
        if (!costText.isEmpty()) {
            try {
                BigDecimal cost = new BigDecimal(costText.replace(",", "."));
                if (cost.compareTo(BigDecimal.ZERO) != 0) {
                    plans.addAll(planService.findByValue(cost));
                }
            } catch (NumberFormatException ignored) {}
        }

        view.getTitleList().setText("Lista de Planes: Buscados");
        loadPlansToTableAsync(() -> plans.stream()
                .map(PlanMapper::toDTO)
                .collect(Collectors.toList()));
    }

    private void onAll() {
        view.getTitleList().setText("Lista de Planes");
        loadPlansToTableAsync(() -> planService.getAllPlansDTO());
    }

    private void onActive() {
        view.getTitleList().setText("Lista de Planes: Activos");
        loadPlansToTableAsync(() -> planService.findByIsActiveDTO(true));
    }

    private void onInactive() {
        view.getTitleList().setText("Lista de Planes: Inactivos");
        loadPlansToTableAsync(() -> planService.findByIsActiveDTO(false));
    }

    private void loadPlansToTableAsync(java.util.concurrent.Callable<List<PlanDTO>> loader) {
        view.showLoadingTable();
        
        com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader.loadData(
            loader,
            new com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.AsyncDataLoader.DataLoadCallback<List<PlanDTO>>() {
                @Override
                public void onSuccess(List<PlanDTO> plans) {
                    view.updateTable(plans);
                }

                @Override
                public void onError(Exception ex) {
                    view.getBtnSearch().setEnabled(true);
                    view.showError("Error cargando planes: " + ex.getMessage(), "Error");
                }
            }
        );
    }

    private boolean isFormValid() {
        boolean valid = true;

        if (!view.isEditMode()) {
            String name = view.getTxtName().getText().trim();
            if (name.isEmpty() || !planValidation.isValidName(name)) {
                view.getTxtName().setBorder(BorderFactory.createLineBorder(Color.RED));
                valid = false;
            } else {
                view.getTxtName().setBorder(UIManager.getBorder("TextField.border"));
            }
        }

        String costText = view.getTxtCost().getText().trim();
        if (costText.isEmpty()) {
            view.getTxtCost().setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        } else {
            try {
                BigDecimal costValue = new BigDecimal(costText.replace(",", "."));
                if (!planValidation.isValidCost(costValue)) {
                    view.getTxtCost().setBorder(BorderFactory.createLineBorder(Color.RED));
                    valid = false;
                } else {
                    view.getTxtCost().setBorder(UIManager.getBorder("TextField.border"));
                }
            } catch (NumberFormatException e) {
                view.getTxtCost().setBorder(BorderFactory.createLineBorder(Color.RED));
                valid = false;
            }
        }

        int days = (int) view.getSpinnerDays().getValue();
        if (!planValidation.isValidDays(days)) {
            view.getSpinnerDays().setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        } else {
            view.getSpinnerDays().setBorder(UIManager.getBorder("Spinner.border"));
        }

        int hours = (int) view.getSpinnerHours().getValue();
        if (!planValidation.isValidHours(hours)) {
            view.getSpinnerHours().setBorder(BorderFactory.createLineBorder(Color.RED));
            valid = false;
        } else {
            view.getSpinnerHours().setBorder(UIManager.getBorder("Spinner.border"));
        }

        return valid;
    }
}
