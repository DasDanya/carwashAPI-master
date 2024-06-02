package ru.pin120.carwashAPI.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.dtos.ServiceWithPriceListDTO;
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

    public List<ServiceWithPriceListDTO> getTransportPriceList(Long catTrId){
        List<ServiceWithPriceListDTO> serviceWithPriceListDTOS = new ArrayList<>();
        List<PriceList> priceLists = priceListRepository.findByCategoryOfTransportCatTrId(catTrId);

        for(PriceList priceListPosition: priceLists){
            ServiceWithPriceListDTO serviceWithPriceListDTO = new ServiceWithPriceListDTO(priceListPosition.getService().getCategory().getCatName(), priceListPosition.getService().getServName(), priceListPosition.getPlPrice(), priceListPosition.getPlTime());
            serviceWithPriceListDTOS.add(serviceWithPriceListDTO);
        }

        return serviceWithPriceListDTOS;
    }

    public boolean existPriceListPosition(PriceList priceListPosition){
        return priceListRepository.findByCategoryOfTransportCatTrIdAndServiceServName(priceListPosition.getCategoryOfTransport().getCatTrId(), priceListPosition.getService().getServName()).isPresent();
    }

    public boolean existPriceListPosition(Long catTrId, String servName){
        return priceListRepository.findByCategoryOfTransportCatTrIdAndServiceServName(catTrId, servName).isPresent();
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

    public List<PriceList> filter(String servName,String catTrName, String priceOperator, Integer price, String timeOperator, Integer time) {
        if(catTrName != null && priceOperator != null && price != null && timeOperator != null && time != null){
            if(priceOperator.equals("<") && timeOperator.equals(">")){
                return priceListRepository.query1(servName, catTrName, time, price);
            }else if(priceOperator.equals("<") && timeOperator.equals("=")){
                return priceListRepository.query3(servName,catTrName, time, price);
            }else if(priceOperator.equals("<") && timeOperator.equals("<")){
                return priceListRepository.query5(servName,catTrName, time, price);
            }else if(priceOperator.equals("=") && timeOperator.equals(">")){
                return priceListRepository.query7(servName,catTrName, time, price);
            }else if(priceOperator.equals("=") && timeOperator.equals("=")){
                return priceListRepository.query9(servName,catTrName, time, price);
            }else if(priceOperator.equals("=") && timeOperator.equals("<")){
                return priceListRepository.query11(servName,catTrName, time, price);
            }else if(priceOperator.equals(">") && timeOperator.equals("<")){
                return priceListRepository.query13(servName,catTrName, time, price);
            }else if(priceOperator.equals(">") && timeOperator.equals("=")){
                return priceListRepository.query15(servName,catTrName, time, price);
            }else if(priceOperator.equals(">") && timeOperator.equals(">")){
                return priceListRepository.query17(servName,catTrName, time, price);
            }
        }else if(priceOperator != null && price != null && timeOperator != null && time != null){
            if(priceOperator.equals("<") && timeOperator.equals(">")){
                return priceListRepository.query2(servName,time, price);
            }else if(priceOperator.equals("<") && timeOperator.equals("=")){
                return priceListRepository.query4(servName,time, price);
            }else if(priceOperator.equals("<") && timeOperator.equals("<")){
                return priceListRepository.query6(servName,time, price);
            }else if(priceOperator.equals("=") && timeOperator.equals(">")){
                return priceListRepository.query8(servName,time, price);
            }else if(priceOperator.equals("=") && timeOperator.equals("=")){
                return priceListRepository.query10(servName,time, price);
            }else if(priceOperator.equals("=") && timeOperator.equals("<")){
                return priceListRepository.query12(servName,time, price);
            }else if(priceOperator.equals(">") && timeOperator.equals("<")){
                return priceListRepository.query14(servName,time, price);
            }else if(priceOperator.equals(">") && timeOperator.equals("=")){
                return priceListRepository.query16(servName,time, price);
            }else if(priceOperator.equals(">") && timeOperator.equals(">")){
                return priceListRepository.query18(servName,time, price);
            }
        }else if(catTrName != null && priceOperator != null && price != null){
            switch (priceOperator) {
                case "<" -> {
                    return priceListRepository.query19(servName,catTrName, price);
                }
                case "=" -> {
                    return priceListRepository.query21(servName,catTrName, price);
                }
                case ">" -> {
                    return priceListRepository.query23(servName,catTrName, price);
                }
            }
        }else if(catTrName != null && timeOperator != null && time != null){
            switch (timeOperator) {
                case "<" -> {
                    return priceListRepository.query25(servName,catTrName, time);
                }
                case "=" -> {
                    return priceListRepository.query27(servName,catTrName, time);
                }
                case ">" -> {
                    return priceListRepository.query29(servName,catTrName, time);
                }
            }
        }else if(timeOperator != null && time != null){
            switch (timeOperator) {
                case "<" -> {
                    return priceListRepository.query26(servName,time);
                }
                case "=" -> {
                    return priceListRepository.query28(servName,time);
                }
                case ">" -> {
                    return priceListRepository.query30(servName, time);
                }
            }
        }else if(priceOperator != null && price != null){
            switch (priceOperator) {
                case "<" -> {
                    return priceListRepository.query20(servName,price);
                }
                case "=" -> {
                    return priceListRepository.query22(servName,price);
                }
                case ">" -> {
                    return priceListRepository.query24(servName,price);
                }
            }
        }else if(catTrName != null){
            return priceListRepository.query31(servName,catTrName);
        }

        return new ArrayList<>();
    }
}
