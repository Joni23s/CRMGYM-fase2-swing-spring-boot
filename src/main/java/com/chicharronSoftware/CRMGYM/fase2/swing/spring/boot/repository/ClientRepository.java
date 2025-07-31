package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.currentPlan")
    List<Client> findAllWithPlan();

    List<Client> findByNameIgnoreCase(String Name);

    List<Client> findByLastNameIgnoreCase(String lastName);

    List<Client> findByIsActive(boolean isActive);

    List<Client> findByPhoneNumber(String phone);

    List<Client> findByEmail(String mail);

    List<Client> findByCurrentPlan_NamePlan(String namePlan);
}
