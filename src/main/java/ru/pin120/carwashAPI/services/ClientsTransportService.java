package ru.pin120.carwashAPI.services;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.models.ClientsTransport;
import ru.pin120.carwashAPI.repositories.ClientsTransportRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClientsTransportService {

    private final ClientsTransportRepository clientsTransportRepository;

    public ClientsTransportService(ClientsTransportRepository clientsTransportRepository) {
        this.clientsTransportRepository = clientsTransportRepository;
    }

    public List<ClientsTransport> getByClientId(Long clientId){
        return clientsTransportRepository.findByClientId(clientId);
    }

    public boolean existsClientTransport(ClientsTransport clientsTransport) {
        int countTransports = clientsTransportRepository.countByStateNumberAndClientIdAndTransportId(clientsTransport.getClTrStateNumber(), clientsTransport.getClient().getClId(), clientsTransport.getTransport().getTrId());
        return countTransports > 0;
    }

    public void save(ClientsTransport clientsTransport) {
        clientsTransportRepository.save(clientsTransport);
    }

    public Optional<ClientsTransport> findById(Long id) {
        return clientsTransportRepository.findByClTrId(id);
    }

    public boolean existsOtherClientTransport(ClientsTransport clientsTransport) {
        int countTransports = clientsTransportRepository.countByStateNumberAndClientIdAndTransportIdWithoutCurrentId(clientsTransport.getClTrStateNumber(), clientsTransport.getClient().getClId(), clientsTransport.getTransport().getTrId(), clientsTransport.getClTrId());
        return countTransports > 0;
    }

    @Transactional
    public void deleteById(Long clTrId) {
        clientsTransportRepository.deleteByClTrId(clTrId);
    }
}
