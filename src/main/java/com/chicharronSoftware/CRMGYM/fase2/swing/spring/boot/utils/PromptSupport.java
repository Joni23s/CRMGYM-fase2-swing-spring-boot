package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class PromptSupport {

    public static void setPrompt(String promptText, JTextField textField) {
        Color originalForeground = textField.getForeground();
        Color promptColor = Color.GRAY;

        textField.setText(promptText);
        textField.setForeground(promptColor);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(promptText)) {
                    textField.setText("");
                    textField.setForeground(originalForeground);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(promptColor);
                    textField.setText(promptText);
                }
            }
        });
    }
}
