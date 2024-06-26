package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.ClientsTransport;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий транспорта клиента
 */
@Repository
public interface ClientsTransportRepository extends PagingAndSortingRepository<ClientsTransport, Long> {

    /**
     * Сохранение транспорта клиента
     *
     * @param clientsTransport транспорт клиента
     */
    void save(ClientsTransport clientsTransport);

    /**
     * Получение списка транспорта клиента
     *
     * @param clientId id транспорта клиента
     * @return Список транспорта клиента
     */
    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    List<ClientsTransport> findByClientId(Long clientId);



    /**
     * Подсчет количества транспорта клиента по госномеру, id клиента и id транспорта
     *
     * @param stateNumber госномер
     * @param clId id клиента
     * @param trId id транспорта
     * @return Количество транспорта
     */
    @Query("SELECT COUNT(ct) FROM ClientsTransport ct WHERE ct.clTrStateNumber = :stateNumber AND ct.client.clId = :clId AND ct.transport.trId = :trId")
    int countByStateNumberAndClientIdAndTransportId(@Param("stateNumber") String stateNumber, @Param("clId") Long clId, @Param("trId") Long trId);


    /**
     * Подсчет количества транспорта клиента по госномеру, id клиента и id транспорта, исключая текущий транспорт клиента
     *
     * @param stateNumber госномер
     * @param clId id клиента
     * @param trId id транспорта
     * @param clTrId id текущего транспорта клиента
     * @return Количество транспорта
     */
    @Query("SELECT COUNT(ct) FROM ClientsTransport ct WHERE ct.clTrStateNumber = :stateNumber AND ct.client.clId = :clId AND ct.transport.trId = :trId AND ct.clTrId <> :clTrId ")
    int countByStateNumberAndClientIdAndTransportIdWithoutCurrentId(@Param("stateNumber") String stateNumber, @Param("clId") Long clId, @Param("trId") Long trId, @Param("clTrId") Long clTrId);

    /**
     * Поиск транспорта клиента по id
     *
     * @param id id транспорта
     * @return объект Optional, содержащий найденный транспорт или пустой, если транспорт не найден
     */
    Optional<ClientsTransport> findByClTrId(Long id);

    /**
     * Удаление транспорта клиента по id
     *
     * @param clTrId id транспорта клиента
     */
    void deleteByClTrId(Long clTrId);

    // Поиск

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndMarkAndModelAndCategoryAndStateNumber(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("model") String model, @Param("category") String category, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndMarkAndModelAndCategory(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("model") String model, @Param("category") String category);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndMarkAndModelAndStateNumber(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("model") String model, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndMarkAndCategoryAndStateNumber(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("category") String category, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndModelAndCategoryAndStateNumber(@Param("clientId") Long clientId, @Param("model") String model, @Param("category") String category, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndMarkAndModel(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("model") String model);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndMarkAndCategory(@Param("clientId") Long clientId, @Param("mark") String mark,  @Param("category") String category);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndMarkAndStateNumber(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndModelAndCategory(@Param("clientId") Long clientId, @Param("model") String model, @Param("category") String category);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndModelAndStateNumber(@Param("clientId") Long clientId,@Param("model") String model,@Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndCategoryAndStateNumber(@Param("clientId") Long clientId,@Param("category") String category, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndMark(@Param("clientId") Long clientId, @Param("mark") String mark);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndModel(@Param("clientId") Long clientId,@Param("model") String model);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndCategory(@Param("clientId") Long clientId, @Param("category") String category);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
     List<ClientsTransport> findByClientIdAndStateNumber(@Param("clientId") Long clientId,@Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE " +
            "LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    List<ClientsTransport> findByStateNumber(@Param("stateNumber") String stateNumber);
}
