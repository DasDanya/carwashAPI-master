package ru.pin120.carwashAPI.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Transport;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportRepository extends PagingAndSortingRepository<Transport, Long> {

    void save(Transport transport);

    @Query("SELECT COUNT(t) FROM Transport t WHERE t.trMark = :trMark AND t.trModel = :trModel AND t.categoryOfTransport.catTrId = :categoryId")
    int countByTrMarkAndTrModelAndCategoryId(@Param("trMark") String trMark,@Param("trModel") String trModel, @Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(t) FROM Transport t WHERE t.trMark = :trMark AND t.trModel = :trModel AND t.categoryOfTransport.catTrId = :categoryId AND t.trId <> :trId")
    int countByTrMarkAndTrModelAndCategoryIdWithoutCurrent(@Param("trMark") String trMark,@Param("trModel") String trModel, @Param("categoryId") Long categoryId, @Param("trId") Long trId);

    Optional<Transport> findByTrId(Long trId);

    void deleteByTrId(Long trId);


    //Поиск
    @Query("SELECT t FROM Transport t WHERE LOWER(t.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(t.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(t.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Transport> findByMarkAndModelAndCategory(@Param("mark") String mark, @Param("model") String model, @Param("category") String category, Pageable pageable);
    @Query("SELECT t FROM Transport t WHERE LOWER(t.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(t.trModel) LIKE LOWER(CONCAT('%', :model, '%'))")
    List<Transport> findByMarkAndModel(@Param("mark") String mark, @Param("model") String model, Pageable pageable);

    @Query("SELECT t FROM Transport t WHERE LOWER(t.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(t.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Transport> findByMarkAndCategory(@Param("mark") String mark, @Param("category") String category, Pageable pageable);

    @Query("SELECT t FROM Transport t WHERE LOWER(t.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(t.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Transport> findByModelAndCategory(@Param("model") String model, @Param("category") String category, Pageable pageable);

    @Query("SELECT t FROM Transport t WHERE LOWER(t.trModel) LIKE LOWER(CONCAT('%', :model, '%'))")
    List<Transport> findByModel(@Param("model") String model, Pageable pageable);

    @Query("SELECT t FROM Transport t WHERE LOWER(t.trMark) LIKE LOWER(CONCAT('%', :mark, '%'))")
    List<Transport> findByMark(@Param("mark") String mark, Pageable pageable);

    @Query("SELECT t FROM Transport t WHERE LOWER(t.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Transport> findByCategory(@Param("category") String category, Pageable pageable);
}
