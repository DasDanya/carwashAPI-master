package ru.pin120.carwashAPI.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.BookingIdSequence;

import java.util.Optional;

/**
 * Репозиторий последовательности для генерации номера заказа
 */
@Repository
public interface BookingIdSequenceRepository extends CrudRepository<BookingIdSequence, Integer> {

    /**
     * Поиск последовательности по указанному году.
     *
     * @param year год
     * @return последовательность, соответствующая указанному году, или null, если она не найдена
     */
    BookingIdSequence findByYear(int year);
}
