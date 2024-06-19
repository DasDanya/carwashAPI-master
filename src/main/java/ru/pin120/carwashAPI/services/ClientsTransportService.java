package ru.pin120.carwashAPI.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.models.ClientsTransport;
import ru.pin120.carwashAPI.repositories.ClientsTransportRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис транспорта клиента
 */
@Service
public class ClientsTransportService {

    /**
     * Репозиторий транспорта клиента
     */
    private final ClientsTransportRepository clientsTransportRepository;

    /**
     * Внедрение зависимости
     * @param clientsTransportRepository репозиторий транспорта клиента
     */
    public ClientsTransportService(ClientsTransportRepository clientsTransportRepository) {
        this.clientsTransportRepository = clientsTransportRepository;
    }

    /**
     * Получение списка транспорта клиента
     * @param clientId id клиента
     * @return Список транспорта клиента
     */
    public List<ClientsTransport> getByClientId(Long clientId){
        return clientsTransportRepository.findByClientId(clientId);
    }

    /**
     * Проверяет существование транспорта клиента
     * @param clientsTransport транспорт клиента
     * @return true, если существует, иначе false
     */
    public boolean existsClientTransport(ClientsTransport clientsTransport) {
        int countTransports = clientsTransportRepository.countByStateNumberAndClientIdAndTransportId(clientsTransport.getClTrStateNumber(), clientsTransport.getClient().getClId(), clientsTransport.getTransport().getTrId());
        return countTransports > 0;
    }

    /**
     * Сохранение транспорта клиента
     * @param clientsTransport транспорт клиента
     */
    public void save(ClientsTransport clientsTransport) {
        clientsTransportRepository.save(clientsTransport);
    }

    /**
     * Получение транспорта клиента по id
     * @param id id транспорта клиента
     * @return Объект Optional с транспортом, если он существует
     */
    public Optional<ClientsTransport> findById(Long id) {
        return clientsTransportRepository.findByClTrId(id);
    }

    /**
     * Проверяет существование транспорта клиента, исключая текущий
     * @param clientsTransport транспрот клиента
     * @return true, если существует, иначе false
     */
    public boolean existsOtherClientTransport(ClientsTransport clientsTransport) {
        int countTransports = clientsTransportRepository.countByStateNumberAndClientIdAndTransportIdWithoutCurrentId(clientsTransport.getClTrStateNumber(), clientsTransport.getClient().getClId(), clientsTransport.getTransport().getTrId(), clientsTransport.getClTrId());
        return countTransports > 0;
    }

    /**
     * Удаление транспорта клиента по id
     * @param clTrId id транспрота клиента
     */
    @Transactional
    public void deleteById(Long clTrId) {
        clientsTransportRepository.deleteByClTrId(clTrId);
    }

    /**
     * Поиск транспорта конкретного клиента
     * @param clientId id клиента
     * @param mark марка
     * @param model модель
     * @param category категория
     * @param stateNumber госномер
     * @return Список найденного транспорта
     */
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

    /**
     * Получение списка со всем транспортом клиента
     * @return Список со всем транспортом клиента
     */
    public List<ClientsTransport> getAll() {
        return (List<ClientsTransport>) clientsTransportRepository.findAll(Sort.by("transport.trMark", "transport.trModel", "transport.categoryOfTransport.catTrName", "clTrStateNumber"));
    }

    /**
     * Получение списка транспорта клиентов по госномеру
     * @param stateNumber госномер
     * @return Список транспорта клиента
     */
    public List<ClientsTransport> getByStateNumber(String stateNumber) {
        return clientsTransportRepository.findByStateNumber(stateNumber);
    }
}
