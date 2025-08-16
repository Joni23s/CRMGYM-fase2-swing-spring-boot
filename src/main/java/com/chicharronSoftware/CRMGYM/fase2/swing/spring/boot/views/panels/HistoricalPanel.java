
package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.HistoricalPlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.*;
import java.util.List;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.HistoricalPlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.utils.PromptSupport;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

@Component
public class HistoricalPanel extends JPanel {

    private DefaultTableModel tableModelHistorical;
    private final ClientService clientService;
    private final PlanService planService;
    private final HistoricalPlanService historicalPlanService;
    private Border defaultBorder;

    public HistoricalPanel(ClientService clientService, PlanService planService, HistoricalPlanService historicalPlanService) {
        this.clientService = clientService;
        this.planService = planService;
        this.historicalPlanService = historicalPlanService;
        initComponents();
        initHistoricalPlanTable();
        initRadioButtonsClients();
        initRadioButtonsPlans();
    }

    private void initHistoricalPlanTable() {
        tableModelHistorical = new DefaultTableModel();
        tableListClients.setModel(tableModelHistorical);
        // Definir cabeceras de la tabla
        tableModelHistorical.setColumnIdentifiers(new Object[]{
                "Id", "Inicio", "Fin", "Estado", "DNI", "Cliente", "Plan"
        });
        loadHistoricalPlanToTable(clientService.findAll());
    }

    /**
     * Carga la lista de clientes en la tabla.
     */
    private void loadHistoricalPlanToTable(List<Client> clients) {
        //Clientes desde el service
        tableModelHistorical.setRowCount(0);
        List<HistoricalPlanDTO> historicalPlanDTOS = clients.stream()
                .flatMap(client -> historicalPlanService
                        .findByClientWithDetails(client.getDocumentId().toString()).stream())
                .toList();


        for (HistoricalPlanDTO dto : historicalPlanDTOS) {
            tableModelHistorical.addRow(new Object[] {
                    dto.getIdHistorical(),
                    dto.getStartDate(),
                    dto.getEndDate(),
                    dto.getIsActive(),
                    dto.getDocumentId(),
                    dto.getClientName(),
                    dto.getPlanName()
            });
        }
    }

    private void initRadioButtonsPlans() {
        ButtonGroup groupPlanStatus = new ButtonGroup();
        groupPlanStatus.add(btnPlanActive);
        groupPlanStatus.add(btnPlanInactive);
        btnPlanActive.setSelected(true);
        loadPlansToComboBox(true);

        btnPlanActive.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadPlansToComboBox(true); // Activo
            }
        });

        btnPlanInactive.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadPlansToComboBox(false); // Inactivo
            }
        });
    }

    private void initRadioButtonsClients() {
        ButtonGroup groupClientStatus = new ButtonGroup();
        groupClientStatus.add(btnClientActive);
        groupClientStatus.add(btnClientInactive);

        btnClientActive.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadHistoricalPlanToTable(clientService.findByIsActive(true));
            }
        });

        btnClientInactive.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadHistoricalPlanToTable(clientService.findByIsActive(false));
            }
        });
    }

    /**
     * Carga todos los planes disponibles en el comboBox.
     * El primer ítem es un texto fijo obligatorio.
     */
    private void loadPlansToComboBox(boolean active) {
        comboBoxPlan.removeAllItems();
        comboBoxPlan.addItem("Seleccione un Plan *");

        List<PlanDTO> planes = planService.findByIsActiveDTO(active);
        if (planes != null) {
            for (PlanDTO plan : planes) {
                comboBoxPlan.addItem(plan.toString());
            }
        }
    }

    private void resetForm() {
        PromptSupport.setPrompt("Ingrese el DNI *", txtDni);
        PromptSupport.setPrompt("Ingrese el nombre *", txtName);
        PromptSupport.setPrompt("Ingrese el apellido *", txtLastName);
        PromptSupport.setPrompt("Ingrese el mail", txtMail);
        PromptSupport.setPrompt("Ingrese el celular", txtPhone);
        comboBoxPlan.setSelectedIndex(0);
        titleCharge.setText("Nuevo Cliente");
        txtDni.setBorder(defaultBorder);
        txtName.setBorder(defaultBorder);
        txtLastName.setBorder(defaultBorder);
        txtMail.setBorder(defaultBorder);
        txtPhone.setBorder(defaultBorder);
        comboBoxPlan.setBorder(defaultBorder);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtMail = new JTextField();
        txtPhone = new JTextField();
        labelPhone = new JLabel();
        labelMail = new JLabel();
        jRadioButton7 = new JRadioButton();
        jRadioButton9 = new JRadioButton();
        titleMain = new JLabel();
        panelNewClient = new JPanel();
        titleCharge = new JLabel();
        labelName = new JLabel();
        txtName = new JTextField();
        labelLastName = new JLabel();
        txtLastName = new JTextField();
        labelDni = new JLabel();
        txtDni = new JTextField();
        labelPlan = new JLabel();
        btnSearch = new JButton();
        btnCleanPanels = new JButton();
        comboBoxPlan = new JComboBox<>();
        btnClientActive = new JRadioButton();
        btnClientInactive = new JRadioButton();
        titleClient = new JLabel();
        titlePlan = new JLabel();
        btnPlanActive = new JRadioButton();
        btnPlanInactive = new JRadioButton();
        labelPhone1 = new JLabel();
        txtPhone1 = new JTextField();
        btnName = new JRadioButton();
        btnLastName = new JRadioButton();
        btnDni = new JRadioButton();
        btnPhone = new JRadioButton();
        btnPlan = new JRadioButton();
        panelTable = new JPanel();
        scrollPaneTable = new JScrollPane();
        tableListClients = new JTable();
        titleList = new JLabel();
        btnSelect = new JButton();
        btnCleanTable = new JButton();

        txtMail.setText("Ingrese el mail");

        txtPhone.setText("Ingrese el celular");

        labelPhone.setHorizontalAlignment(SwingConstants.CENTER);
        labelPhone.setText("Celular");

        labelMail.setHorizontalAlignment(SwingConstants.CENTER);
        labelMail.setText("Mail");

        jRadioButton7.setText("Usar");

        jRadioButton9.setText("Usar");

        setBackground(new Color(120, 120, 120));
        setMinimumSize(new Dimension(749, 580));

        titleMain.setFont(new Font("Segoe UI", 1, 24)); // NOI18N
        titleMain.setHorizontalAlignment(SwingConstants.CENTER);
        titleMain.setText("Historial");

        titleCharge.setFont(new Font("Segoe UI", 1, 24)); // NOI18N
        titleCharge.setHorizontalAlignment(SwingConstants.CENTER);
        titleCharge.setText("Busqueda");

        labelName.setHorizontalAlignment(SwingConstants.CENTER);
        labelName.setText("Nombre");

        txtName.setText("Ingrese el nombre *");

        labelLastName.setHorizontalAlignment(SwingConstants.CENTER);
        labelLastName.setText("Apellido");

        txtLastName.setText("Ingrese el apellido *");

        labelDni.setHorizontalAlignment(SwingConstants.CENTER);
        labelDni.setText("DNI");

        txtDni.setText("Ingrese el DNI *");

        labelPlan.setHorizontalAlignment(SwingConstants.CENTER);
        labelPlan.setText("Seleccione el plan");

        btnSearch.setText("Buscar");
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnCleanPanels.setText("Limpiar");
        btnCleanPanels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCleanPanelsActionPerformed(evt);
            }
        });

        comboBoxPlan.setModel(new DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnClientActive.setText("Activo");

        btnClientInactive.setText("Inactivo");

        titleClient.setFont(new Font("Roboto", 1, 14)); // NOI18N
        titleClient.setHorizontalAlignment(SwingConstants.CENTER);
        titleClient.setText("Por Cliente");

        titlePlan.setFont(new Font("Roboto", 1, 14)); // NOI18N
        titlePlan.setHorizontalAlignment(SwingConstants.CENTER);
        titlePlan.setText("Por Plan");

        btnPlanActive.setText("Activo");

        btnPlanInactive.setText("Inactivo");

        labelPhone1.setHorizontalAlignment(SwingConstants.CENTER);
        labelPhone1.setText("Celular");

        txtPhone1.setText("Ingrese el celular");

        btnName.setText("Usar");

        btnLastName.setText("Usar");

        btnDni.setText("Usar");

        btnPhone.setText("Usar");

        btnPlan.setText("Usar");

        GroupLayout panelNewClientLayout = new GroupLayout(panelNewClient);
        panelNewClient.setLayout(panelNewClientLayout);
        panelNewClientLayout.setHorizontalGroup(
            panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelNewClientLayout.createSequentialGroup()
                .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(titleClient, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, panelNewClientLayout.createSequentialGroup()
                        .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(panelNewClientLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnClientActive))
                            .addGroup(panelNewClientLayout.createSequentialGroup()
                                .addComponent(labelName, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(panelNewClientLayout.createSequentialGroup()
                                .addComponent(btnClientInactive)
                                .addGap(34, 34, 34))
                            .addComponent(btnName, GroupLayout.Alignment.TRAILING)))
                    .addGroup(panelNewClientLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(GroupLayout.Alignment.TRAILING, panelNewClientLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnCleanPanels, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
                            .addComponent(titleCharge, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtDni, GroupLayout.Alignment.TRAILING)
                            .addComponent(txtName, GroupLayout.Alignment.TRAILING)
                            .addComponent(txtLastName, GroupLayout.Alignment.TRAILING)
                            .addComponent(comboBoxPlan, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPhone1, GroupLayout.Alignment.TRAILING)
                            .addGroup(panelNewClientLayout.createSequentialGroup()
                                .addComponent(labelPlan, GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPlan))
                            .addGroup(panelNewClientLayout.createSequentialGroup()
                                .addComponent(labelLastName, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnLastName))
                            .addGroup(panelNewClientLayout.createSequentialGroup()
                                .addComponent(labelDni, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnDni))
                            .addGroup(panelNewClientLayout.createSequentialGroup()
                                .addComponent(labelPhone1, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPhone))
                            .addGroup(GroupLayout.Alignment.TRAILING, panelNewClientLayout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(btnPlanActive)
                                .addGap(18, 18, 18)
                                .addComponent(btnPlanInactive)
                                .addGap(36, 36, 36))
                            .addComponent(titlePlan, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panelNewClientLayout.setVerticalGroup(
            panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(panelNewClientLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleCharge, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(titleClient)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClientActive)
                    .addComponent(btnClientInactive))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(labelName)
                    .addComponent(btnName))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(labelLastName)
                    .addComponent(btnLastName))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLastName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(labelDni)
                    .addComponent(btnDni))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDni, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(labelPhone1)
                    .addComponent(btnPhone))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPhone1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(titlePlan)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPlanActive)
                    .addComponent(btnPlanInactive))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(labelPlan)
                    .addComponent(btnPlan))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(comboBoxPlan, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(panelNewClientLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCleanPanels, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        tableListClients.setModel(new DefaultTableModel(
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
        scrollPaneTable.setViewportView(tableListClients);

        titleList.setFont(new Font("Segoe UI", 1, 24)); // NOI18N
        titleList.setHorizontalAlignment(SwingConstants.CENTER);
        titleList.setText("Resultados");

        btnSelect.setText("Seleccionar");
        btnSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnSelectActionPerformed(evt);
            }
        });

        btnCleanTable.setText("Limpiar");
        btnCleanTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCleanTableActionPerformed(evt);
            }
        });

        GroupLayout panelTableLayout = new GroupLayout(panelTable);
        panelTable.setLayout(panelTableLayout);
        panelTableLayout.setHorizontalGroup(
            panelTableLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, panelTableLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelTableLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(titleList, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPaneTable, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))
                .addGap(185, 185, 185))
            .addGroup(panelTableLayout.createSequentialGroup()
                .addGap(130, 130, 130)
                .addComponent(btnCleanTable, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSelect, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTableLayout.setVerticalGroup(
            panelTableLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, panelTableLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleList, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(scrollPaneTable, GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(panelTableLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelect, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCleanTable, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelNewClient, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelTable, GroupLayout.PREFERRED_SIZE, 496, GroupLayout.PREFERRED_SIZE))
                    .addComponent(titleMain, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(7, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(titleMain)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelTable, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelNewClient, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        Set<Client> clients = new HashSet<>();

        // Buscar por nombre
        if (btnName.isSelected()) {
            String name = txtName.getText().trim();
            if (!name.isEmpty() && !name.equals("Ingrese el nombre *")) {
                clients.addAll(clientService.findByName(name));
            }
        }

        // Buscar por apellido
        if (btnLastName.isSelected()) {
            String lastName = txtLastName.getText().trim();
            if (!lastName.isEmpty() && !lastName.equals("Ingrese el apellido *")) {
                clients.addAll(clientService.findByLastName(lastName));
            }
        }

        // Buscar por DNI
        if (btnDni.isSelected()) {
            String dniText = txtDni.getText().trim();
            if (!dniText.isEmpty() && !dniText.equals("Ingrese el DNI *")) {
                try {
                    int dni = Integer.parseInt(dniText);
                    clientService.findById(dni).ifPresent(clients::add);
                } catch (NumberFormatException ignored) {
                    // Mostrar mensaje
                }
            }
        }

        // Buscar por plan
        if (btnPlan.isSelected()) {
            if (comboBoxPlan.getSelectedIndex() != 0) {
                String selectedPlan = comboBoxPlan.getSelectedItem().toString();
                clients.addAll(clientService.findByCurrentPlan(selectedPlan));
            }
        }

        // Mostrar resultados
        titleList.setText("Lista de Historial: Buscados");

        loadHistoricalPlanToTable(new ArrayList<>(clients));
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnCleanPanelsActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCleanPanelsActionPerformed
        resetForm();
    }//GEN-LAST:event_btnCleanPanelsActionPerformed

    private void btnSelectActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnSelectActionPerformed
        int filaSelected = tableListClients.getSelectedRow();

        // Validar que haya selección
        if (filaSelected == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona a un cliente primero.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtener el ID del cliente desde la columna 4
            String idStr = tableListClients.getValueAt(filaSelected, 4).toString();
            int clientId = Integer.parseInt(idStr);

            // Buscar cliente
            Optional<Client> clientOpt = clientService.findById(clientId);
            if (clientOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se encontró el cliente con ID: " + clientId,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Client client = clientOpt.get();

            // Crear lista modificable con el cliente
            List<Client> clients = new ArrayList<>();
            clients.add(client);

            // Actualizar título y tabla
            titleList.setText("Historial de Cliente: " + client.getDocumentId());
            loadHistoricalPlanToTable(clients);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "El valor de ID no es válido.",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al obtener el cliente: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSelectActionPerformed

    private void btnCleanTableActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnCleanTableActionPerformed
        tableModelHistorical.setRowCount(0);
        titleList.setText("Lista de Historial");
    }//GEN-LAST:event_btnCleanTableActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton btnCleanPanels;
    private JButton btnCleanTable;
    private JRadioButton btnClientActive;
    private JRadioButton btnClientInactive;
    private JRadioButton btnDni;
    private JRadioButton btnLastName;
    private JRadioButton btnName;
    private JRadioButton btnPhone;
    private JRadioButton btnPlan;
    private JRadioButton btnPlanActive;
    private JRadioButton btnPlanInactive;
    private JButton btnSearch;
    private JButton btnSelect;
    private JComboBox<String> comboBoxPlan;
    private JRadioButton jRadioButton7;
    private JRadioButton jRadioButton9;
    private JLabel labelDni;
    private JLabel labelLastName;
    private JLabel labelMail;
    private JLabel labelName;
    private JLabel labelPhone;
    private JLabel labelPhone1;
    private JLabel labelPlan;
    private JPanel panelNewClient;
    private JPanel panelTable;
    private JScrollPane scrollPaneTable;
    private JTable tableListClients;
    private JLabel titleCharge;
    private JLabel titleClient;
    private JLabel titleList;
    private JLabel titleMain;
    private JLabel titlePlan;
    private JTextField txtDni;
    private JTextField txtLastName;
    private JTextField txtMail;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtPhone1;
    // End of variables declaration//GEN-END:variables
}
