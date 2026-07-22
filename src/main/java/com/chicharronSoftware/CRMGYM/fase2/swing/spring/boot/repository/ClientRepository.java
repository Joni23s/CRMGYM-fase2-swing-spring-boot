package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * [MEJORA JUNIOR] Repositorio Spring Data JPA para la entidad Client.
 * 
 * Cambios realizados:
 * 1. Clave primaria Long (id_client): La interfaz ahora extiende JpaRepository<Client, Long>.
 * 2. Búsqueda por DNI (findByDocumentId): Permite encontrar a un cliente por su documento de identidad.
 * 3. Prevención de N+1: Mantiene la anotación @EntityGraph(attributePaths = {"currentPlan"}) para
 *    realizar un LEFT JOIN unificado en las lecturas masivas.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @EntityGraph(attributePaths = { "currentPlan" })
    Optional<Client> findByDocumentId(Integer documentId);

    boolean existsByDocumentId(Integer documentId);

    @EntityGraph(attributePaths = { "currentPlan" })
    List<Client> findAll();

    @EntityGraph(attributePaths = { "currentPlan" })
    List<Client> findByNameIgnoreCase(String name);

    @EntityGraph(attributePaths = { "currentPlan" })
    List<Client> findByLastNameIgnoreCase(String lastName);

    @EntityGraph(attributePaths = { "currentPlan" })
    List<Client> findByIsActive(boolean isActive);

    @EntityGraph(attributePaths = { "currentPlan" })
    List<Client> findByPhoneNumber(String phone);

    @EntityGraph(attributePaths = { "currentPlan" })
    List<Client> findByEmail(String mail);

    @EntityGraph(attributePaths = { "currentPlan" })
    List<Client> findByCurrentPlan_NamePlan(String namePlan);

}
