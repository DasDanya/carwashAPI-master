package ru.pin120.carwashAPI.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Cleaner;

@Repository
public interface CleanerRepository extends CrudRepository<Cleaner, Long> {

}
