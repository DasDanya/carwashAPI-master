package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Transport;

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
}
