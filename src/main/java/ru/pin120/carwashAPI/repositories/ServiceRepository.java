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

/**
 * Репозиторий услуг
 */
@Repository
public interface ServiceRepository extends PagingAndSortingRepository<Service, String> {

    /**
     * Сохранение услуги
     *
     * @param service услуга для сохранения
     */
    void save(Service service);


    /**
     * Поиск услуг по названию категории, с сортировкой по названию услуги в возрастающем порядке
     *
     * @param catName название категории услуги для поиска
     * @return список услуг, удовлетворяющих критериям поиска, отсортированный по названию услуги в возрастающем порядке
     */
    List<Service> findByCategory_CatNameOrderByServNameAsc(String catName);

    /**
     * Поиск услуг по категории
     *
     * @param category категория услуг
     * @return список услуг, удовлетворяющих критериям поиска
     */
    List<Service> findByCategory(CategoryOfServices category);

    /**
     * Поиск услуги по названию
     *
     * @param servName название услуги
     * @return объект Optional, содержащий найденную услугу или пустой, если услуга не найдена
     */
    Optional<Service> findByServName(String servName);

    /**
     * Проверка существования услуги по названию (игнорируя регистр).
     *
     * @param servName название услуги для проверки
     * @return true, если услуга с указанным названием существует, иначе false
     */
    boolean existsByServNameIgnoreCase(String servName);

    /**
     * Удаление услуги по названию
     *
     * @param servName название услуги для удаления
     */
    void deleteByServName(String servName);


    //@Query(value = "Select serv_name FROM services WHERE cat_name = :catName", nativeQuery = true)
    //List<String> getAllServicesName(@Param("catName") String catName);

    //@Query("SELECT s FROM Service s JOIN s.category c ORDER BY LOWER(c.catName), LOWER(s.servName) ASC")
    //List<Service> findAllSortedByCategoryNameAndServiceName();
}
