package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.ClientsTransport;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientsTransportRepository extends PagingAndSortingRepository<ClientsTransport, Long> {

    void save(ClientsTransport clientsTransport);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    List<ClientsTransport> findByClientId(Long clientId);


    @Query("SELECT COUNT(ct) FROM ClientsTransport ct WHERE ct.clTrStateNumber = :stateNumber AND ct.client.clId = :clId AND ct.transport.trId = :trId")
    int countByStateNumberAndClientIdAndTransportId(@Param("stateNumber") String stateNumber, @Param("clId") Long clId, @Param("trId") Long trId);

    @Query("SELECT COUNT(ct) FROM ClientsTransport ct WHERE ct.clTrStateNumber = :stateNumber AND ct.client.clId = :clId AND ct.transport.trId = :trId AND ct.clTrId <> :clTrId ")
    int countByStateNumberAndClientIdAndTransportIdWithoutCurrentId(@Param("stateNumber") String stateNumber, @Param("clId") Long clId, @Param("trId") Long trId, @Param("clTrId") Long clTrId);

    Optional<ClientsTransport> findByClTrId(Long id);

    void deleteByClTrId(Long clTrId);

    // Поиск

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndMarkAndModelAndCategoryAndStateNumber(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("model") String model, @Param("category") String category, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndMarkAndModelAndCategory(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("model") String model, @Param("category") String category);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndMarkAndModelAndStateNumber(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("model") String model, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndMarkAndCategoryAndStateNumber(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("category") String category, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndModelAndCategoryAndStateNumber(@Param("clientId") Long clientId, @Param("model") String model, @Param("category") String category, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndMarkAndModel(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("model") String model);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndMarkAndCategory(@Param("clientId") Long clientId, @Param("mark") String mark,  @Param("category") String category);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndMarkAndStateNumber(@Param("clientId") Long clientId, @Param("mark") String mark, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndModelAndCategory(@Param("clientId") Long clientId, @Param("model") String model, @Param("category") String category);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndModelAndStateNumber(@Param("clientId") Long clientId,@Param("model") String model,@Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndCategoryAndStateNumber(@Param("clientId") Long clientId,@Param("category") String category, @Param("stateNumber") String stateNumber);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trMark) LIKE LOWER(CONCAT('%', :mark, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndMark(@Param("clientId") Long clientId, @Param("mark") String mark);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.trModel) LIKE LOWER(CONCAT('%', :model, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndModel(@Param("clientId") Long clientId,@Param("model") String model);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.transport.categoryOfTransport.catTrName) LIKE LOWER(CONCAT('%', :category, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndCategory(@Param("clientId") Long clientId, @Param("category") String category);

    @Query("SELECT ct FROM ClientsTransport ct WHERE ct.client.clId = :clientId " +
            "AND LOWER(ct.clTrStateNumber) LIKE LOWER(CONCAT('%', :stateNumber, '%')) " +
            "ORDER BY  ct.transport.trMark, ct.transport.trModel,ct.transport.categoryOfTransport.catTrName, ct.clTrStateNumber ASC")
    public List<ClientsTransport> findByClientIdAndStateNumber(@Param("clientId") Long clientId,@Param("stateNumber") String stateNumber);
}
