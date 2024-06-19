package ru.pin120.carwashAPI.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.dtos.AddSuppliesFromBoxDTO;
import ru.pin120.carwashAPI.models.SuppliesInBox;
import ru.pin120.carwashAPI.models.Supply;
import ru.pin120.carwashAPI.repositories.SuppliesInBoxRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис расходных материалов в боксе
 */
@Service
public class SuppliesInBoxService {

    /**
     * Репозиторий расходных материалов в боксе
     */
    private final SuppliesInBoxRepository suppliesInBoxRepository;

    /**
     * Сервис расходных материалов
     */
    private final SupplyService supplyService;
    private final int COUNT_ITEMS_IN_PAGE = 12;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Внедрение зависимостей
     * @param suppliesInBoxRepository репозиторий расходных материалов в боксе
     * @param supplyService сервис расходных материалов
     */
    public SuppliesInBoxService(SuppliesInBoxRepository suppliesInBoxRepository, SupplyService supplyService) {
        this.suppliesInBoxRepository = suppliesInBoxRepository;
        this.supplyService = supplyService;
    }


    /**
     * Получение расходных материалов в боксе по указанных критериям
     * @param boxId id бокса
     * @param pageIndex индекс страницы
     * @param supName название расходного материала
     * @param supCategory название категории расходного материала
     * @param operator оператор сравнения количества
     * @param supCount количество расходного материала
     * @return Список с расходными материалами в боксе
     */
    public List<SuppliesInBox> get(Long boxId, int pageIndex, String supName, String supCategory, String operator, Integer supCount){
        Pageable pageable = PageRequest.of(pageIndex, COUNT_ITEMS_IN_PAGE);
        Map<String, Object> parameters = new HashMap<>();
        String baseQuery = "SELECT s FROM SuppliesInBox s WHERE s.box.boxId = :boxId ";
        parameters.put("boxId", boxId);
        String partQuery = "";
        if(supName != null){
            partQuery = " AND LOWER(s.supply.supName) LIKE LOWER(CONCAT('%', :name, '%'))";
            parameters.put("name", supName);
        }
        if(supCategory != null){
            partQuery += " AND LOWER(s.supply.category.cSupName) LIKE LOWER(CONCAT('%', :category, '%'))";
            parameters.put("category", supCategory);
        }
        if(operator != null && supCount != null){
            partQuery += " AND s.countSupplies "+operator+" :count";
            parameters.put("count", supCount);
        }

        partQuery += " ORDER BY s.supply.category.cSupName, s.supply.supName, s.countSupplies ASC";
        baseQuery += partQuery;

        TypedQuery<SuppliesInBox> query = entityManager.createQuery(baseQuery, SuppliesInBox.class);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }

    /**
     * Добавление расходного материала в бокс
     * @param suppliesInBox расходный материал в боксе
     * @return Добавленный расходный материал в боксе
     */
    @Transactional
    public SuppliesInBox add(SuppliesInBox suppliesInBox) {
        Optional<Supply> supplyOptional = supplyService.getById(suppliesInBox.getSupply().getSupId());
        if(supplyOptional.isEmpty()){
            throw new EntityNotFoundException("Отсутсвует расходный материал, количество которого будет уменьшаться");
        }
        Supply supply = supplyOptional.get();
        int newCountSupply = supply.getSupCount() - suppliesInBox.getCountSupplies();
        if(newCountSupply < 0){
            throw new IllegalArgumentException("Количество добавляемого расходного материала превышает его количество на складе");
        }
        supply.setSupCount(newCountSupply);
        supplyService.save(supply);

        Optional<SuppliesInBox> suppliesInBoxOptional = suppliesInBoxRepository.findByBox_BoxIdAndSupply_SupId(suppliesInBox.getBox().getBoxId(), suppliesInBox.getSupply().getSupId());
        if(suppliesInBoxOptional.isPresent()){
            SuppliesInBox existedSuppliesInBox = suppliesInBoxOptional.get();
            existedSuppliesInBox.setCountSupplies(existedSuppliesInBox.getCountSupplies() + suppliesInBox.getCountSupplies());

            suppliesInBoxRepository.save(existedSuppliesInBox);
            return existedSuppliesInBox;
        }else{
            suppliesInBoxRepository.save(suppliesInBox);
            return suppliesInBox;
        }
    }

    /**
     * Получение расходного материала в боксе по id
     * @param id id расходного материала в боксе
     * @return Объект Optional с расходным материалом в боксе, если он существует
     */
    public Optional<SuppliesInBox> getById(Long id) {
        return suppliesInBoxRepository.findById(id);
    }

    /**
     * Удаление расходного материала в боксе
     * @param suppliesInBox расходный материал в боксе
     */
    @Transactional
    public void delete(SuppliesInBox suppliesInBox) {
        suppliesInBoxRepository.delete(suppliesInBox);
    }

    /**
     * Перевод расходного материала на склад
     * @param addSuppliesFromBoxDTO информация о количестве расходного материала
     * @param existedsuppliesInBox существующий расходный материал в боксе
     */
    @Transactional
    public void transferToWarehouse(AddSuppliesFromBoxDTO addSuppliesFromBoxDTO, SuppliesInBox existedsuppliesInBox) {
        existedsuppliesInBox.setCountSupplies(addSuppliesFromBoxDTO.getSuppliesInBox().getCountSupplies());
        suppliesInBoxRepository.save(existedsuppliesInBox);

        Optional<Supply> supplyOptional = supplyService.getById(addSuppliesFromBoxDTO.getSuppliesInBox().getSupply().getSupId());
        if(supplyOptional.isEmpty()){
            throw new EntityNotFoundException("Отсутсвует расходный материал, переносимый на склад");
        }
        Supply supply = supplyOptional.get();
        supply.setSupCount(supply.getSupCount() + addSuppliesFromBoxDTO.getCountOfAdded());
        supplyService.save(supply);

    }

    /**
     * Проверяет существование другого расходного материала в боксе
     * @param suppliesInBox расходный материал в боксе
     * @return true, если существует, иначе false
     */
    public boolean existsOther(SuppliesInBox suppliesInBox) {
        return suppliesInBoxRepository.countByBoxIdAndSupplyIdExceptCurrent(suppliesInBox.getBox().getBoxId(), suppliesInBox.getSupply().getSupId(), suppliesInBox.getSibId()) > 0;
    }

    /**
     * Изменение данные о расходном материале в боксе
     * @param suppliesInBox новые данные о расходном материале в боксе
     * @param existedsuppliesInBox существующий расходный материал в боксе
     */
    public void edit(SuppliesInBox suppliesInBox, SuppliesInBox existedsuppliesInBox) {
        existedsuppliesInBox.setBox(suppliesInBox.getBox());
        existedsuppliesInBox.setSupply(suppliesInBox.getSupply());
        existedsuppliesInBox.setCountSupplies(suppliesInBox.getCountSupplies());

        suppliesInBoxRepository.save(existedsuppliesInBox);
    }

    /**
     * Получение списка расходных материалов в боксе определенной категории
     * @param boxId id бокса
     * @param categoryName название категории расходных материалов
     * @return Саисок расходных материалов в боксе
     */
    public List<SuppliesInBox> getListExistingSuppliesCertainCategory(Long boxId, String categoryName){
        return suppliesInBoxRepository.findByBox_BoxIdAndSupply_Category_cSupNameAndCountSuppliesGreaterThan(boxId, categoryName, 0);
    }
}
