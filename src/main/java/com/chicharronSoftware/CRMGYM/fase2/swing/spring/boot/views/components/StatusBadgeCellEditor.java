package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Editor de celda que permite interactuar con los 3 puntos del StatusBadgeRenderer.
 */
public class StatusBadgeCellEditor extends AbstractCellEditor implements TableCellEditor {

    private final StatusBadgeRenderer renderer;
    private Object currentValue;
    private final JPopupMenu popupMenu;

    public StatusBadgeCellEditor(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
        this.renderer = new StatusBadgeRenderer(true);

        this.renderer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int rightPadding = 12;
                int dotsW = 4;
                // Si el clic ocurre en el area derecha (donde están los puntos)
                if (e.getX() > renderer.getWidth() - rightPadding - dotsW - 10) {
                    if (StatusBadgeCellEditor.this.popupMenu != null) {
                        StatusBadgeCellEditor.this.popupMenu.show(renderer, e.getX(), e.getY());
                    }
                }
                fireEditingStopped();
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        return currentValue;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentValue = value;
        return renderer.getTableCellRendererComponent(table, value, isSelected, true, row, column);
    }
}
