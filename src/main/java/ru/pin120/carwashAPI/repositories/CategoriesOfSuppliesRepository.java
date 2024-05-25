package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.CategoryOfSupplies;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriesOfSuppliesRepository extends CrudRepository<CategoryOfSupplies, String> {
    @Query("SELECT c FROM CategoryOfSupplies c ORDER BY c.cSupName ASC")
    List<CategoryOfSupplies> findAllByOrderByCSupNameAsc();

    boolean existsBycSupNameIgnoreCase(String cSupName);

    @Query("SELECT c FROM CategoryOfSupplies c WHERE LOWER(c.cSupName) LIKE LOWER(CONCAT('%', :cSupName, '%')) ORDER BY c.cSupName ASC")
    List<CategoryOfSupplies> findByCSupNameContainsIgnoreCase(@Param("cSupName") String cSupName);

}
