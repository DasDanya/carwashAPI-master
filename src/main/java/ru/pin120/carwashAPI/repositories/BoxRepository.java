package ru.pin120.carwashAPI.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Box;

import java.util.List;

@Repository
public interface BoxRepository extends CrudRepository<Box, Long> {

    List<Box> findAllByOrderByBoxId();
}
