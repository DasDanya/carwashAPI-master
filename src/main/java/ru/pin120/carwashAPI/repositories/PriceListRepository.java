package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.CategoryOfTransport;
import ru.pin120.carwashAPI.models.PriceList;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceListRepository extends PagingAndSortingRepository<PriceList, Long> {

    @Query("SELECT p FROM PriceList p WHERE p.service.servName = :servName ORDER BY p.categoryOfTransport.catTrName ASC")
    List<PriceList> findByServiceName(@Param("servName") String servName);

    Optional<PriceList> findByCategoryOfTransportCatTrIdAndServiceServName(Long catTrId, String servName);

    Optional<PriceList> findByPlId(Long plId);

    void save(PriceList priceListPosition);

    void delete(PriceList priceList);
}
