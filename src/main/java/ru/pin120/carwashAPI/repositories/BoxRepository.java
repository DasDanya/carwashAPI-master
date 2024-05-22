package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Box;
import ru.pin120.carwashAPI.models.BoxStatus;

import java.util.List;

@Repository
public interface BoxRepository extends CrudRepository<Box, Long> {

    List<Box> findAllByOrderByBoxId();

    @Query("SELECT b FROM Box b WHERE b.boxStatus <> :status ORDER BY b.boxId ASC")
    List<Box> findAvailable(@Param("status") BoxStatus status);
}
