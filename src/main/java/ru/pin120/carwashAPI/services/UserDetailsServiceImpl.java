package ru.pin120.carwashAPI.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.models.User;
import ru.pin120.carwashAPI.repositories.UserRepository;
import ru.pin120.carwashAPI.security.details.UserDetailsImpl;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Реализация интерфейса UserDetailsService для аутентификации пользователей в системе
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Репозиторий пользователей
     */
    private final UserRepository userRepository;

    /**
     * Внедрение зависимости
     * @param userRepository репозиторий пользователей
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Формирует UserDetailsImpl по имени пользователя (username)
     *
     * @param username имя пользователя, по которому производится поиск
     * @return UserDetails объект, представляющий данные пользователя
     * @throws UsernameNotFoundException если пользователь с указанным именем не найден
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository
                .findByUsName(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Пользователь с именем %s не найден", username)));

        return UserDetailsImpl.build(user);
    }

}
