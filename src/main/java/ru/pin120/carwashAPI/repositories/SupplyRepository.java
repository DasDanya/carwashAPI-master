package ru.pin120.carwashAPI.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.Supply;

/**
 * Репозиторий расходных материалов
 */
@Repository
public interface SupplyRepository extends JpaRepository<Supply, Long> {

    /**
     * Подсчет количества расходных материалов по названию, категории и количеству/объёму единицы
     *
     * @param name название расходного материала
     * @param category название категории
     * @param measure количество/объём единицы
     * @return количество расходных материалов, удовлетворяющих критериям поиска
     */
    @Query("SELECT COUNT(s) FROM Supply s WHERE s.supName = :name AND s.category.cSupName = :category AND s.supMeasure = :measure")
    int countBySupNameAndCatNameAndMeasure(@Param("name") String name, @Param("category") String category,@Param("measure") int measure);

    /**
     * Подсчет количества расходных материалов по названию, категории и количеству/объёму единицы, исключая текущий расходный материал
     *
     * @param name название расходного материала
     * @param category название категории
     * @param measure количество/объём единицы
     * @param supId id исключаемого расходного материала
     * @return количество расходных материалов, удовлетворяющих критериям поиска
     */
    @Query("SELECT COUNT(s) FROM Supply s WHERE s.supName = :name AND s.category.cSupName = :category AND s.supMeasure = :measure AND s.supId <> :supId")
    int countBySupNameAndCatNameAndMeasureWithoutCurrent(@Param("name") String name, @Param("category") String category,@Param("measure") int measure, @Param("supId") Long supId);
}
