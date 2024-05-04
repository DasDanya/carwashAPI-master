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

@Repository
public interface CategoryOfServicesRepository extends PagingAndSortingRepository<CategoryOfServices, String> {
    @Query("SELECT c.catName, s.servName FROM CategoryOfServices c LEFT JOIN c.services s ORDER BY c.catName ASC, s.servName ASC")
    List<String[]> findCategoriesWithServices();

    boolean existsByCatNameIgnoreCase(String catName);

    void save(CategoryOfServices categoryOfServices);

    Optional<CategoryOfServices> findByCatName(String catName);

    @Query("SELECT c.catName FROM CategoryOfServices c ORDER BY c.catName ASC")
    List<String> findAllCatNamesSortedAsc();

    @Query("SELECT c.catName FROM CategoryOfServices c WHERE LOWER(c.catName) LIKE LOWER(concat('%', :searchParameter, '%')) ORDER BY c.catName ASC")
    List<String> findCatNamesByParameterAsc(@Param("searchParameter") String parameter);
    void deleteByCatName(String catName);

}
