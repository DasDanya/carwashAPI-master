package ru.pin120.carwashAPI.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.models.CategoryOfSupplies;
import ru.pin120.carwashAPI.repositories.CategoriesOfSuppliesRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис категории расходных материалов
 */
@Service
public class CategoriesOfSuppliesService {

    /**
     * Репозиторий категории расходных материалов
     */
    private final CategoriesOfSuppliesRepository categoriesOfSuppliesRepository;

    /**
     * Внедрений зависимости
     * @param categoriesOfSuppliesRepository репозиторий категории расходных материалов
     */
    public CategoriesOfSuppliesService(CategoriesOfSuppliesRepository categoriesOfSuppliesRepository) {
        this.categoriesOfSuppliesRepository = categoriesOfSuppliesRepository;
    }

    /**
     * Получение списка всех категорий расходных материалов
     * @return Список всех категорий расходных материалов
     */
    public List<CategoryOfSupplies> getAll(){
        return categoriesOfSuppliesRepository.findAllByOrderByCSupNameAsc();
    }

    /**
     * Сохрание категории расходных материалов
     * @param categoryOfSupplies категория расходных материалов
     */
    public void save(CategoryOfSupplies categoryOfSupplies) {
        categoriesOfSuppliesRepository.save(categoryOfSupplies);
    }

    /**
     * Проверяет, существует ли категория расходных материалов с указанным именем
     * @param categoryOfSupplies категория расходных материалов
     * @return true, если существует, иначе false
     */
    public boolean exists(CategoryOfSupplies categoryOfSupplies) {
        return categoriesOfSuppliesRepository.existsBycSupNameIgnoreCase(categoryOfSupplies.getCSupName());
    }

    /**
     * Получение категории расходных материалов по названию
     * @param cSupName название
     * @return Объект Optional с категорией, если она существует
     */
    public Optional<CategoryOfSupplies> getByCSupName(String cSupName) {
        return categoriesOfSuppliesRepository.findById(cSupName);
    }

    /**
     * Удаление категории расходных материалов
     * @param categoryOfSupplies категория расходных материалов
     */
    @Transactional
    public void delete(CategoryOfSupplies categoryOfSupplies) {
        categoriesOfSuppliesRepository.deleteById(categoryOfSupplies.getCSupName());
    }

    /**
     * Поиск расходных материалов по названию
     * @param csupName название
     * @return Список расходных материалов
     */
    public List<CategoryOfSupplies> search(String csupName) {
        return categoriesOfSuppliesRepository.findByCSupNameContainsIgnoreCase(csupName);
    }
}
