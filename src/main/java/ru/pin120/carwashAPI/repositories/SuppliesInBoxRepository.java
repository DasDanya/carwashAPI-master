package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.SuppliesInBox;

import java.util.Optional;

@Repository
public interface SuppliesInBoxRepository extends JpaRepository<SuppliesInBox, Long> {
    Optional<SuppliesInBox> findByBox_BoxIdAndSupply_SupId(Long boxId, Long supId);

    @Query("SELECT COUNT(s) FROM SuppliesInBox s WHERE s.box.boxId = :boxId AND s.supply.supId = :supId AND  s.sibId <> :sibId")
    int countByBoxIdAndSupplyIdExceptCurrent(@Param("boxId") Long boxId, @Param("supId") Long supId, @Param("sibId") Long sibId);
}
