package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.PriceList;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий позиции прайс-листа
 */
@Repository
public interface PriceListRepository extends PagingAndSortingRepository<PriceList, Long> {

    /**
     * Поиск позиций прайс-листа по названию услуги
     *
     * @param servName имя услуги для поиска
     * @return список позиций прайс-листа, удовлетворяющих критериям поиска, отсортированный по категории транспорта
     */
    @Query("SELECT p FROM PriceList p WHERE p.service.servName = :servName ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> findByServiceName(@Param("servName") String servName);



    /**
     * Поиск позиций прайс-листа по id категории транспорта
     *
     * @param catTrId id категории транспорта для поиска
     * @return список позиций прайс-листа, удовлетворяющих критериям поиска, отсортированный по названию категории и названию услуги
     */
    @Query("SELECT p FROM PriceList p WHERE p.categoryOfTransport.catTrId = :catTrId ORDER BY p.service.category.catName, p.service.servName ASC")
    List<PriceList> findByCategoryOfTransportCatTrId(Long catTrId);

    /**
     * Поиск позиций прайс-листа по id категории транспорта и названию услуги
     *
     * @param catTrId  id категории транспорта для поиска
     * @param servName название услуги для поиска
     * @return объект Optional, содержащий найденную запись в прайс-листе или пустой, если запись не найдена
     */
    Optional<PriceList> findByCategoryOfTransportCatTrIdAndServiceServName(Long catTrId, String servName);

    /**
     * Поиск позиции в прайс-листе по id
     *
     * @param plId id записи в прайс-листе для поиска
     * @return объект Optional, содержащий найденную позицию в прайс-листе или пустой, если позиция не найдена
     */
    Optional<PriceList> findByPlId(Long plId);

    /**
     * Сохранение позиции в прайс-лист
     *
     * @param priceListPosition позиция прайс-листа
     */
    void save(PriceList priceListPosition);

    /**
     * Удаление позиции из прайс-листа
     *
     * @param priceList позиция прайс-листа для удаления
     */
    void delete(PriceList priceList);


    // поиск
    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime > :time " +
            "AND p.plPrice < :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query1(@Param("servName") String servName, @Param("catTrName") String catTrName, @Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime > :time " +
            "AND p.plPrice < :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query2(@Param("servName")String servName,@Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime = :time " +
            "AND p.plPrice < :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query3(@Param("servName") String servName,@Param("catTrName") String catTrName, @Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime = :time " +
            "AND p.plPrice < :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query4(@Param("servName")String servName,@Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime < :time " +
            "AND p.plPrice < :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query5(@Param("servName") String servName,@Param("catTrName") String catTrName, @Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime < :time " +
            "AND p.plPrice < :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query6(@Param("servName") String servName,@Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime > :time " +
            "AND p.plPrice = :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query7(@Param("servName") String servName,@Param("catTrName") String catTrName, @Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime > :time " +
            "AND p.plPrice = :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query8(@Param("servName")String servName,@Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime = :time " +
            "AND p.plPrice = :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query9(@Param("servName") String servName,@Param("catTrName") String catTrName, @Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime = :time " +
            "AND p.plPrice = :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query10(@Param("servName") String servName,@Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime < :time " +
            "AND p.plPrice = :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query11(@Param("servName")String servName,@Param("catTrName") String catTrName, @Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime < :time " +
            "AND p.plPrice = :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query12(@Param("servName") String servName,@Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime < :time " +
            "AND p.plPrice > :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query13(@Param("servName") String servName,@Param("catTrName") String catTrName, @Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime < :time " +
            "AND p.plPrice > :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query14(@Param("servName")String servName,@Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime = :time " +
            "AND p.plPrice > :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query15(@Param("servName")String servName,@Param("catTrName") String catTrName, @Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime = :time " +
            "AND p.plPrice > :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query16(@Param("servName") String servName,@Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime > :time " +
            "AND p.plPrice > :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query17(@Param("servName") String servName,@Param("catTrName") String catTrName, @Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime > :time " +
            "AND p.plPrice > :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query18(@Param("servName") String servName,@Param("time") Integer time, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plPrice < :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query19(@Param("servName")String servName,@Param("catTrName") String catTrName, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plPrice < :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query20(@Param("servName") String servName,@Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plPrice = :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query21(@Param("servName") String servName,@Param("catTrName") String catTrName, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plPrice = :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query22(@Param("servName")String servName,@Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plPrice > :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query23(@Param("servName")String servName,@Param("catTrName") String catTrName, @Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plPrice > :price " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query24(@Param("servName") String servName,@Param("price") Integer price);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime < :time " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query25(@Param("servName") String servName,@Param("catTrName") String catTrName, @Param("time") Integer time);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime < :time " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query26(@Param("servName") String servName, @Param("time") Integer time);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime = :time " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query27(@Param("servName") String servName,@Param("catTrName") String catTrName, @Param("time") Integer time);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime = :time " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query28(@Param("servName") String servName, @Param("time") Integer time);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            "AND p.plTime > :time " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query29(@Param("servName") String servName,@Param("catTrName") String catTrName, @Param("time") Integer time);

    @Query("SELECT p FROM PriceList p WHERE " +
            "p.plTime > :time " +
            "AND p.service.servName = :servName " +
            "ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query30(@Param("servName") String servName, @Param("time") Integer time);

    @Query("SELECT p FROM PriceList p WHERE LOWER(p.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :catTrName, '%')) " +
            " AND p.service.servName = :servName ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> query31(@Param("servName") String servName, @Param("catTrName") String catTrName);
}
