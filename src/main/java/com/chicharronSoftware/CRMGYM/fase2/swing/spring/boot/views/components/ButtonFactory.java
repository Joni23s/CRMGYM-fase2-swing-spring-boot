package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.components;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.theme.Theme;
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;

/**
 * =================================================================================
 * [MEJORA JUNIOR] FÁBRICA DE BOTONES PARA DESIGN SYSTEM (Fase 2)
 * =================================================================================
 * Centraliza la creación y estilos de botones de la interfaz gráfica.
 * Implementa el patrón Factory para garantizar consistencia visual a lo largo
 * de la aplicación utilizando los tokens definidos en Theme.java.
 */
@Component
public class ButtonFactory {

    public JButton createPrimaryButton(String text, Icon icon) {
        JButton button = new JButton(text, icon);
        styleButton(button, Theme.BTN_PRIMARY_BG, Theme.BTN_PRIMARY_FG, Theme.BTN_PRIMARY_BG, new Color(0x701C1A), Theme.BTN_PRIMARY_FG);
        return button;
    }

    public JButton createSecondaryButton(String text, Icon icon) {
        JButton button = new JButton(text, icon);
        styleButton(button, Theme.BTN_SECONDARY_BG, Theme.BTN_SECONDARY_FG, Theme.BORDER_SLATE, Theme.CARD_BG_ALT, Theme.TEXT_ACTIVE);
        return button;
    }

    public JButton createPrimaryButton(String text) {
        return createPrimaryButton(text, null);
    }

    public JButton createSecondaryButton(String text) {
        return createSecondaryButton(text, null);
    }

    private void styleButton(JButton button, Color bg, Color fg, Color borderColor, Color hoverBg, Color hoverFg) {
        button.setIconTextGap(8);
        button.setFont(new Font("Oswald", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        
        button.putClientProperty("FlatLaf.style", String.format(
            "arc: %d; margin: %d %d %d %d; background: #%06x; foreground: #%06x; borderColor: #%06x; borderWidth: 1; hoverBackground: #%06x; hoverForeground: #%06x;",
            0, // Arc 0 for vintage stamp style
            6, 12, 6, 12, 
            bg.getRGB() & 0xFFFFFF,
            fg.getRGB() & 0xFFFFFF,
            borderColor.getRGB() & 0xFFFFFF,
            hoverBg.getRGB() & 0xFFFFFF,
            hoverFg.getRGB() & 0xFFFFFF
        ));
    }

    public void styleToolActionButton(JButton button) {
        button.setIconTextGap(10);
        button.setFont(new Font("Oswald", Font.BOLD, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.putClientProperty("JButton.buttonType", "toolBarButton");
        button.putClientProperty("FlatLaf.style",
                "arc: 0; " +
                "margin: 8 16 8 16; " +
                "foreground: #211B15; " + // Tinta
                "hoverBackground: #EADFC8; " + // Parchment Alt
                "hoverForeground: #8C2320;"); // Oxblood
    }
}
