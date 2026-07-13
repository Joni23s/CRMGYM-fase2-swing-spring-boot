package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;

import javax.swing.*;
import java.awt.*;

/**
 * Factory para la creación de componentes visuales redondeados estilo "Card" consistentes
 * a lo largo de todos los paneles, utilizando el Theme unificado.
 */
public class CardFactory {

    public static void paintCardBackground(Graphics g, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        Theme.enableHighFidelity(g2);
        
        // Pintar fondo
        g2.setColor(Theme.CARD_BG);
        g2.fillRoundRect(0, 0, width, height, Theme.ARC_CARD, Theme.ARC_CARD);
        
        // Pintar borde
        g2.setColor(Theme.BORDER_SLATE);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, width - 1, height - 1, Theme.ARC_CARD, Theme.ARC_CARD);
        
        g2.dispose();
    }

    /**
     * Crea un panel redondeado con el diseño estandar de "Card" (fondo y bordes).
     * 
     * @param layoutManager LayoutManager para aplicar al contenedor.
     * @return JPanel con sobreescritura de paintComponent para bordes redondeados.
     */
    public static JPanel createCardPanel(LayoutManager layoutManager) {
        // [MEJORA JUNIOR] Centralizamos la instanciación de Cards aquí para que si el diseño cambia
        // solo debamos tocar este archivo, no los de cada vista independiente.
        JPanel cardPanel = new JPanel(layoutManager) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintCardBackground(g, getWidth(), getHeight());
            }
        };
        cardPanel.setOpaque(false);
        return cardPanel;
    }
}
