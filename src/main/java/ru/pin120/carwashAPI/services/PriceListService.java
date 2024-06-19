package ru.pin120.carwashAPI.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.dtos.ServiceWithPriceListDTO;
import ru.pin120.carwashAPI.models.PriceList;
import ru.pin120.carwashAPI.repositories.PriceListRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для позиции прайс-листа
 */
@Service
public class PriceListService {

    /**
     * Репозиторий позиции прайс-листа
     */
    private final PriceListRepository priceListRepository;

    /**
     * Внедрение зависимости
     * @param priceListRepository репозиторий позиции прайс-листа
     */
    public PriceListService(PriceListRepository priceListRepository) {
        this.priceListRepository = priceListRepository;
    }

    /**
     * Получение списка позиций определенной услуги
     * @param servName название услуги
     * @return Список позиций
     */
    public List<PriceList> getByServName(String servName){
        return priceListRepository.findByServiceName(servName);

    }

    /**
     * Получение списка услуг вместе со стоимостью и временем выполнения определенной категории транспорта
     * @param catTrId id категории транспорта
     * @return Список услуг вместе со стоимостью и временем выполнения
     */
    public List<ServiceWithPriceListDTO> getTransportPriceList(Long catTrId){
        List<ServiceWithPriceListDTO> serviceWithPriceListDTOS = new ArrayList<>();
        List<PriceList> priceLists = priceListRepository.findByCategoryOfTransportCatTrId(catTrId);

        for(PriceList priceListPosition: priceLists){
            ServiceWithPriceListDTO serviceWithPriceListDTO = new ServiceWithPriceListDTO(priceListPosition.getService().getCategory().getCatName(), priceListPosition.getService().getServName(), priceListPosition.getPlPrice(), priceListPosition.getPlTime());
            serviceWithPriceListDTOS.add(serviceWithPriceListDTO);
        }

        return serviceWithPriceListDTOS;
    }

    /**
     * Проверяет существование позиции
     * @param priceListPosition позиция в прайс-листе
     * @return true, если существует, иначе false
     */
    public boolean existPriceListPosition(PriceList priceListPosition){
        return priceListRepository.findByCategoryOfTransportCatTrIdAndServiceServName(priceListPosition.getCategoryOfTransport().getCatTrId(), priceListPosition.getService().getServName()).isPresent();
    }

    /**
     * Проверяет существование позиции
     * @param catTrId id категории транспорта
     * @param servName название услуги
     * @return true, если существует, иначе false
     */
    public boolean existPriceListPosition(Long catTrId, String servName){
        return priceListRepository.findByCategoryOfTransportCatTrIdAndServiceServName(catTrId, servName).isPresent();
    }


    /**
     * Получение списка всех позиций прайс-листа
     * @return Список со всеми позициями прайс-листа
     */
    public List<PriceList> getAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "plPrice");
        return (List<PriceList>) priceListRepository.findAll(sort);
    }

    /**
     * Сохранение позиции
     * @param priceListPosition позиция
     */
    public void save(PriceList priceListPosition) {
        priceListRepository.save(priceListPosition);
    }

    /**
     * Получение позиции по id
     * @param plId id позиции
     * @return Объект Optional с позицией, если он существует
     */
    public Optional<PriceList> getById(Long plId) {
        return priceListRepository.findByPlId(plId);
    }

    /**
     * Удаление позиции
     * @param priceList позиция
     */
    @Transactional
    public void delete(PriceList priceList) {
        priceListRepository.delete(priceList);
    }

    /**
     * Поиск позиций
     * @param servName название услуги
     * @param catTrName название категории транспорта
     * @param priceOperator оператор сравнения стоимости
     * @param price стоимость
     * @param timeOperator оператор сравнения времени выполнения
     * @param time время выполнения
     * @return Список найденных позиций
     */
    public List<PriceList> search(String servName, String catTrName, String priceOperator, Integer price, String timeOperator, Integer time) {
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
