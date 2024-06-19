package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.SuppliesInBox;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий расходных материалов в боксе
 */
@Repository
public interface SuppliesInBoxRepository extends JpaRepository<SuppliesInBox, Long> {

    /**
     * Поиск расходного материала в боксе по id бокса и id расходного материала
     *
     * @param boxId id бокса
     * @param supId id расходного материала
     * @return объект Optional, содержащий найденную запись или пустой, если поставка не найдена
     */
    Optional<SuppliesInBox> findByBox_BoxIdAndSupply_SupId(Long boxId, Long supId);

    /**
     * Подсчет количества расходных материалов в боксе по id бокса и расходного материала, исключая текущую запись
     *
     * @param boxId id бокса
     * @param supId id расходного материала
     * @param sibId id расходного материала в боксе
     * @return Количество записей
     */
    @Query("SELECT COUNT(s) FROM SuppliesInBox s WHERE s.box.boxId = :boxId AND s.supply.supId = :supId AND  s.sibId <> :sibId")
    int countByBoxIdAndSupplyIdExceptCurrent(@Param("boxId") Long boxId, @Param("supId") Long supId, @Param("sibId") Long sibId);

    /**
     * Получение списка расходных материалов в боксе по его id, названию категории расходных материалов и количеству расходного материала
     *
     * @param boxId id бокса
     * @param catName название категории расходных материалов
     * @param count количество расходных материалов
     * @return Список расходных материалов в боксе
     */
    List<SuppliesInBox> findByBox_BoxIdAndSupply_Category_cSupNameAndCountSuppliesGreaterThan(Long boxId, String catName, int count);
}
