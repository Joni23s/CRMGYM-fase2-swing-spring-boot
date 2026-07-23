package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.panels;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto.ClientDTO;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.VectorIcon;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.ButtonFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.CardFactory;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components.StatusBadgeRenderer;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;

import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

@Component
public class ClientPanel extends JPanel implements Scrollable {
    
    private DefaultTableModel tableModelClients;
    private final ButtonFactory buttonFactory;
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
    public ClientPanel(ButtonFactory buttonFactory) {
        this.buttonFactory = buttonFactory;
        initComponentsHandCoded();
    }

    private void initComponentsHandCoded() {
        setOpaque(false); 
        
        setLayout(new MigLayout("ins 20 30 20 30, gapx 25, fill", "[340px!, fill][grow, fill]", "[grow, fill]"));
        
        // =========================================================================
        // 1. TARJETA DEL FORMULARIO (COLUMNA IZQUIERDA)
        // =========================================================================
        JPanel formCard = CardFactory.createCardPanel(new MigLayout("wrap 1, ins 18 22 18 22, fillx", "[grow, fill]", "[]12[]"));
        
        titleCharge = new JLabel("Nuevo Cliente");
        titleCharge.setFont(new Font("Oswald", Font.BOLD, 16));
        titleCharge.setForeground(Theme.BTN_PRIMARY_BG);
        formCard.add(titleCharge, "gapbottom 8, alignx left");

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
        
        // Panel de acciones del formulario
        JPanel actionGrid = new JPanel(new MigLayout("ins 0, gap 8, fillx", "[grow, fill][grow, fill]", "[]"));
        actionGrid.setOpaque(false);
        
        btnSave = buttonFactory.createPrimaryButton("Guardar", new VectorIcon("file-check", 14));
        btnSearch = buttonFactory.createSecondaryButton("Buscar", new VectorIcon("clipboard", 14));
        
        actionGrid.add(btnSave, "cell 0 0");
        actionGrid.add(btnSearch, "cell 1 0");
        formCard.add(actionGrid, "growx, gapbottom 8");
        
        btnClean = buttonFactory.createSecondaryButton("Limpiar Campos", new VectorIcon("history", 14));
        formCard.add(btnClean, "growx");

        add(formCard, "cell 0 0, grow");
        
        // =========================================================================
        // 2. TARJETA DEL LISTADO (COLUMNA DERECHA)
        // =========================================================================
        JPanel listCard = CardFactory.createCardPanel(new MigLayout("wrap 1, ins 18 22 18 22, fill", "[grow, fill]", "[]15[grow, fill]12[]"));
        
        JPanel listHeader = new JPanel(new MigLayout("ins 0, fillx", "[grow][]", "[]"));
        listHeader.setOpaque(false);
        
        titleList = new JLabel("Lista de Clientes");
        titleList.setFont(new Font("Oswald", Font.BOLD, 16));
        titleList.setForeground(Theme.BTN_PRIMARY_BG);
        listHeader.add(titleList, "cell 0 0, alignx left, aligny center");
        
        JPanel filterPanel = new JPanel(new MigLayout("ins 0, gapx 8", "[][][]"));
        filterPanel.setOpaque(false);
        
        btnAll = buttonFactory.createSecondaryButton("Todos");
        btnActive = buttonFactory.createSecondaryButton("Activos");
        btnInactive = buttonFactory.createSecondaryButton("Inactivos");
        
        filterPanel.add(btnAll);
        filterPanel.add(btnActive);
        filterPanel.add(btnInactive);
        
        listHeader.add(filterPanel, "cell 1 0, alignx right, aligny center");
        listCard.add(listHeader);
        
        tableListClients = new JTable();
        tableListClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableListClients.setShowGrid(false);
        tableListClients.setRowHeight(38);
        tableListClients.setFillsViewportHeight(true);
        
        tableListClients.setBackground(Theme.CARD_BG);
        tableListClients.setForeground(Theme.TEXT_ACTIVE);
        tableListClients.setSelectionBackground(Theme.CARD_BG_ALT);
        tableListClients.setSelectionForeground(Theme.TEXT_ACTIVE);
        
        JScrollPane scrollPaneTable = new JScrollPane(tableListClients);
        scrollPaneTable.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SLATE, 1, false));
        scrollPaneTable.getViewport().setBackground(Theme.CARD_BG);
        listCard.add(scrollPaneTable, "grow");
        
        JPanel rowActionsBar = new JPanel(new MigLayout("ins 0, gapx 12, alignx right"));
        rowActionsBar.setOpaque(false);
        
        btnModify = buttonFactory.createPrimaryButton("Modificar Socio", new VectorIcon("clipboard", 14));
        btnActivate = buttonFactory.createSecondaryButton("Activar", new VectorIcon("file-check", 14));
        btnDeactivate = buttonFactory.createSecondaryButton("Desactivar", new VectorIcon("history", 14));
        
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
        label.setFont(new Font("Oswald", Font.BOLD, 12));
        label.setForeground(Theme.TEXT_INACTIVE);
        return label;
    }

    public void initClientTable() {
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

        JTableHeader header = tableListClients.getTableHeader();
        header.setPreferredSize(new Dimension(0, 36));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Oswald", Font.BOLD, 12));
                setForeground(Theme.TEXT_ACTIVE);
                setBackground(Theme.BG_DARK);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER_SLATE),
                        BorderFactory.createEmptyBorder(0, 15, 0, 15)
                ));
                setHorizontalAlignment(column == 5 ? JLabel.CENTER : JLabel.LEFT);
                return this;
            }
        });

        DefaultTableCellRenderer normalRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                super.paintComponent(g);
            }

            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(new Font("Courier Prime", Font.PLAIN, 13));
                setForeground(Theme.TEXT_ACTIVE);
                setBackground((row % 2 == 0) ? Theme.CARD_BG : Theme.CARD_BG_ALT);
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
                return this;
            }
        };

        for (int i = 0; i < 7; i++) {
            if (i != 5) {
                tableListClients.getColumnModel().getColumn(i).setCellRenderer(normalRenderer);
            }
        }

        tableListClients.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer(false));
    }

    public void resetForm() {
        txtName.setText("");
        txtLastName.setText("");
        txtDni.setText("");
        txtMail.setText("");
        txtPhone.setText("");
        if (comboBoxPlan.getItemCount() > 0) {
            comboBoxPlan.setSelectedIndex(0);
        }
        
        titleCharge.setText("Nuevo Cliente");
        isEditMode = false;
        editingDni = -1;
        txtDni.setEnabled(true);
        
        txtName.putClientProperty("JComponent.outline", null);
        txtLastName.putClientProperty("JComponent.outline", null);
        txtDni.putClientProperty("JComponent.outline", null);
        txtMail.putClientProperty("JComponent.outline", null);
        txtPhone.putClientProperty("JComponent.outline", null);
        comboBoxPlan.putClientProperty("JComponent.outline", null);
    }

    public void showLoadingTable() {
        tableModelClients.setRowCount(0);
        tableModelClients.addRow(new Object[] {
                "", "", "Cargando datos...", "", "", "", ""
        });
        enableButtons(false);
    }

    public void updateTable(List<ClientDTO> clients) {
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
    }
    
    public void enableButtons(boolean enabled) {
        btnAll.setEnabled(enabled);
        btnActive.setEnabled(enabled);
        btnInactive.setEnabled(enabled);
        btnSearch.setEnabled(enabled);
    }

    public void showError(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public void showWarning(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public void showSuccess(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    // Getters y Setters
    /**
     * [MEJORA JUNIOR] Encapsulamiento: Extrae los datos ingresados en el formulario visual a un ClientDTO.
     */
    public ClientDTO getFormData() {
        int dni = 0;
        try {
            dni = Integer.parseInt(txtDni.getText().trim());
        } catch (NumberFormatException ignored) {}
        String phoneText = txtPhone.getText().trim();

        return ClientDTO.builder()
                .documentId(dni)
                .name(txtName.getText().trim())
                .lastName(txtLastName.getText().trim())
                .email(txtMail.getText().trim())
                .phoneNumber(phoneText.isEmpty() ? null : phoneText)
                .status("Activo")
                .namePlan(comboBoxPlan.getSelectedItem() != null ? comboBoxPlan.getSelectedItem().toString() : "")
                .build();
    }

    /**
     * [MEJORA JUNIOR] Encapsulamiento: Pobla el formulario visual a partir de los datos de un ClientDTO.
     */
    public void setFormData(ClientDTO dto) {
        if (dto == null) return;
        txtDni.setText(dto.getDocumentId() != 0 ? String.valueOf(dto.getDocumentId()) : "");
        txtName.setText(dto.getName() != null ? dto.getName() : "");
        txtLastName.setText(dto.getLastName() != null ? dto.getLastName() : "");
        txtPhone.setText(dto.getPhoneNumber() != null ? dto.getPhoneNumber() : "");
        txtMail.setText(dto.getEmail() != null ? dto.getEmail() : "");
        if (dto.getNamePlan() != null) {
            comboBoxPlan.setSelectedItem(dto.getNamePlan());
        }
    }

    public JTextField getTxtDni() { return txtDni; }
    public JTextField getTxtName() { return txtName; }
    public JTextField getTxtLastName() { return txtLastName; }
    public JTextField getTxtMail() { return txtMail; }
    public JTextField getTxtPhone() { return txtPhone; }
    public JComboBox<String> getComboBoxPlan() { return comboBoxPlan; }
    
    public JTable getTableListClients() { return tableListClients; }
    public JLabel getTitleCharge() { return titleCharge; }
    public JLabel getTitleList() { return titleList; }
    
    public JButton getBtnSave() { return btnSave; }
    public JButton getBtnSearch() { return btnSearch; }
    public JButton getBtnClean() { return btnClean; }
    public JButton getBtnAll() { return btnAll; }
    public JButton getBtnActive() { return btnActive; }
    public JButton getBtnInactive() { return btnInactive; }
    public JButton getBtnModify() { return btnModify; }
    public JButton getBtnActivate() { return btnActivate; }
    public JButton getBtnDeactivate() { return btnDeactivate; }

    public boolean isEditMode() { return isEditMode; }
    public void setEditMode(boolean editMode) { isEditMode = editMode; }
    
    public int getEditingDni() { return editingDni; }
    public void setEditingDni(int editingDni) { this.editingDni = editingDni; }

    // =========================================================================
    // IMPLEMENTACIÓN DE Scrollable
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
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getWidth() < 850) {
            return false;
        }
        if (getParent() instanceof JViewport) {
            return getPreferredSize().height <= ((JViewport) getParent()).getHeight();
        }
        return true;
    }
}
