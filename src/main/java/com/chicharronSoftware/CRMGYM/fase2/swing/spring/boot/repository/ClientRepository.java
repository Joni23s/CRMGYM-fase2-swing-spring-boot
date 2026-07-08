package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.currentPlan")
    List<Client> findAllWithPlan();

    // [MEJORA JUNIOR] Con @EntityGraph le decimos a Hibernate que, además de buscar
    // al cliente, nos traiga su "currentPlan" en la misma consulta (un LEFT OUTER JOIN).
    // Esto mata el problema N+1 al mapear los DTOs de listas completas.
    
    @EntityGraph(attributePaths = {"currentPlan"})
    List<Client> findAll();

    @EntityGraph(attributePaths = {"currentPlan"})
    List<Client> findByNameIgnoreCase(String Name);

    @EntityGraph(attributePaths = {"currentPlan"})
    List<Client> findByLastNameIgnoreCase(String lastName);


    @EntityGraph(attributePaths = {"currentPlan"})
    List<Client> findByIsActive(boolean isActive);

    @EntityGraph(attributePaths = {"currentPlan"})
    List<Client> findByPhoneNumber(String phone);

    @EntityGraph(attributePaths = {"currentPlan"})
    List<Client> findByEmail(String mail);

    @EntityGraph(attributePaths = {"currentPlan"})
    List<Client> findByCurrentPlan_NamePlan(String namePlan);

}
