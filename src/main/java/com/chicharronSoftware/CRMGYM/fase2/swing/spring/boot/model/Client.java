package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Builder
@Table(name = "clients")
public class Client {

    // [MEJORA JUNIOR] Validamos que el ID (DNI) no sea nulo al momento de persistir.
    @NotNull(message = "El DNI no puede ser nulo")
    @Id
    @Column(name = "document_id")
    private Integer documentId;

    // [MEJORA JUNIOR] @NotBlank valida que el String no sea nulo, ni vacío, ni solo espacios en blanco.
    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "name",nullable = false)
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Column(name = "last_name",nullable = false)
    private String lastName;
    
    // [MEJORA JUNIOR] @Email valida que el texto tenga un formato de correo electrónico válido (ej. algo@dominio.com).
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Column(name = "email",nullable = false, unique = true)
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @NotNull(message = "El estado del cliente es obligatorio")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_plan_id")
    private Plan currentPlan;

    @OneToMany(mappedBy = "client", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<HistoricalPlan> historicalPlans;

    // Constructor útil para crear clientes sin historial, pero con plan
    public Client(Integer documentId, String name, String lastName, String email, String phoneNumber, Boolean isActive, Plan currentPlan) {
        this.documentId = documentId;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isActive = isActive;
        this.currentPlan = currentPlan;
    }

}
