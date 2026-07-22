package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.MainFrame;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;
import javax.swing.UIManager;

import org.springframework.scheduling.annotation.EnableScheduling;

import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// [MEJORA JUNIOR] Con @EnableScheduling activamos el soporte para tareas
// programadas (@Scheduled) en toda la aplicación de Spring Boot.
@EnableScheduling
@SpringBootApplication
public class Application {

	// [MEJORA JUNIOR] Creamos un Logger para poder registrar los errores
	// inesperados
	// en la consola o archivo de log de forma estructurada.
	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		// [MEJORA JUNIOR] Configuramos un "Manejador Global de Excepciones".
		// Esto atrapa cualquier error que no hayamos controlado con try-catch
		// en la interfaz gráfica (EDT de Swing), evitando que la app falle en silencio.
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			logger.error("Error no controlado en el hilo {}: {}", thread.getName(), throwable.getMessage(), throwable);
			JOptionPane.showMessageDialog(null,
					"Ocurrió un error inesperado:\n" + throwable.getMessage(),
					"Error Interno",
					JOptionPane.ERROR_MESSAGE);
		});

		try {
			// 1. Setup FlatLaf Look and Feel FIRST so it establishes its base defaults
			com.formdev.flatlaf.FlatDarkLaf.setup();

			// 2. Definición de la Paleta de Colores Charcoal Slate Pro (Software de Escritorio POS)
			Color charcoalBase = Color.decode("#181c24");
			Color charcoalSurface = Color.decode("#222732");
			Color charcoalBorder = Color.decode("#333a48");
			Color slateTextMuted = Color.decode("#94a3b8");
			Color slateTextMain = Color.decode("#f8fafc");
			Color emeraldGreen = Color.decode("#059669");
			Color corporateBlue = Color.decode("#2563eb");

			// 3. Ajustes de bordes planos sutiles (6px) para software de escritorio operativo
			UIManager.put("Button.arc", 6);
			UIManager.put("Component.arc", 6);
			UIManager.put("TextComponent.arc", 6);
			UIManager.put("Table.rowHeight", 34);
			UIManager.put("TableHeader.height", 38);
			UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
			UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
			UIManager.put("ScrollBar.thumbArc", 999);
			UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

			// 4. Inserción de la identidad Charcoal Slate Pro en FlatLaf
			UIManager.put("Panel.background", charcoalBase);
			UIManager.put("Background", charcoalBase);
			UIManager.put("TitlePane.background", charcoalBase);
			UIManager.put("TitlePane.foreground", slateTextMain);
			UIManager.put("Label.foreground", slateTextMain);
			UIManager.put("Label.disabledForeground", slateTextMuted);
			UIManager.put("Component.background", charcoalSurface);
			UIManager.put("Component.borderColor", charcoalBorder);
			UIManager.put("Component.focusedBorderColor", corporateBlue);
			UIManager.put("Button.background", charcoalSurface);
			UIManager.put("Button.foreground", slateTextMain);
			UIManager.put("Button.borderColor", charcoalBorder);
			UIManager.put("Button.focusedBorderColor", corporateBlue);
			UIManager.put("Button.default.background", emeraldGreen);
			UIManager.put("Button.default.foreground", Color.WHITE);
			UIManager.put("Button.default.hoverBackground", Color.decode("#047857"));
			UIManager.put("Separator.foreground", charcoalBorder);
			UIManager.put("Separator.background", charcoalBorder);
			UIManager.put("Table.background", charcoalSurface);
			UIManager.put("Table.foreground", slateTextMain);
			UIManager.put("Table.gridColor", charcoalBorder);
			UIManager.put("Table.selectionBackground", charcoalBorder);
			UIManager.put("Table.selectionForeground", slateTextMain);
			UIManager.put("Table.alternateRowColor", Color.decode("#1d222b"));
			UIManager.put("TableHeader.background", Color.decode("#171b23"));
			UIManager.put("TableHeader.foreground", slateTextMain);
			UIManager.put("TableHeader.separatorColor", charcoalBorder);
			UIManager.put("TextField.background", Color.decode("#151820"));
			UIManager.put("TextField.foreground", slateTextMain);
			UIManager.put("TextField.caretColor", slateTextMain);
			UIManager.put("TextField.borderColor", charcoalBorder);
			UIManager.put("ComboBox.background", charcoalSurface);
			UIManager.put("ComboBox.foreground", slateTextMain);
			UIManager.put("ComboBox.buttonBackground", charcoalSurface);
			UIManager.put("ScrollPane.background", charcoalBase);
			UIManager.put("ScrollPane.borderColor", charcoalBorder);
			UIManager.put("ScrollBar.track", charcoalBase);
			UIManager.put("ScrollBar.thumb", charcoalBorder);
			UIManager.put("ScrollBar.trackArc", 999);
			UIManager.put("TabbedPane.background", charcoalBase);
			UIManager.put("TabbedPane.foreground", slateTextMuted);
			UIManager.put("TabbedPane.selectedForeground", slateTextMain);
			UIManager.put("TabbedPane.selectedBackground", charcoalSurface);
			UIManager.put("TabbedPane.underlineColor", corporateBlue);
			UIManager.put("MenuBar.background", charcoalBase);
			UIManager.put("MenuBar.foreground", slateTextMain);
			UIManager.put("Menu.background", charcoalBase);
			UIManager.put("Menu.foreground", slateTextMain);
			UIManager.put("MenuItem.background", charcoalBase);
			UIManager.put("MenuItem.foreground", slateTextMain);
			UIManager.put("MenuItem.selectionBackground", charcoalBorder);
			UIManager.put("MenuItem.selectionForeground", slateTextMain);
			UIManager.put("PopupMenu.background", charcoalSurface);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ConfigurableApplicationContext contextSpring = new SpringApplicationBuilder(Application.class) // <- cambio aquí
				.headless(false)
				.web(WebApplicationType.NONE)
				.run(args);

		EventQueue.invokeLater(() -> {
			MainFrame mainMenu = contextSpring.getBean(MainFrame.class);
			mainMenu.setVisible(true);
		});
	}
}
