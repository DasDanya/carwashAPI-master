package ru.pin120.carwashAPI.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.models.Client;
import ru.pin120.carwashAPI.repositories.ClientRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private static final int COUNT_CLIENTS_IN_PAGE = 3;
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getByPage(Integer pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex, COUNT_CLIENTS_IN_PAGE, Sort.by("clSurname", "clName", "clPhone", "clDiscount"));
        return clientRepository.findAll(pageable).getContent();
    }

    public void save(Client client) {
        clientRepository.save(client);
    }

    public Optional<Client> getById(Long id) {
        return clientRepository.findByClId(id);
    }

    @Transactional
    public void deleteById(Long clId) {
        clientRepository.deleteByClId(clId);
    }
}
