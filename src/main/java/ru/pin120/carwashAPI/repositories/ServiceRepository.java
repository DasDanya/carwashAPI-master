package ru.pin120.carwashAPI.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.CategoryOfServices;
import ru.pin120.carwashAPI.models.Service;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends PagingAndSortingRepository<Service, String> {

    void save(Service service);


    List<Service> findByCategory_CatNameOrderByServNameAsc(String catName);

//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE services SET cat_name = :newCatName WHERE cat_name = :pastCatName", nativeQuery = true)
//    int bindServicesWithNewCategory(@Param("pastCatName") String pastCatName, @Param("newCatName") String newCatName);
//
//    @Transactional
//    @Modifying
//    @Query(value = "UPDATE services SET cat_name = :newCatName WHERE serv_name = :servName", nativeQuery = true)
//    int bindServiceWithNewCategory(@Param("servName") String servName,@Param("newCatName") String newCatName);


    List<Service> findByCategory(CategoryOfServices category);

    Optional<Service> findByServName(String servName);

    @Query(value = "Select serv_name FROM services WHERE cat_name = :catName", nativeQuery = true)
    List<String> getAllServicesName(@Param("catName") String catName);

    boolean existsByServNameIgnoreCase(String servName);

    void deleteByServName(String servName);

    @Query("SELECT s FROM Service s JOIN s.category c ORDER BY LOWER(c.catName), LOWER(s.servName) ASC")
    List<Service> findAllSortedByCategoryNameAndServiceName();
}
