package ru.pin120.carwashAPI.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.pin120.carwashAPI.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий пользователей
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по имени
     * @param usName имя пользователя
     * @return объект Optional, содержащий найденного пользователя или пустой, если пользователь не найден
     */
    Optional<User> findByUsName(String usName);

}
