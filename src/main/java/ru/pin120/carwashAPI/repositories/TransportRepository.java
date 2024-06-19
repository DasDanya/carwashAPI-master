package ru.pin120.carwashAPI.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Transport;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий транспорта
 */
@Repository
public interface TransportRepository extends PagingAndSortingRepository<Transport, Long> {

    /**
     * Сохранение транспорта
     *
     * @param transport транспорт
     */
    void save(Transport transport);

    /**
     * Подсчет количества транспорта по марке, модели и категории
     *
     * @param trMark марка
     * @param trModel модель
     * @param categoryId id категории
     * @return количество транспорта, удовлетворяющих критериям поиска
     */
    @Query("SELECT COUNT(t) FROM Transport t WHERE t.trMark = :trMark AND t.trModel = :trModel AND t.categoryOfTransport.catTrId = :categoryId")
    int countByTrMarkAndTrModelAndCategoryId(@Param("trMark") String trMark,@Param("trModel") String trModel, @Param("categoryId") Long categoryId);

    /**
     * Подсчет количества транспорта по марке, модели и категории, исключая текущий транспорт
     * @param trMark марка
     * @param trModel модель
     * @param categoryId id категории
     * @param trId id текущего транспорта
     * @return количество транспорта, удовлетворяющих критериям поиска
     */
    @Query("SELECT COUNT(t) FROM Transport t WHERE t.trMark = :trMark AND t.trModel = :trModel AND t.categoryOfTransport.catTrId = :categoryId AND t.trId <> :trId")
    int countByTrMarkAndTrModelAndCategoryIdWithoutCurrent(@Param("trMark") String trMark,@Param("trModel") String trModel, @Param("categoryId") Long categoryId, @Param("trId") Long trId);

    /**
     * Поиск транспортного средства по id
     * @param trId id транспортного средства
     * @return объект Optional, содержащий найденную транспортное средство или пустой, если транспортное средство не найдено
     */
    Optional<Transport> findByTrId(Long trId);

    /**
     * Удаление транспортного средства по id
     * @param trId id транспортного средства
     */
    void deleteByTrId(Long trId);


    /**
     * Поиск транспортных средств по марке, модели и категории
     *
     * @param mark марка транспортного средства для поиска
     * @param model модель транспортного средства для поиска
     * @param category категория транспортного средства для поиска
     * @param pageable объект, предоставляющий информацию о странице результатов и сортировке
     * @return список транспортных средств, удовлетворяющих критериям поиска
     */
    @Query("SELECT t FROM Transport t WHERE LOWER(t.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(t.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(t.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Transport> findByMarkAndModelAndCategory(@Param("mark") String mark,
                                                  @Param("model") String model,
                                                  @Param("category") String category,
                                                  Pageable pageable);

    /**
     * Поиск транспортных средств по марке и модели
     *
     * @param mark марка транспортного средства для поиска
     * @param model модель транспортного средства для поиска
     * @param pageable объект, предоставляющий информацию о странице результатов и сортировке
     * @return список транспортных средств, удовлетворяющих критериям поиска
     */
    @Query("SELECT t FROM Transport t WHERE LOWER(t.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(t.trModel) LIKE LOWER(CONCAT('%', :model, '%'))")
    List<Transport> findByMarkAndModel(@Param("mark") String mark, @Param("model") String model, Pageable pageable);

    /**
     * Поиск транспортных средств по марке и категории.
     *
     * @param mark марка транспортного средства для поиска
     * @param category категория транспортного средства для поиска
     * @param pageable объект, предоставляющий информацию о странице результатов и сортировке
     * @return список транспортных средств, удовлетворяющих критериям поиска
     */
    @Query("SELECT t FROM Transport t WHERE LOWER(t.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(t.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Transport> findByMarkAndCategory(@Param("mark") String mark, @Param("category") String category, Pageable pageable);

    /**
     * Поиск транспортных средств по модели и категории.
     *
     * @param model модель транспортного средства для поиска
     * @param category категория транспортного средства для поиска
     * @param pageable объект, предоставляющий информацию о странице результатов и сортировке
     * @return список транспортных средств, удовлетворяющих критериям поиска
     */
    @Query("SELECT t FROM Transport t WHERE LOWER(t.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(t.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Transport> findByModelAndCategory(@Param("model") String model, @Param("category") String category, Pageable pageable);


    /**
     * Поиск транспортных средств по модели
     *
     * @param model модель транспортного средства для поиска
     * @param pageable объект, предоставляющий информацию о странице результатов и сортировке
     * @return список транспортных средств, удовлетворяющих критериям поиска
     */
    @Query("SELECT t FROM Transport t WHERE LOWER(t.trModel) LIKE LOWER(CONCAT('%', :model, '%'))")
    List<Transport> findByModel(@Param("model") String model, Pageable pageable);


    /**
     * Поиск транспортных средств по марке
     *
     * @param mark марка транспортного средства для поиска
     * @param pageable объект, предоставляющий информацию о странице результатов и сортировке
     * @return список транспортных средств, удовлетворяющих критериям поиска
     */
    @Query("SELECT t FROM Transport t WHERE LOWER(t.trMark) LIKE LOWER(CONCAT('%', :mark, '%'))")
    List<Transport> findByMark(@Param("mark") String mark, Pageable pageable);


    /**
     * Поиск транспортных средств по категории
     *
     * @param category категория транспортного средства для поиска
     * @param pageable объект, предоставляющий информацию о странице результатов и сортировке
     * @return список транспортных средств, удовлетворяющих критериям поиска
     */
    @Query("SELECT t FROM Transport t WHERE LOWER(t.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Transport> findByCategory(@Param("category") String category, Pageable pageable);
}
