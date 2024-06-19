package ru.pin120.carwashAPI.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.CategoryOfServices;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий категорий услуг
 */
@Repository
public interface CategoryOfServicesRepository extends PagingAndSortingRepository<CategoryOfServices, String> {

    /**
     * Получение списка категорий услуг с их названиями и названиями связанных с ними услуг
     *
     * @return список массивов строк, содержащих названия категорий и услуг, отсортированных по названию категории и названию услуги
     */
    @Query("SELECT c.catName, s.servName FROM CategoryOfServices c LEFT JOIN c.services s ORDER BY c.catName ASC, s.servName ASC")
    List<String[]> findCategoriesWithServices();

    /**
     * Проверка наличия категории услуг с указанным названием (игнорируя регистр)
     *
     * @param catName название категории услуг для поиска
     * @return true, если категория услуг с указанным названием существует; false в противном случае
     */
    boolean existsByCatNameIgnoreCase(String catName);


    /**
     * Сохранение категории услуг в базе данных
     *
     * @param categoryOfServices категория услуг для сохранения
     */
    void save(CategoryOfServices categoryOfServices);

    /**
     * Поиск категории услуг по названию
     *
     * @param catName название категории услуг для поиска
     * @return объект Optional, содержащий найденную категорию услуг или пустой, если категория не найдена
     */
    Optional<CategoryOfServices> findByCatName(String catName);

    /**
     * Получение списка всех названий категорий услуг, отсортированных по названию в алфавитном порядке
     *
     * @return список названий категорий услуг, отсортированный по названию в алфавитном порядке
     */
    @Query("SELECT c.catName FROM CategoryOfServices c ORDER BY c.catName ASC")
    List<String> findAllCatNamesSortedAsc();

    /**
     * Поиск названий категорий услуг, содержащих указанную подстроку в названии (игнорируя регистр)
     *
     * @param parameter подстрока для поиска в названиях категорий услуг
     * @return список названий категорий услуг, содержащих указанную подстроку, отсортированный по названию в алфавитном порядке
     */
    @Query("SELECT c.catName FROM CategoryOfServices c WHERE LOWER(c.catName) LIKE LOWER(concat('%', :searchParameter, '%')) ORDER BY c.catName ASC")
    List<String> findCatNamesByParameterAsc(@Param("searchParameter") String parameter);

    /**
     * Удаление категории услуг по названию
     *
     * @param catName название категории услуг для удаления
     */
    void deleteByCatName(String catName);

}
