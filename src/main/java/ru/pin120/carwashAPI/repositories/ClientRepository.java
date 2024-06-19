package ru.pin120.carwashAPI.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Client;
import ru.pin120.carwashAPI.models.Transport;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий клиента
 */
@Repository
public interface ClientRepository extends PagingAndSortingRepository<Client, Long> {

    /**
     * Сохранение клиента в базе данных
     *
     * @param client клиент для сохранения
     */
    void save(Client client);

    /**
     * Поиск клиента по id
     *
     * @param clId id клиента для поиска
     * @return объект Optional, содержащий найденного клиента или пустой, если клиент не найден
     */
    Optional<Client> findByClId(Long clId);

    /**
     * Удаление клиента по id
     *
     * @param clId id клиента для удаления
     */
    void deleteByClId(Long clId);


    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount < :discount ")
    List<Client> query1(@Param("surname") String surname, @Param("name") String name, @Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount > :discount ")
    List<Client> query2(@Param("surname") String surname, @Param("name") String name, @Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount = :discount ")
    List<Client> query3(@Param("surname") String surname, @Param("name") String name, @Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%'))")
    List<Client> query4(@Param("surname") String surname, @Param("name") String name, @Param("phone") String phone,Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND c.clDiscount < :discount")
    List<Client> query5(@Param("surname") String surname, @Param("name") String name, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND c.clDiscount > :discount")
    List<Client> query6(@Param("surname") String surname, @Param("name") String name, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND c.clDiscount = :discount")
    List<Client> query7(@Param("surname") String surname, @Param("name") String name, @Param("discount") Integer discount, Pageable pageable);
    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount < :discount")
    List<Client> query8(@Param("surname") String surname, @Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount > :discount")
    List<Client> query9(@Param("surname") String surname, @Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount = :discount")
    List<Client> query10(@Param("surname") String surname, @Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount < :discount")
    List<Client> query11(@Param("name") String name, @Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount > :discount")
    List<Client> query12(@Param("name") String name, @Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount = :discount")
    List<Client> query13(@Param("name") String name, @Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%'))")
    List<Client> query14(@Param("surname") String surname, @Param("name") String name,Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%'))")
    List<Client> query15(@Param("surname") String surname, @Param("phone") String phone,Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND c.clDiscount < :discount")
    List<Client> query16(@Param("surname") String surname, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND c.clDiscount > :discount")
    List<Client> query17(@Param("surname") String surname, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%')) " +
            "AND c.clDiscount = :discount")
    List<Client> query18(@Param("surname") String surname, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Client> query19(@Param("name") String name, @Param("phone") String phone,Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND c.clDiscount < :discount")
    List<Client> query20(@Param("name") String name, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND c.clDiscount > :discount")
    List<Client> query21(@Param("name") String name, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND c.clDiscount = :discount")
    List<Client> query22(@Param("name") String name, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount < :discount")
    List<Client> query23(@Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount > :discount")
    List<Client> query24(@Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%')) " +
            "AND c.clDiscount = :discount")
    List<Client> query25(@Param("phone") String phone, @Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clSurname) LIKE LOWER(CONCAT('%', :surname, '%'))")
    List<Client> query26(@Param("surname") String surname, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Client> query27(@Param("name") String name, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE LOWER(c.clPhone) LIKE LOWER(CONCAT('%', :phone, '%'))")
    List<Client> query28(@Param("phone") String phone, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.clDiscount < :discount")
    List<Client> query29(@Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.clDiscount > :discount")
    List<Client> query30(@Param("discount") Integer discount, Pageable pageable);

    @Query("SELECT c FROM Client c WHERE c.clDiscount = :discount")
    List<Client> query31(@Param("discount") Integer discount, Pageable pageable);
}
