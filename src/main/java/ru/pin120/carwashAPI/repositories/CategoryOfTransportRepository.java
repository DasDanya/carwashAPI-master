package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.CategoryOfTransport;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryOfTransportRepository extends PagingAndSortingRepository<CategoryOfTransport, Long> {

    boolean existsByCatTrNameIgnoreCase(String catTrName);

    void save(CategoryOfTransport categoryOfTransport);

    Optional<CategoryOfTransport> findByCatTrNameIgnoreCase(String catTrName);

    Optional<CategoryOfTransport> findByCatTrId(Long catTrId);

    void deleteByCatTrName(String catTrName);

    List<CategoryOfTransport> findByCatTrNameContainsIgnoreCaseOrderByCatTrNameAsc(String catTrName);

    @Query("SELECT ct FROM CategoryOfTransport ct WHERE ct.catTrId NOT IN (SELECT p.categoryOfTransport.catTrId FROM PriceList p WHERE p.service.servName = :servName) ORDER BY ct.catTrName ASC")
    List<CategoryOfTransport> findCategoriesOfTransportWithoutPriceAndTimeByServName(@Param("servName") String servName);

    @Query("SELECT c FROM CategoryOfTransport c WHERE c.catTrId NOT IN " +
            "(SELECT t.categoryOfTransport.catTrId FROM Transport t " +
            "WHERE t.trMark = :mark AND t.trModel = :model) ORDER BY c.catTrName ASC")
    List<CategoryOfTransport> findCategoriesByMarkAndModel(@Param("mark") String mark, @Param("model") String model);

//    @Query("SELECT c FROM CategoryOfTransport c WHERE c.catTrId NOT IN " +
//            "(SELECT t.categoryOfTransport.catTrId FROM Transport t " +
//            "WHERE t.trMark = :mark AND t.trModel = :model AND t.trId != :excludeId) ORDER BY c.catTrName ASC")
//    List<CategoryOfTransport> findCategoriesByMarkAndModel(@Param("mark") String mark, @Param("model") String model, @Param("excludeId") Long excludeId);

    @Query("SELECT c FROM CategoryOfTransport c WHERE NOT EXISTS " +
            "(SELECT t FROM Transport t " +
            "WHERE t.categoryOfTransport.catTrId = c.catTrId AND t.trMark = :mark AND t.trModel = :model AND t.trId <> :excludedId) ORDER BY c.catTrName ASC")
    List<CategoryOfTransport> findCategoriesByMarkAndModel(@Param("mark") String mark, @Param("model") String model, @Param("excludedId") Long excludedId);
}
