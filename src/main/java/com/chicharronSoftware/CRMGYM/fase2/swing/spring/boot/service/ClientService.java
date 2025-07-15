package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Optional<Client> findById(Integer documentId) {
        return clientRepository.findById(documentId);
    }

    public List<Client> findByName(String name) {
        return clientRepository.findByNameIgnoreCase(name);
    }

    public List<Client> findByLastName(String lastName) {
        return clientRepository.findByLastNameIgnoreCase(lastName);
    }

    public List<Client> findByIsActive(boolean isActive) {
        return clientRepository.findByIsActive(isActive);
    }

    public void save(Client client) {
        clientRepository.save(client);
    }

    public void deactivate(Integer id) {
        clientRepository.findById(id).ifPresent(client -> {
            client.setIsActive(false);
            clientRepository.save(client);
        });
    }

    public void activate(Integer id) {
        clientRepository.findById(id).ifPresent(client -> {
            client.setIsActive(true);
            clientRepository.save(client);
        });
    }

}
