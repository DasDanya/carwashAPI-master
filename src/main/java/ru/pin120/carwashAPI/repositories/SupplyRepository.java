package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Supply;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, Long> {

    @Query("SELECT COUNT(s) FROM Supply s WHERE s.supName = :name AND s.category.cSupName = :category AND s.supMeasure = :measure")
    int countBySupNameAndCatNameAndMeasure(@Param("name") String name, @Param("category") String category,@Param("measure") int measure);

    @Query("SELECT COUNT(s) FROM Supply s WHERE s.supName = :name AND s.category.cSupName = :category AND s.supMeasure = :measure AND s.supId <> :supId")
    int countBySupNameAndCatNameAndMeasureWithoutCurrent(@Param("name") String name, @Param("category") String category,@Param("measure") int measure, @Param("supId") Long supId);
}
