package ru.pin120.carwashAPI.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.dtos.PriceListDTO;
import ru.pin120.carwashAPI.models.PriceList;
import ru.pin120.carwashAPI.repositories.PriceListRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PriceListService {

    private final PriceListRepository priceListRepository;


    public PriceListService(PriceListRepository priceListRepository) {
        this.priceListRepository = priceListRepository;
    }

    public List<PriceList> getByServName(String servName){
        return priceListRepository.findByServiceName(servName);

    }

//    public List<PriceListDTO> getByServName(String servName){
//        List<PriceList> priceLists = priceListRepository.findByServiceName(servName);
//        List<PriceListDTO> priceListDTOS = new ArrayList<>();
//        for(PriceList priceList: priceLists){
//            PriceListDTO priceListDTO = new PriceListDTO(priceList.getPlId(), priceList.getService().getServName(), priceList.getCategoryOfTransport().getCatTrName(), priceList.getPlTime(), priceList.getPlPrice());
//            priceListDTOS.
//        }
//    }

    public boolean existPriceListPosition(PriceList priceListPosition){
        return priceListRepository.findByCategoryOfTransportCatTrIdAndServiceServName(priceListPosition.getCategoryOfTransport().getCatTrId(), priceListPosition.getService().getServName()).isPresent();
    }

    public boolean existsOtherPriceListPosition(PriceList priceListPosition){
        Optional<PriceList> priceListOptional = priceListRepository.findByCategoryOfTransportCatTrIdAndServiceServName(priceListPosition.getCategoryOfTransport().getCatTrId(), priceListPosition.getService().getServName());
        if(priceListOptional.isEmpty()){
            return false;
        }else{
            if(Objects.equals(priceListOptional.get().getPlId(), priceListPosition.getPlId())){
                return false;
            }else{
                return true;
            }
        }
    }

    public List<PriceList> getAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "plPrice");
        return (List<PriceList>) priceListRepository.findAll(sort);
    }

    public void save(PriceList priceListPosition) {
        priceListRepository.save(priceListPosition);
    }

    public Optional<PriceList> getById(Long plId) {
        return priceListRepository.findByPlId(plId);
    }


    @Transactional
    public void delete(PriceList priceList) {
        priceListRepository.delete(priceList);
    }
}
