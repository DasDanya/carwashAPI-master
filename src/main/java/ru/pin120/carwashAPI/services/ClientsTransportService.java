package ru.pin120.carwashAPI.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;
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

    public List<ClientsTransport> search(@NotNull Long clientId, String mark, String model, String category, String stateNumber) {
        if(mark != null && model != null && category != null && stateNumber != null){
            return clientsTransportRepository.findByClientIdAndMarkAndModelAndCategoryAndStateNumber(clientId,mark,model,category,stateNumber);
        }else if(mark != null && model != null && category != null){
            return clientsTransportRepository.findByClientIdAndMarkAndModelAndCategory(clientId, mark,model,category);
        }else if(mark != null && model != null && stateNumber != null){
            return clientsTransportRepository.findByClientIdAndMarkAndModelAndStateNumber(clientId,mark,model,stateNumber);
        }else if(mark != null && category != null && stateNumber != null){
            return clientsTransportRepository.findByClientIdAndMarkAndCategoryAndStateNumber(clientId,mark,category,stateNumber);
        }else if(model != null && category != null && stateNumber != null){
            return clientsTransportRepository.findByClientIdAndModelAndCategoryAndStateNumber(clientId,model,category,stateNumber);
        }else if(mark != null && model != null){
            return clientsTransportRepository.findByClientIdAndMarkAndModel(clientId, mark,model);
        }else if(mark != null && category != null){
            return clientsTransportRepository.findByClientIdAndMarkAndCategory(clientId,mark,category);
        }else if(mark != null && stateNumber != null){
            return clientsTransportRepository.findByClientIdAndMarkAndStateNumber(clientId, mark,stateNumber);
        }else if(model != null && category != null){
            return clientsTransportRepository.findByClientIdAndModelAndCategory(clientId,model,category);
        }else if(model != null && stateNumber != null){
            return clientsTransportRepository.findByClientIdAndModelAndStateNumber(clientId,model,stateNumber);
        }else if(category != null && stateNumber != null){
            return clientsTransportRepository.findByClientIdAndCategoryAndStateNumber(clientId, category,stateNumber);
        }else if(mark != null){
            return clientsTransportRepository.findByClientIdAndMark(clientId,mark);
        }else if(model != null){
            return clientsTransportRepository.findByClientIdAndModel(clientId,model);
        }else if(category != null){
            return clientsTransportRepository.findByClientIdAndCategory(clientId,category);
        }else if(stateNumber != null){
            return clientsTransportRepository.findByClientIdAndStateNumber(clientId,stateNumber);
        }

        return null;
    }

    public List<ClientsTransport> getAll() {
        return (List<ClientsTransport>) clientsTransportRepository.findAll(Sort.by("transport.trMark", "transport.trModel", "transport.categoryOfTransport.catTrName", "clTrStateNumber"));
    }

    public List<ClientsTransport> getByStateNumber(String stateNumber) {
        return clientsTransportRepository.findByStateNumber(stateNumber);
    }
}
