package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model;

import jakarta.persistence.*;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * [MEJORA JUNIOR] Entidad JPA que representa a un Socio (Cliente) en el sistema CRMGYM.
 * 
 * Cambios arquitectónicos realizados:
 * 1. Clave Primaria Surrogate (id_client): Se migró a un ID autoincremental de tipo Long (@Id).
 *    Esto permite editar o corregir el DNI del cliente sin romper las relaciones de clave foránea.
 * 2. DNI como Clave de Negocio Única (document_id): Se mantiene como un atributo de negocio único (@Column unique).
 * 3. Plan Actual (currentPlan): Apunta directamente al plan vigente o al estado "Sin Plan" para acceso rápido.
 * 4. Remoción de Lombok @Data: Se utiliza @Getter y @Setter explícitos junto con @EqualsAndHashCode(of = "id")
 *    para prevenir problemas de recursión infinita o LazyInitializationException al comparar entidades en colecciones.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "currentPlan")
@EqualsAndHashCode(of = "id")
@Builder
@Entity
@Table(name = "clients")
public class Client {

    /**
     * [MEJORA JUNIOR] Identificador único autogenerado en la base de datos (Clave Primaria Surrogate).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_client")
    private Long id;

    /**
     * [MEJORA JUNIOR] Número de documento (DNI) del socio. Actúa como identificador de negocio único.
     */
    @NotNull(message = "El DNI no puede ser nulo")
    @Column(name = "document_id", nullable = false, unique = true)
    private Integer documentId;

    /**
     * [MEJORA JUNIOR] Nombre del socio. No puede ser nulo ni estar en blanco.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * [MEJORA JUNIOR] Apellido del socio.
     */
    @NotBlank(message = "El apellido es obligatorio")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * [MEJORA JUNIOR] Correo electrónico único del socio. Validado con expresión regular.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * [MEJORA JUNIOR] Número de teléfono/celular de contacto.
     */
    @NotBlank(message = "El teléfono es obligatorio")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    /**
     * [MEJORA JUNIOR] Estado del socio (true = Activo, false = Inactivo).
     */
    @NotNull(message = "El estado del cliente es obligatorio")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * [MEJORA JUNIOR] Relación Muchos-a-Uno con el Plan actual contratado por el socio.
     * Carga perezosa (LAZY) para evitar traer datos innecesarios de memoria en consultas masivas.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_plan_id")
    private Plan currentPlan;

    /**
     * Constructor manual útil para crear clientes asignando DNI y plan actual sin conocer el ID de base de datos.
     */
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
