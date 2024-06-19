package ru.pin120.carwashAPI.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.models.Transport;
import ru.pin120.carwashAPI.repositories.TransportRepository;


import java.util.List;
import java.util.Optional;

/**
 * Сервис транспорта
 */
@Service
public class TransportService {

    private static final int COUNT_TRANSPORT_IN_PAGE = 12;

    /**
     * Репозиторий транспорта
     */
    private final TransportRepository transportRepository;

    /**
     * Внедрение зависимости
     * @param transportRepository репозиторий транспорта
     */
    public TransportService(TransportRepository transportRepository) {
        this.transportRepository = transportRepository;
    }

    /**
     * Получение списка транспорта по индексу страницы
     * @param pageIndex индекс страницы
     * @return Список транспорта
     */
    public List<Transport> getByPage(Integer pageIndex){
        Pageable pageable = PageRequest.of(pageIndex, COUNT_TRANSPORT_IN_PAGE, Sort.by( "trMark", "trModel", "categoryOfTransport.catTrName"));

        return transportRepository.findAll(pageable).getContent();
    }

    /**
     * Получение транспорта по id
     * @param id id транспорта
     * @return Объект Optional с транспортом, если он существует
     */
    public Optional<Transport> getById(Long id){
        return transportRepository.findByTrId(id);
    }

    /**
     * Проверка существования транспорта
     * @param transport транспорт
     * @return true, если существует, иначе false
     */
    public boolean existsTransport(Transport transport){
        int countTransport = transportRepository.countByTrMarkAndTrModelAndCategoryId(transport.getTrMark(), transport.getTrModel(), transport.getCategoryOfTransport().getCatTrId());
        return countTransport > 0;
    }

    /**
     * Проверка существования транспорта, исключая текущий
     * @param transport исключаемый транспорт
     * @return true, если существует, иначе false
     */
    public boolean existsOtherTransport(Transport transport){
        int countOtherTransport = transportRepository.countByTrMarkAndTrModelAndCategoryIdWithoutCurrent(transport.getTrMark(), transport.getTrModel(), transport.getCategoryOfTransport().getCatTrId(), transport.getTrId());
        return countOtherTransport > 0;
    }

    /**
     * Сохрание транспорта
     * @param transport транспорт
     */
    public void save(Transport transport){
        transportRepository.save(transport);
    }

    /**
     * Удаление транспорта по id
     * @param trId id транспорта
     */
    @Transactional
    public void deleteById(Long trId) {
        transportRepository.deleteByTrId(trId);
    }

    /**
     * Поиск транспорта
     * @param pageIndex индекс страницы
     * @param category категория
     * @param mark марка
     * @param model модель
     * @return Список найденного транспорта
     */
    public List<Transport> search(Integer pageIndex, String category, String mark, String model) {
        Pageable pageable = PageRequest.of(pageIndex, COUNT_TRANSPORT_IN_PAGE, Sort.by( "trMark", "trModel", "categoryOfTransport.catTrName"));
        if(mark != null && model != null && category != null){
            return transportRepository.findByMarkAndModelAndCategory(mark,model,category,pageable);
        }else if(mark != null && model != null){
            return transportRepository.findByMarkAndModel(mark,model,pageable);
        }else if(mark != null && category != null){
            return transportRepository.findByMarkAndCategory(mark,category,pageable);
        }else if(model != null && category != null){
            return transportRepository.findByModelAndCategory(model,category,pageable);
        }else if(mark != null){
            return transportRepository.findByMark(mark,pageable);
        }else if(model != null){
            return transportRepository.findByModel(model,pageable);
        }else if(category != null){
            return transportRepository.findByCategory(category,pageable);
        }

        return null;
    }
}
