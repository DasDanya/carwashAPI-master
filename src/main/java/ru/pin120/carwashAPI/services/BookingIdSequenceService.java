package ru.pin120.carwashAPI.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.models.BookingIdSequence;
import ru.pin120.carwashAPI.repositories.BookingIdSequenceRepository;

import java.time.LocalDate;

/**
 * Сервис последовательности генерации номера заказа
 */
@Service
public class BookingIdSequenceService {

    /**
     * Репозиторий последовательности генерации номера заказа
     */
    private final BookingIdSequenceRepository bookingIdSequenceRepository;

    /**
     * Внедрение зависимостей
     * @param bookingIdSequenceRepository репозиторий последовательности генерации номера заказа
     */
    public BookingIdSequenceService(BookingIdSequenceRepository bookingIdSequenceRepository) {
        this.bookingIdSequenceRepository = bookingIdSequenceRepository;
    }

    /**
     * Генерация номера заказа
     * @return Номер заказа
     */
    @Transactional
    public String generateId(){
        int currentYear = LocalDate.now().getYear();
        BookingIdSequence idSequence = bookingIdSequenceRepository.findByYear(currentYear);
        if (idSequence == null) {
            idSequence = new BookingIdSequence(currentYear, 1);
        } else {
            idSequence.setLastId(idSequence.getLastId() + 1);
        }

        bookingIdSequenceRepository.save(idSequence);

        return currentYear + "-" + idSequence.getLastId();
    }
}
