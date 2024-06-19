package ru.pin120.carwashAPI.services;

import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.models.Box;
import ru.pin120.carwashAPI.models.BoxStatus;
import ru.pin120.carwashAPI.repositories.BoxRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис бокса
 */
@Service
public class BoxService {

    /**
     * Репозиторий бокса
     */
    private final BoxRepository boxRepository;

    /**
     * Внедрение зависимости
     * @param boxRepository репозиторий бокса
     */
    public BoxService(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    /**
     * Получение списка всех боксов
     * @return Список со всеми боксами
     */
    public List<Box> getAll(){
        return boxRepository.findAllByOrderByBoxId();
    }

    /**
     * Получение списка доступных боксов
     * @return Список с доступными боксами
     */
    public List<Box> getAvailable(){
        return boxRepository.findAvailable(BoxStatus.CLOSED);
    }

    /**
     * Сохранение данных о боксе
     * @param box бокс
     */
    public void save(Box box){
        boxRepository.save(box);
    }

    /**
     * Получение бокса по id
     * @param boxId id бокса
     * @return Объект Optional с боксом, если он существует
     */
    public Optional<Box> getById(Long boxId){
        return boxRepository.findById(boxId);
    }

    /**
     * Удаление бокса
     * @param boxId id бокса
     */
    public void delete(Long boxId) {
        boxRepository.deleteById(boxId);
    }
}
