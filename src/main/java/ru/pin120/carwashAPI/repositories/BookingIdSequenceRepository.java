package ru.pin120.carwashAPI.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.BookingIdSequence;

import java.util.Optional;

@Repository
public interface BookingIdSequenceRepository extends CrudRepository<BookingIdSequence, Integer> {
    BookingIdSequence findByYear(int year);
}
