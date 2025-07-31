package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.validations;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class GeneralValidation {
    private final Scanner console = new Scanner(System.in);

    public int getIntInput(String message) {
        while (true) {
            System.out.print(message);

            try {
                return Integer.parseInt(console.nextLine().trim());

            } catch (NumberFormatException e) {
                System.out.println("Error: Ingresa un número válido.");
            }
        }
    }

    public int getIntInput(String message, int currentValue) {
        while (true) {
            System.out.print(message);
            String input = console.nextLine().trim();
            if (input.equals("-")){
                return currentValue;
            }

            try {
                return Integer.parseInt(input);

            } catch (NumberFormatException e) {
                System.out.println("Error: Ingresa un número válido.");
            }
        }
    }

    public String getStringInput(String message) {
        String input;
        while (true) {
            System.out.print(message);
            input = console.nextLine().trim();

            if (!input.isBlank()) {
                return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
            } else {
                System.out.println("Error: el campo no puede estar en blanco o vació.");
            }

        }
    }

    public String getStringInput(String message, String currentValue) {
        String input;
        while (true) {
            System.out.print(message);
            input = console.nextLine().trim();

            if (input.equals("-")){
                return currentValue;
            }

            if (!input.isBlank()) {
                return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
            } else {
                System.out.println("Error: el campo no puede estar en blanco o vació.");
            }

        }
    }

    public long getLongInput(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Long.parseLong(console.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingresa un número válido.");
            }
        }
    }

    public long getLongInput(String message, Long currentValue) {
        while (true) {
            System.out.print(message);

            String input = console.nextLine().trim();
            if (input.equals("-")){
                return currentValue;
            }
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingresa un número válido.");
            }
        }
    }

    public boolean getStateInput(String message, boolean currentValue) {
        while (true) {
            System.out.print(message);
            String input = console.nextLine().trim().toLowerCase();

            if (input.equals("-")) {
                return currentValue;
            } else if (input.equalsIgnoreCase("activo") || input.equalsIgnoreCase("inactivo")) {

                return input.equals("activo");
            } else {
                System.out.println("Error: ingresa 'activo', 'inactivo' o '-' para mantener el valor actual.");
            }
        }
    }
}
