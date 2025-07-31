package com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.validations;

import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.model.Client;
import com.chicharronSoftware.CRMGYM.fase2.swing.spring.boot.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class ClientValidation {
    private final ClientService clientService;
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{7,15}$");
    private final Pattern NAME_PATTERN = Pattern.compile("^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+(?: [A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)*$");

    @Autowired
    public ClientValidation(ClientService clientService) {
        this.clientService = clientService;
    }

    public boolean isDniAvailable(Integer dni) {
        return clientService.findById(dni).isEmpty();
    }

    public boolean isDniAvailable(Integer dni, Client editingClient) {
        Optional<Client> existing = clientService.findById(dni);
        return existing.isEmpty() || existing.get().getDocumentId().equals(editingClient.getDocumentId());
    }

    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    public boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }
}
