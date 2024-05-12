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
}
