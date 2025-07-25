package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.views.Dashboard;
import com.formdev.flatlaf.FlatDarkLaf;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;
import javax.swing.UIManager;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception e) {
			e.printStackTrace();
		}

		ConfigurableApplicationContext contextSpring =
				new SpringApplicationBuilder(Application.class) // <- cambio aquí
						.headless(false)
						.web(WebApplicationType.NONE)
						.run(args);

		EventQueue.invokeLater(() -> {
			Dashboard mainMenu = contextSpring.getBean(Dashboard.class);
			mainMenu.setVisible(true);
		});
	}
}

