package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.CategoryOfSupplies;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий категорий расходных материалов
 */
@Repository
public interface CategoriesOfSuppliesRepository extends CrudRepository<CategoryOfSupplies, String> {


    /**
     * Получение списка всех категорий расходных материалов в алфавитном порядке по наименованию
     *
     * @return Список категорий расходных материалов, отсортированный по наименованию
     */
    @Query("SELECT c FROM CategoryOfSupplies c ORDER BY c.cSupName ASC")
    List<CategoryOfSupplies> findAllByOrderByCSupNameAsc();

    /**
     * Проверка наличия категории расходных материалов с указанным наименованием (игнорируя регистр)
     *
     * @param cSupName наименование категории расходных материалов для поиска
     * @return true, если категория с указанным наименованием существует; false в противном случае
     */
    boolean existsBycSupNameIgnoreCase(String cSupName);

    /**
     * Поиск категорий расходных материалов, наименование которых содержит указанную строку (игнорируя регистр)
     *
     * @param cSupName часть наименования категории расходных материалов для поиска
     * @return список категорий расходных материалов, наименование которых содержит указанную строку, отсортированный по наименованию
     */
    @Query("SELECT c FROM CategoryOfSupplies c WHERE LOWER(c.cSupName) LIKE LOWER(CONCAT('%', :cSupName, '%')) ORDER BY c.cSupName ASC")
    List<CategoryOfSupplies> findByCSupNameContainsIgnoreCase(@Param("cSupName") String cSupName);

}
