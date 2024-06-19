package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.CategoryOfTransport;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс категорий транспорта
 */
@Repository
public interface CategoryOfTransportRepository extends PagingAndSortingRepository<CategoryOfTransport, Long> {

    /**
     * Проверка наличия категории транспорта с указанным названием (игнорируя регистр)
     *
     * @param catTrName название категории транспорта для поиска
     * @return true, если категория транспорта с указанным названием существует; false в противном случае
     */
    boolean existsByCatTrNameIgnoreCase(String catTrName);

    /**
     * Сохранение категории транспорта в базе данных
     *
     * @param categoryOfTransport категория транспорта для сохранения
     */
    void save(CategoryOfTransport categoryOfTransport);

    /**
     * Поиск категории транспорта по названию (игнорируя регистр)
     *
     * @param catTrName название категории транспорта для поиска
     * @return объект Optional, содержащий найденную категорию транспорта или пустой, если категория не найдена
     */
    Optional<CategoryOfTransport> findByCatTrNameIgnoreCase(String catTrName);

    /**
     * Поиск категории транспорта по id
     *
     * @param catTrId id категории транспорта для поиска
     * @return объект Optional, содержащий найденную категорию транспорта или пустой, если категория не найдена
     */
    Optional<CategoryOfTransport> findByCatTrId(Long catTrId);

    /**
     * Удаление категории транспорта по названию
     *
     * @param catTrName название категории транспорта для удаления
     */
    void deleteByCatTrName(String catTrName);

    /**
     * Поиск категорий транспорта, содержащих указанную подстроку в названии (игнорируя регистр), отсортированных по названию в алфавитном порядке
     *
     * @param catTrName подстрока для поиска в названиях категорий транспорта
     * @return список категорий транспорта, содержащих указанную подстроку, отсортированный по названию в алфавитном порядке
     */
    List<CategoryOfTransport> findByCatTrNameContainsIgnoreCaseOrderByCatTrNameAsc(String catTrName);

    /**
     * Поиск категорий транспорта, которые не связаны с определенной услугой
     *
     * @param servName название услуги для поиска категорий транспорта
     * @return список категорий транспорта, не связанных с определенной услугой, отсортированный по названию в алфавитном порядке
     */
    @Query("SELECT ct FROM CategoryOfTransport ct WHERE ct.catTrId NOT IN (SELECT p.categoryOfTransport.catTrId FROM PriceList p WHERE p.service.servName = :servName) ORDER BY ct.catTrName ASC")
    List<CategoryOfTransport> findCategoriesOfTransportWithoutPriceAndTimeByServName(@Param("servName") String servName);

    /**
     * Поиск категорий транспорта, которые не связаны с определенной маркой и моделью транспорта
     *
     * @param mark  марка транспорта
     * @param model модель транспорта
     * @return список категорий транспорта, не связанных с указанной маркой и моделью транспорта, отсортированный по названию в алфавитном порядке
     */
    @Query("SELECT c FROM CategoryOfTransport c WHERE c.catTrId NOT IN " +
            "(SELECT t.categoryOfTransport.catTrId FROM Transport t " +
            "WHERE t.trMark = :mark AND t.trModel = :model) ORDER BY c.catTrName ASC")
    List<CategoryOfTransport> findCategoriesByMarkAndModel(@Param("mark") String mark, @Param("model") String model);


    /**
     * Поиск категорий транспорта, которые не связаны с определенной маркой и моделью транспорта, исключая указанный транспорт
     *
     * @param mark марка транспорта
     * @param model модель транспорта
     * @param excludedId id транспорта, который нужно исключить из результата
     * @return список категорий транспорта, не связанных с указанной маркой и моделью транспорта, исключая указанный транспорт, отсортированный по названию в алфавитном порядке
     */
    @Query("SELECT c FROM CategoryOfTransport c WHERE NOT EXISTS " +
            "(SELECT t FROM Transport t " +
            "WHERE t.categoryOfTransport.catTrId = c.catTrId AND t.trMark = :mark AND t.trModel = :model AND t.trId <> :excludedId) ORDER BY c.catTrName ASC")
    List<CategoryOfTransport> findCategoriesByMarkAndModel(@Param("mark") String mark, @Param("model") String model, @Param("excludedId") Long excludedId);
}
