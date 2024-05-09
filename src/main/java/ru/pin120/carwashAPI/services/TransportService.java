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

@Service
public class TransportService {

    private static final int COUNT_TRANSPORT_IN_PAGE = 6;
    private final TransportRepository transportRepository;

    public TransportService(TransportRepository transportRepository) {
        this.transportRepository = transportRepository;
    }


    public List<Transport> getByPage(Integer pageIndex){
        Pageable pageable = PageRequest.of(pageIndex, COUNT_TRANSPORT_IN_PAGE, Sort.by("categoryOfTransport.catTrName", "trMark", "trModel"));

        return transportRepository.findAll(pageable).getContent();
    }

    public Optional<Transport> getById(Long id){
        return transportRepository.findByTrId(id);
    }

    public boolean existsTransport(Transport transport){
        int countTransport = transportRepository.countByTrMarkAndTrModelAndCategoryId(transport.getTrMark(), transport.getTrModel(), transport.getCategoryOfTransport().getCatTrId());
        return countTransport > 0;
    }

    public boolean existsOtherTransport(Transport transport){
        int countOtherTransport = transportRepository.countByTrMarkAndTrModelAndCategoryIdWithoutCurrent(transport.getTrMark(), transport.getTrModel(), transport.getCategoryOfTransport().getCatTrId(), transport.getTrId());
        return countOtherTransport > 0;
    }

    public void save(Transport transport){
        transportRepository.save(transport);
    }

    @Transactional
    public void deleteById(Long trId) {
        transportRepository.deleteByTrId(trId);
    }
}
