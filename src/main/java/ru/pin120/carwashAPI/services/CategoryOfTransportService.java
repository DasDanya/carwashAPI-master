package ru.pin120.carwashAPI.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.models.CategoryOfTransport;
import ru.pin120.carwashAPI.repositories.CategoryOfTransportRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Сервис категорий транспорта
 */
@Service
public class CategoryOfTransportService {

    /**
     * Репозиторий категории транспорта
     */
    private final CategoryOfTransportRepository categoryOfTransportRepository;

    /**
     * Внедрение зависимости
     * @param categoryOfTransportRepository репозиторий категории транспорта
     */
    public CategoryOfTransportService(CategoryOfTransportRepository categoryOfTransportRepository) {
        this.categoryOfTransportRepository = categoryOfTransportRepository;
    }

    /**
     * Получение списка всех категорий транспорта
     * @return Список со всеми категориями транспорта
     */
    public List<CategoryOfTransport> getAll(){
        Sort sort = Sort.by(Sort.Direction.ASC, "catTrName");

        return (List<CategoryOfTransport>) categoryOfTransportRepository.findAll(sort);
    }

    /**
     * Получение списка категорий транспорта, для которых не установлена стоимость и время выполнения определенной услуги
     * @param servName Название услуги
     * @return Список категорий транспорта
     */
    public List<CategoryOfTransport> getCategoriesOfTransportWithoutPriceAndTime(String servName){
        return categoryOfTransportRepository.findCategoriesOfTransportWithoutPriceAndTimeByServName(servName);
    }

    /**
     * Получение списка категорий транспорта, которые не привязаны к конкретному транспорту
     * @param mark марка транспорта
     * @param model модель транспорта
     * @return Список категорий транспорта
     */
    public List<CategoryOfTransport> getAvailableCategoriesByMarkAndModel(String mark, String model){
        return categoryOfTransportRepository.findCategoriesByMarkAndModel(mark,model);
    }

    /**
     * Получение списка категорий транспорта, которые не привязаны к конкретному транспорту, исключая текущий транспорт
     * @param mark марка транспорта
     * @param model модель транспорта
     * @param trId id транспорта
     * @return Список категорий транспорта
     */
    public List<CategoryOfTransport> getAvailableCategoriesByMarkAndModel(String mark, String model, Long trId){
        return categoryOfTransportRepository.findCategoriesByMarkAndModel(mark, model, trId);
    }

    /**
     * Проверяет, существует ли категорий транспорта с заданным названием
     * @param catTrName название
     * @return true, если существует, иначе false
     */
    public boolean existsByCatTrName(String catTrName){
        return categoryOfTransportRepository.existsByCatTrNameIgnoreCase(catTrName);
    }

    /**
     * Получение категории транспорта по id
     * @param catTrId id категории транспорта
     * @return Объект Optional с категорией, если она существует
     */
    public Optional<CategoryOfTransport> getById(Long catTrId){
        return categoryOfTransportRepository.findByCatTrId(catTrId);
    }

    /**
     * Метод сохранения категории транспорта
     * @param categoryOfTransport категория транспорта
     */
    public void save(CategoryOfTransport categoryOfTransport) {
        categoryOfTransportRepository.save(categoryOfTransport);
    }

    /**
     * Удаление категории транспорта по названию
     * @param catTrName название
     */
    @Transactional
    public void delete(String catTrName) {
        categoryOfTransportRepository.deleteByCatTrName(catTrName);
    }

    /**
     * Получение списка категорий транспорта по названию без учёта регистра
     * @param trName Название
     * @return Список категорий транспорта
     */
    public List<CategoryOfTransport> getByTrNameIgnoreCase(String trName) {
        return categoryOfTransportRepository.findByCatTrNameContainsIgnoreCaseOrderByCatTrNameAsc(trName);
    }

    /**
     * Проверяет, существует ли категорий транспорта с заданным названием, исключая текущую категорию
     * @param catTrName название
     * @param catTrId id категории
     * @return true, если существует, иначе false
     */
    public boolean existsCategoryOfTransport(String catTrName, Long catTrId){
        Optional<CategoryOfTransport> categoryOfTransportOptional = categoryOfTransportRepository.findByCatTrNameIgnoreCase(catTrName);
        if(categoryOfTransportOptional.isEmpty()){
            return false;
        }else{
            if(Objects.equals(categoryOfTransportOptional.get().getCatTrId(), catTrId)){
                return false;
            }else{
                return true;
            }
        }
    }
}
