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
			// 1. Setup FlatLaf Look and Feel
			com.formdev.flatlaf.FlatLightLaf.setup();

			// 2. Definición de la Paleta Vintage (Papel Envejecido, Tinta & Sangre de Toro)
			Color parchmentBase = Color.decode("#EFE3C8");
			Color parchmentSurface = Color.decode("#F5EDDA");
			Color parchmentAlt = Color.decode("#EADFC8");
			Color inkBorder = Color.decode("#211B15");
			Color inkTextMain = Color.decode("#211B15");
			Color inkTextMuted = Color.decode("#5C4E43");
			Color oxbloodRed = Color.decode("#8C2320");

			// 3. Ajustes de bordes rectos (Arc = 0) para software estilo vintage editorial
			UIManager.put("Button.arc", 0);
			UIManager.put("Component.arc", 0);
			UIManager.put("TextComponent.arc", 0);
			UIManager.put("Table.rowHeight", 34);
			UIManager.put("TableHeader.height", 38);
			UIManager.put("TableHeader.font", new Font("Oswald", Font.BOLD, 13));
			UIManager.put("Table.font", new Font("Courier Prime", Font.PLAIN, 13));
			UIManager.put("ScrollBar.thumbArc", 0);
			UIManager.put("ScrollBar.thumbInsets", new Insets(1, 1, 1, 1));

			// 4. Inserción de la identidad Vintage Parchment & Ink en FlatLaf
			UIManager.put("Panel.background", parchmentBase);
			UIManager.put("Background", parchmentBase);
			UIManager.put("TitlePane.background", parchmentBase);
			UIManager.put("TitlePane.foreground", inkTextMain);
			UIManager.put("Label.foreground", inkTextMain);
			UIManager.put("Label.disabledForeground", inkTextMuted);
			UIManager.put("Component.background", parchmentSurface);
			UIManager.put("Component.borderColor", inkBorder);
			UIManager.put("Component.focusedBorderColor", oxbloodRed);
			UIManager.put("Button.background", parchmentSurface);
			UIManager.put("Button.foreground", inkTextMain);
			UIManager.put("Button.borderColor", inkBorder);
			UIManager.put("Button.focusedBorderColor", oxbloodRed);
			UIManager.put("Button.default.background", oxbloodRed);
			UIManager.put("Button.default.foreground", Color.WHITE);
			UIManager.put("Button.default.hoverBackground", Color.decode("#6b1a18"));
			UIManager.put("Separator.foreground", inkBorder);
			UIManager.put("Separator.background", inkBorder);
			UIManager.put("Table.background", parchmentSurface);
			UIManager.put("Table.foreground", inkTextMain);
			UIManager.put("Table.gridColor", inkBorder);
			UIManager.put("Table.selectionBackground", parchmentAlt);
			UIManager.put("Table.selectionForeground", inkTextMain);
			UIManager.put("Table.alternateRowColor", parchmentAlt);
			UIManager.put("TableHeader.background", parchmentBase);
			UIManager.put("TableHeader.foreground", inkTextMain);
			UIManager.put("TableHeader.separatorColor", inkBorder);
			UIManager.put("TextField.background", parchmentSurface);
			UIManager.put("TextField.foreground", inkTextMain);
			UIManager.put("TextField.caretColor", inkTextMain);
			UIManager.put("TextField.borderColor", inkBorder);
			UIManager.put("ComboBox.background", parchmentSurface);
			UIManager.put("ComboBox.foreground", inkTextMain);
			UIManager.put("ComboBox.buttonBackground", parchmentSurface);
			UIManager.put("ScrollPane.background", parchmentBase);
			UIManager.put("ScrollPane.borderColor", inkBorder);
			UIManager.put("ScrollBar.track", parchmentBase);
			UIManager.put("ScrollBar.thumb", inkBorder);
			UIManager.put("ScrollBar.trackArc", 0);
			UIManager.put("TabbedPane.background", parchmentBase);
			UIManager.put("TabbedPane.foreground", inkTextMuted);
			UIManager.put("TabbedPane.selectedForeground", inkTextMain);
			UIManager.put("TabbedPane.selectedBackground", parchmentSurface);
			UIManager.put("TabbedPane.underlineColor", oxbloodRed);
			UIManager.put("MenuBar.background", parchmentBase);
			UIManager.put("MenuBar.foreground", inkTextMain);
			UIManager.put("Menu.background", parchmentBase);
			UIManager.put("Menu.foreground", inkTextMain);
			UIManager.put("MenuItem.background", parchmentBase);
			UIManager.put("MenuItem.foreground", inkTextMain);
			UIManager.put("MenuItem.selectionBackground", parchmentAlt);
			UIManager.put("MenuItem.selectionForeground", inkTextMain);
			UIManager.put("PopupMenu.background", parchmentSurface);
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
