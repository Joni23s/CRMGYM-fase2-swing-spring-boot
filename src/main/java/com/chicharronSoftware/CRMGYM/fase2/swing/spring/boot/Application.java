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

import org.springframework.cache.annotation.EnableCaching;

// [MEJORA JUNIOR] Con @EnableScheduling activamos el soporte para tareas
// programadas (@Scheduled) en toda la aplicación de Spring Boot.
@EnableScheduling
@EnableCaching
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

			// 2. Define the Slate Color Palette
			Color slate950 = Color.decode("#0b0f19");
			Color slate900 = Color.decode("#0f172a");
			Color slate800 = Color.decode("#1e293b");
			Color slate700 = Color.decode("#334155");
			Color slate600 = Color.decode("#475569");
			Color slate400 = Color.decode("#94a3b8");
			Color slate100 = Color.decode("#f8fafc");
			Color blue500 = Color.decode("#3b82f6");
			Color blue600 = Color.decode("#2563eb");

			// 3. Apply custom global design properties (They will now safely override
			// FlatLaf defaults)
			UIManager.put("Button.arc", 12);
			UIManager.put("Component.arc", 12);
			UIManager.put("TextComponent.arc", 12);
			UIManager.put("Table.rowHeight", 32);
			UIManager.put("TableHeader.height", 36);
			UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
			UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
			UIManager.put("ScrollBar.thumbArc", 999);
			UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));

			// 4. Overrides for Dark Slate Theme identity
			UIManager.put("Panel.background", slate950);
			UIManager.put("Background", slate950);
			UIManager.put("TitlePane.background", slate950);
			UIManager.put("TitlePane.foreground", slate100);
			UIManager.put("Label.foreground", slate100);
			UIManager.put("Label.disabledForeground", slate400);
			UIManager.put("Component.background", slate800);
			UIManager.put("Component.borderColor", slate700);
			UIManager.put("Component.focusedBorderColor", blue500);
			UIManager.put("Button.background", slate800);
			UIManager.put("Button.foreground", slate100);
			UIManager.put("Button.borderColor", slate700);
			UIManager.put("Button.focusedBorderColor", blue500);
			UIManager.put("Button.default.background", blue500);
			UIManager.put("Button.default.foreground", Color.WHITE);
			UIManager.put("Button.default.hoverBackground", blue600);
			UIManager.put("Separator.foreground", slate700);
			UIManager.put("Separator.background", slate700);
			UIManager.put("Table.background", slate800);
			UIManager.put("Table.foreground", slate100);
			UIManager.put("Table.gridColor", slate700);
			UIManager.put("Table.selectionBackground", slate700);
			UIManager.put("Table.selectionForeground", slate100);
			UIManager.put("Table.alternateRowColor", slate900);
			UIManager.put("TableHeader.background", slate900);
			UIManager.put("TableHeader.foreground", slate100);
			UIManager.put("TableHeader.separatorColor", slate700);
			UIManager.put("TextField.background", slate900);
			UIManager.put("TextField.foreground", slate100);
			UIManager.put("TextField.caretColor", slate100);
			UIManager.put("TextField.borderColor", slate700);
			UIManager.put("ComboBox.background", slate800);
			UIManager.put("ComboBox.foreground", slate100);
			UIManager.put("ComboBox.buttonBackground", slate800);
			UIManager.put("ScrollPane.background", slate950);
			UIManager.put("ScrollPane.borderColor", slate700);
			UIManager.put("ScrollBar.track", slate950);
			UIManager.put("ScrollBar.thumb", slate600);
			UIManager.put("ScrollBar.trackArc", 999);
			UIManager.put("TabbedPane.background", slate950);
			UIManager.put("TabbedPane.foreground", slate400);
			UIManager.put("TabbedPane.selectedForeground", slate100);
			UIManager.put("TabbedPane.selectedBackground", slate800);
			UIManager.put("TabbedPane.underlineColor", blue500);
			UIManager.put("MenuBar.background", slate950);
			UIManager.put("MenuBar.foreground", slate100);
			UIManager.put("Menu.background", slate950);
			UIManager.put("Menu.foreground", slate100);
			UIManager.put("MenuItem.background", slate950);
			UIManager.put("MenuItem.foreground", slate100);
			UIManager.put("MenuItem.selectionBackground", slate700);
			UIManager.put("MenuItem.selectionForeground", slate100);
			UIManager.put("PopupMenu.background", slate800);
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
