package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.ClientDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.PlanDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.mappers.ClientMapper;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Plan;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.PlanService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.validations.ClientValidation;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.VectorIcon;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.ButtonFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.CardFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.StatusBadgeRenderer;

import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * =================================================================================
 * GUÍA DE ARQUITECTURA Swing + Spring Boot (PARA DESARROLLADORES TRAINEES)
 * =================================================================================
 * 
 * 1. INYECCIÓN DE DEPENDENCIAS CON SPRING BOOT:
 *    - @Component: Indica a Spring que esta clase es un Bean administrado por su contenedor.
 *    - @Autowired: Spring inyecta automáticamente las dependencias (servicios y validaciones)
 *      necesarias para comunicarse con la base de datos de manera limpia, sin instanciar
 *      manualmente las conexiones (Patrón IoC - Inversión de Control).
 * 
 * 2. FUNCIONAMIENTO DE putClientProperty EN SWING:
 *    - En Swing, JComponent proporciona una tabla asociativa interna (Key-Value) llamada
 *      Client Properties. Esto permite adjuntar metadatos o configuraciones dinámicas a un
 *      componente sin necesidad de crear una subclase o heredar de él.
 *    - FlatLaf lee estas propiedades durante el ciclo de renderizado (paint) para modificar
 *      el aspecto visual (p. ej., "JComponent.outline" para bordes de error, o
 *      "JTextField.placeholderText" para textos informativos).
 * 
 * 3. ARQUITECTURA DE FLUJO DE DATOS AL GUARDAR:
 *    [UI Input] -> [Validación con ClientValidation] -> [Conversión/Búsqueda de Entidades]
 *               -> [Service Layer / JPA Repository] -> [Actualización de Tabla UI (Event Dispatch Thread)]
 * 
 * 4. HILOS EN SWING (Event Dispatch Thread - EDT):
 *    - Swing es un framework mono-hilo. Toda modificación de componentes visuales (añadir filas,
 *      cambiar colores, repintar) debe ocurrir obligatoriamente en el Event Dispatch Thread (EDT)
 *      para evitar condiciones de carrera ("race conditions") y parpadeos visuales desagradables.
 * =================================================================================
 */
@Component
public class ClientPanel extends JPanel implements Scrollable {
    
    private DefaultTableModel tableModelClients;
    private final ClientService clientService;
    private final PlanService planService;
    private final ClientValidation clientValidation;
    private final ButtonFactory buttonFactory; // [MEJORA JUNIOR] Se inyecta ButtonFactory
    private boolean isEditMode = false;
    private int editingDni = -1;

    // Componentes del Formulario (Columna Izquierda)
    private JTextField txtDni;
    private JTextField txtName;
    private JTextField txtLastName;
    private JTextField txtMail;
    private JTextField txtPhone;
    private JComboBox<String> comboBoxPlan;
    
    // Componentes del Listado (Columna Derecha)
    private JTable tableListClients;
    private JLabel titleCharge;
    private JLabel titleList;
    
    // Botones del Formulario
    private JButton btnSave;
    private JButton btnSearch;
    private JButton btnClean;
    
    // Botones del Listado (Filtros y Operaciones de Fila)
    private JButton btnAll;
    private JButton btnActive;
    private JButton btnInactive;
    private JButton btnModify;
    private JButton btnActivate;
    private JButton btnDeactivate;

    @Autowired
    public ClientPanel(ClientService clientService, PlanService planService, ClientValidation clientValidation, ButtonFactory buttonFactory) {
        this.clientService = clientService;
        this.planService = planService;
        this.clientValidation = clientValidation;
        this.buttonFactory = buttonFactory;

        initComponentsHandCoded();
        initClientTable();
        loadPlansToComboBox();
        resetForm();
    }

    /**
     * Inicializa los componentes de la interfaz de usuario de forma manual.
     * Diseñado con un split layout asimétrico mediante MigLayout.
     */
    private void initComponentsHandCoded() {
        setOpaque(false); // Transparente para que se vea el fondo oscuro degradado del MainFrame
        
        /*
         * EXPLICACIÓN DE RESTRICCIONES DE MigLayout (Grid Layout Engine):
         * "ins 20 30 20 30": Define los márgenes internos (padding) superior, izquierdo, inferior y derecho.
         * "gapx 25": Espaciado horizontal de 25 píxeles entre columnas.
         * "fill": Indica que las celdas deben expandirse al tamaño máximo del contenedor.
         * "[340px!, fill]": Columna 1 fija a 340 píxeles estrictos para el formulario.
         * "[grow, fill]": Columna 2 variable que toma todo el espacio restante disponible del ancho.
         */
        setLayout(new MigLayout("ins 20 30 20 30, gapx 25, fill", "[340px!, fill][grow, fill]", "[grow, fill]"));
        
        // =========================================================================
        // 1. TARJETA DEL FORMULARIO (COLUMNA IZQUIERDA)
        // =========================================================================
        JPanel formCard = CardFactory.createCardPanel(new MigLayout("wrap 1, ins 18 22 18 22, fillx", "[grow, fill]", "[]12[]"));
        
        titleCharge = new JLabel("Nuevo Cliente");
        titleCharge.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleCharge.setForeground(Color.decode("#f8fafc"));
        formCard.add(titleCharge, "gapbottom 8, alignx left");

        // Inicialización de Inputs y configuración de Placeholders nativos de FlatLaf
        formCard.add(createFieldLabel("Nombre *"));
        txtName = new JTextField();
        txtName.putClientProperty("JTextField.placeholderText", "Nombre");
        formCard.add(txtName, "growx, gapbottom 8");
        
        formCard.add(createFieldLabel("Apellido *"));
        txtLastName = new JTextField();
        txtLastName.putClientProperty("JTextField.placeholderText", "Apellido");
        formCard.add(txtLastName, "growx, gapbottom 8");
        
        formCard.add(createFieldLabel("DNI *"));
        txtDni = new JTextField();
        txtDni.putClientProperty("JTextField.placeholderText", "DNI (Solo números)");
        /*
         * EXPLICACIÓN DE selectAllOnFocus:
         * Selecciona automáticamente todo el texto al ganar foco. Esto optimiza enormemente el flujo
         * de entrada de datos (data-entry) al permitir al usuario sobrescribir el valor de inmediato.
         */
        txtDni.putClientProperty("JTextField.selectAllOnFocus", true);
        formCard.add(txtDni, "growx, gapbottom 8");
        
        formCard.add(createFieldLabel("Celular"));
        txtPhone = new JTextField();
        txtPhone.putClientProperty("JTextField.placeholderText", "Teléfono de Contacto");
        formCard.add(txtPhone, "growx, gapbottom 8");
        
        formCard.add(createFieldLabel("Mail"));
        txtMail = new JTextField();
        txtMail.putClientProperty("JTextField.placeholderText", "Correo Electrónico");
        formCard.add(txtMail, "growx, gapbottom 8");
        
        formCard.add(createFieldLabel("Plan *"));
        comboBoxPlan = new JComboBox<>();
        formCard.add(comboBoxPlan, "growx, gapbottom 15");
        
        // Panel de acciones del formulario (Solo Guardar y Buscar en el formulario)
        JPanel actionGrid = new JPanel(new MigLayout("ins 0, gap 8, fillx", "[grow, fill][grow, fill]", "[]"));
        actionGrid.setOpaque(false);
        
        btnSave = buttonFactory.createPrimaryButton("Guardar", new VectorIcon("file-check", 14));
        btnSave.addActionListener(this::btnSaveActionPerformed);
        
        btnSearch = buttonFactory.createSecondaryButton("Buscar", new VectorIcon("clipboard", 14));
        btnSearch.addActionListener(this::btnSearchActionPerformed);
        
        actionGrid.add(btnSave, "cell 0 0");
        actionGrid.add(btnSearch, "cell 1 0");
        formCard.add(actionGrid, "growx, gapbottom 8");
        
        btnClean = buttonFactory.createSecondaryButton("Limpiar Campos", new VectorIcon("history", 14));
        btnClean.addActionListener(this::btnCleanActionPerformed);
        formCard.add(btnClean, "growx");

        add(formCard, "cell 0 0, grow");
        
        // =========================================================================
        // 2. TARJETA DEL LISTADO (COLUMNA DERECHA)
        // =========================================================================
        /*
         * EXPLICACIÓN DE RESTRICCIONES DE FILAS (FILA 1 CABECERA, FILA 2 TABLA, FILA 3 ACCIONES DE FILA):
         * "[]15[grow, fill]12[]": Cabecera automática, separación de 15px, tabla expansible verticalmente,
         * separación de 12px, y controles inferiores automáticos alineados.
         */
        JPanel listCard = new JPanel(new MigLayout("wrap 1, ins 18 22 18 22, fill", "[grow, fill]", "[]15[grow, fill]12[]")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.decode("#1e293b")); // Fondo Slate 800
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(Color.decode("#334155")); // Borde Slate 700
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        listCard.setOpaque(false);
        
        // Cabecera superior: Título del listado y filtros de datos
        JPanel listHeader = new JPanel(new MigLayout("ins 0, fillx", "[grow][]", "[]"));
        listHeader.setOpaque(false);
        
        titleList = new JLabel("Lista de Clientes");
        titleList.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleList.setForeground(Color.decode("#f8fafc"));
        listHeader.add(titleList, "cell 0 0, alignx left, aligny center");
        
        // Filtros de datos superiores
        JPanel filterPanel = new JPanel(new MigLayout("ins 0, gapx 8", "[][][]"));
        filterPanel.setOpaque(false);
        
        btnAll = buttonFactory.createSecondaryButton("Todos");
        btnAll.addActionListener(this::btnAllActionPerformed);
        
        btnActive = buttonFactory.createSecondaryButton("Activos");
        btnActive.addActionListener(this::btnActiveActionPerformed);
        
        btnInactive = buttonFactory.createSecondaryButton("Inactivos");
        btnInactive.addActionListener(this::btnInactiveActionPerformed);
        
        filterPanel.add(btnAll);
        filterPanel.add(btnActive);
        filterPanel.add(btnInactive);
        
        listHeader.add(filterPanel, "cell 1 0, alignx right, aligny center");
        listCard.add(listHeader);
        
        // Configuración e inicialización de la JTable
        tableListClients = new JTable();
        tableListClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableListClients.setShowGrid(false);
        tableListClients.setRowHeight(38);
        tableListClients.setFillsViewportHeight(true);
        
        tableListClients.setBackground(Color.decode("#1e293b"));
        tableListClients.setForeground(Color.decode("#f8fafc"));
        tableListClients.setSelectionBackground(Color.decode("#334155"));
        tableListClients.setSelectionForeground(Color.decode("#f8fafc"));
        
        JScrollPane scrollPaneTable = new JScrollPane(tableListClients);
        scrollPaneTable.setBorder(BorderFactory.createLineBorder(Color.decode("#334155"), 1, true));
        scrollPaneTable.getViewport().setBackground(Color.decode("#1e293b"));
        listCard.add(scrollPaneTable, "grow");
        
        // --- Barra de Acciones de Fila Inferior (UX MEJORADO) ---
        /*
         * EXPLICACIÓN UX:
         * Separamos conceptualmente los filtros globales (arriba de la tabla) de las operaciones
         * destructivas o de modificación sobre filas específicas (abajo de la tabla). Esto evita
         * pulsaciones accidentales y agrupa las acciones de fila cerca del punto focal de selección de la tabla.
         */
        JPanel rowActionsBar = new JPanel(new MigLayout("ins 0, gapx 12, alignx right"));
        rowActionsBar.setOpaque(false);
        
        btnModify = buttonFactory.createPrimaryButton("Modificar Socio", new VectorIcon("clipboard", 14));
        btnModify.addActionListener(this::btnModifyActionPerformed);
        
        btnActivate = buttonFactory.createSecondaryButton("Activar", new VectorIcon("file-check", 14));
        btnActivate.addActionListener(this::btnActivateActionPerformed);
        
        btnDeactivate = buttonFactory.createSecondaryButton("Desactivar", new VectorIcon("history", 14));
        btnDeactivate.addActionListener(this::btnDeactivateActionPerformed);
        
        rowActionsBar.add(btnActivate);
        rowActionsBar.add(btnDeactivate);
        rowActionsBar.add(btnModify);
        
        listCard.add(rowActionsBar, "growx, aligny bottom");
        
        add(listCard, "cell 1 0, grow");

        final MigLayout panelLayout = (MigLayout) getLayout();
        addComponentListener(new java.awt.event.ComponentAdapter() {
            private Boolean isSmall = null;

            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = getWidth();
                boolean shouldBeSmall = width < 850;

                if (isSmall == null || shouldBeSmall != isSmall) {
                    isSmall = shouldBeSmall;
                    removeAll();
                    if (shouldBeSmall) {
                        panelLayout.setLayoutConstraints("wrap 1, ins 12 16 12 16, gapy 16, fillx");
                        panelLayout.setColumnConstraints("[grow, fill]");
                        panelLayout.setRowConstraints("");
                        add(formCard, "growx");
                        add(listCard, "growx");
                    } else {
                        panelLayout.setLayoutConstraints("ins 20 30 20 30, gapx 25, fill");
                        panelLayout.setColumnConstraints("[340px!, fill][grow, fill]");
                        panelLayout.setRowConstraints("[grow, fill]");
                        add(formCard, "grow");
                        add(listCard, "grow");
                    }
                    revalidate();
                    repaint();
                }
            }
        });
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Color.decode("#94a3b8"));
        return label;
    }

    // [MEJORA JUNIOR] Se eliminaron styleNormalButton y styleAccentButton porque ahora usamos ButtonFactory

    private void initClientTable() {
        tableModelClients = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableListClients.setModel(tableModelClients);
        tableModelClients.setColumnIdentifiers(new Object[]{
                "DNI", "Nombre", "Apellido", "Email", "Teléfono", "Estado", "Plan"
        });

        // Personalización estética de la Cabecera de la Tabla
        JTableHeader header = tableListClients.getTableHeader();
        header.setPreferredSize(new Dimension(0, 36));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setForeground(Color.decode("#94a3b8"));
                setBackground(Color.decode("#0f172a")); // Cabecera oscura
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#334155")),
                        BorderFactory.createEmptyBorder(0, 15, 0, 15)
                ));
                setHorizontalAlignment(column == 5 ? JLabel.CENTER : JLabel.LEFT);
                return this;
            }
        });

        // Renderizador de Celdas de Datos
        DefaultTableCellRenderer normalRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                // Forzar anti-aliasing LCD para evitar textos borrosos o pixelados
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                super.paintComponent(g);
            }

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setForeground(Color.decode("#f8fafc"));
                
                // Color de fila alternado para mejor legibilidad horizontal
                setBackground((row % 2 == 0) ? Color.decode("#1e293b") : Color.decode("#151f32"));
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return this;
            }
        };

        for (int i = 0; i < 7; i++) {
            if (i != 5) {
                tableListClients.getColumnModel().getColumn(i).setCellRenderer(normalRenderer);
            }
        }

        // Asignar el renderizador de píldoras de estado personalizado a la columna 5 (Estado)
        tableListClients.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer(false));

        loadClientsToTable(clientService.getAllClientsDTO());
    }

    private void loadClientsToTable(List<ClientDTO> clients) {
        // Aseguramos que la actualización de la tabla se ejecute de forma segura en el EDT
        SwingUtilities.invokeLater(() -> {
            tableModelClients.setRowCount(0);
            for (ClientDTO dto : clients) {
                tableModelClients.addRow(new Object[] {
                        dto.getDocumentId(),
                        dto.getName(),
                        dto.getLastName(),
                        dto.getEmail(),
                        dto.getPhoneNumber(),
                        dto.getStatus(),
                        dto.getNamePlan()
                });
            }
        });
    }

    private void loadPlansToComboBox() {
        comboBoxPlan.removeAllItems();
        comboBoxPlan.addItem("Seleccione un Plan *");
        List<PlanDTO> planes = planService.findByIsActiveDTO(true);
        if (planes != null) {
            for (PlanDTO plan : planes) {
                comboBoxPlan.addItem(plan.toString());
            }
        }
    }

    private void resetForm() {
        txtName.setText("");
        txtLastName.setText("");
        txtDni.setText("");
        txtMail.setText("");
        txtPhone.setText("");
        comboBoxPlan.setSelectedIndex(0);
        
        titleCharge.setText("Nuevo Cliente");
        isEditMode = false;
        editingDni = -1;
        txtDni.setEnabled(true);
        
        // Limpiar indicadores visuales de error de FlatLaf
        txtName.putClientProperty("JComponent.outline", null);
        txtLastName.putClientProperty("JComponent.outline", null);
        txtDni.putClientProperty("JComponent.outline", null);
        txtMail.putClientProperty("JComponent.outline", null);
        txtPhone.putClientProperty("JComponent.outline", null);
        comboBoxPlan.putClientProperty("JComponent.outline", null);
    }

    /**
     * Valida de manera estricta los campos del formulario.
     * En caso de error, utiliza las propiedades nativas de FlatLaf para resaltar la caja.
     */
    private boolean isFormValid() {
        boolean valid = true;

        if (!clientValidation.isValidName(txtName.getText().trim())) {
            txtName.putClientProperty("JComponent.outline", "error");
            valid = false;
        } else {
            txtName.putClientProperty("JComponent.outline", null);
        }

        if (!clientValidation.isValidName(txtLastName.getText().trim())) {
            txtLastName.putClientProperty("JComponent.outline", "error");
            valid = false;
        } else {
            txtLastName.putClientProperty("JComponent.outline", null);
        }

        String dniText = txtDni.getText().trim();
        if (dniText.isEmpty()) {
            txtDni.putClientProperty("JComponent.outline", "error");
            valid = false;
        } else {
            try {
                Integer.parseInt(dniText);
                txtDni.putClientProperty("JComponent.outline", null);
            } catch (NumberFormatException e) {
                txtDni.putClientProperty("JComponent.outline", "error");
                valid = false;
            }
        }

        if (comboBoxPlan.getSelectedIndex() == 0) {
            comboBoxPlan.putClientProperty("JComponent.outline", "error");
            valid = false;
        } else {
            comboBoxPlan.putClientProperty("JComponent.outline", null);
        }

        return valid;
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            String name = txtName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String dniText = txtDni.getText().trim();
            String phoneText = txtPhone.getText().trim();
            String email = txtMail.getText().trim();
            String selectedPlanName = (String) comboBoxPlan.getSelectedItem();

            if (!isFormValid()) {
                JOptionPane.showMessageDialog(this, "Por favor completá todos los campos obligatorios.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int dni = Integer.parseInt(dniText);

            if (!isEditMode && !clientValidation.isDniAvailable(dni)) {
                JOptionPane.showMessageDialog(this, "❌ Ya existe un cliente con DNI: " + dni + "\nIntente con un DNI diferente.", "DNI duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!email.isEmpty() && !clientValidation.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "El email ingresado no tiene un formato válido.", "Email inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String phone = phoneText.isEmpty() ? null : phoneText;
            if (phone != null && !clientValidation.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(this, "El número de celular ingresado no es válido.", "Teléfono inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Optional<Plan> planOptional = planService.findByNamePlanIgnoreCase(selectedPlanName);
            if (planOptional.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró el plan seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Guardar o actualizar la entidad mapeada a través de la capa de servicio de Spring Boot
            Client client = new Client(dni, name, lastName, email, phone, true, planOptional.get());
            clientService.save(client);
            
            String msg = isEditMode ? "Cliente actualizado correctamente." : "Cliente guardado correctamente.";
            JOptionPane.showMessageDialog(this, msg, "Éxito", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error al guardar el cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        resetForm();
        loadClientsToTable(clientService.getAllClientsDTO());
    }

    private void btnModifyActionPerformed(java.awt.event.ActionEvent evt) {
        int filaSelected = tableListClients.getSelectedRow();
        if (filaSelected == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona a un cliente primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String documentId = tableListClients.getValueAt(filaSelected, 0).toString();
        String name = tableListClients.getValueAt(filaSelected, 1).toString();
        String lastName = tableListClients.getValueAt(filaSelected, 2).toString();
        String email = tableListClients.getValueAt(filaSelected, 3) != null ? tableListClients.getValueAt(filaSelected, 3).toString() : "";
        String phone = tableListClients.getValueAt(filaSelected, 4) != null ? tableListClients.getValueAt(filaSelected, 4).toString() : "";
        String namePlan = tableListClients.getValueAt(filaSelected, 6).toString();

        txtDni.setText(documentId);
        txtName.setText(name);
        txtLastName.setText(lastName);
        txtMail.setText(email);
        txtPhone.setText(phone);
        comboBoxPlan.setSelectedItem(namePlan);

        isEditMode = true;
        editingDni = Integer.parseInt(documentId);
        txtDni.setEnabled(false);
        titleCharge.setText("Modificar Cliente");
    }

    private void btnAllActionPerformed(java.awt.event.ActionEvent evt) {
        titleList.setText("Lista de Clientes");
        loadClientsToTable(clientService.getAllClientsDTO());
    }

    private void btnActiveActionPerformed(java.awt.event.ActionEvent evt) {
        titleList.setText("Lista de Clientes: Activos");
        loadClientsToTable(clientService.findByIsActiveDTO(true));
    }

    private void btnInactiveActionPerformed(java.awt.event.ActionEvent evt) {
        titleList.setText("Lista de Clientes: Inactivos");
        loadClientsToTable(clientService.findByIsActiveDTO(false));
    }

    private void btnActivateActionPerformed(java.awt.event.ActionEvent evt) {
        modifyStatus(true);
    }

    private void btnDeactivateActionPerformed(java.awt.event.ActionEvent evt) {
        modifyStatus(false);
    }

    private void modifyStatus(boolean status) {
        int filaSelected = tableListClients.getSelectedRow();
        if (filaSelected == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona a un cliente primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int dni = Integer.parseInt(tableListClients.getValueAt(filaSelected, 0).toString());
        Optional<Client> clientOpt = clientService.findById(dni);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            client.setIsActive(status);
            clientService.save(client);

            JOptionPane.showMessageDialog(this, "Estado actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            resetForm();
            loadClientsToTable(clientService.getAllClientsDTO());
        }
    }

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {
        Set<Client> clients = new HashSet<>();

        String name = txtName.getText().trim();
        if (!name.isEmpty()) {
            clients.addAll(clientService.findByName(name));
        }

        String lastName = txtLastName.getText().trim();
        if (!lastName.isEmpty()) {
            clients.addAll(clientService.findByLastName(lastName));
        }

        String dniText = txtDni.getText().trim();
        if (!dniText.isEmpty()) {
            try {
                int dni = Integer.parseInt(dniText);
                clientService.findById(dni).ifPresent(clients::add);
            } catch (NumberFormatException ignored) {}
        }

        String phone = txtPhone.getText().trim();
        if (!phone.isEmpty()) {
            clients.addAll(clientService.findByPhoneNumber(phone));
        }

        String email = txtMail.getText().trim();
        if (!email.isEmpty()) {
            clients.addAll(clientService.findByEmail(email));
        }

        if (comboBoxPlan.getSelectedIndex() != 0) {
            String selectedPlan = comboBoxPlan.getSelectedItem().toString();
            clients.addAll(clientService.findByCurrentPlan(selectedPlan));
        }

        titleList.setText("Lista de Clientes: Buscados");
        loadClientsToTable(clients.stream()
                .map(ClientMapper::toDTO)
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
        // En modo normal (lado a lado), llenar la pantalla y no hacer scroll principal.
        // En modo responsivo (vertical), permitir scroll principal.
        if (getWidth() < 850) {
            return false;
        }
        if (getParent() instanceof JViewport) {
            return getPreferredSize().height <= ((JViewport) getParent()).getHeight();
        }
        return true;
    }
}
