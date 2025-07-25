package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {

    private int documentId;
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String status; // "Activo" o "Inactivo"
    private String namePlan; // Nombre del plan actual
}
