package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.validations;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class PlanValidation {
    private final Scanner console = new Scanner(System.in);

    public BigDecimal getValueInput(String message) {
        while (true) {
            System.out.print(message);
            String input = console.nextLine().trim();

            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Ingresá un número decimal válido (por ejemplo: 2999.99).");
            }
        }
    }

    public BigDecimal getValueInput(String message, BigDecimal currentValue) {
        while (true) {
            System.out.print(message);
            String input = console.nextLine().trim();

            if (input.equals("-")) {
                return currentValue;
            }

            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Ingresá un número decimal válido (por ejemplo: 2999.99).");
            }
        }
    }
}
