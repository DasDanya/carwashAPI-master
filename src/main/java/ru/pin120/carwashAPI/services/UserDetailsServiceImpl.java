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

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository
                .findByUsName(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Пользователь с именем %s не найден", username)));

        return UserDetailsImpl.build(user);
    }

}
