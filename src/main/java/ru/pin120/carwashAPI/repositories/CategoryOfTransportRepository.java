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

    List<CategoryOfTransport> findByCatTrNameContainsIgnoreCase(String catTrName);

    @Query(value = "UPDATE categories_of_cars SET cat_cars_name = :newCatName WHERE cat_cars_name = :pastCatName",nativeQuery = true)
    int edit(@Param("pastCatName") String pastCatName, @Param("newCatName") String newCatName);
}
