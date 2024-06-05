package ru.pin120.carwashAPI.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.Exceptions.UserExistsException;
import ru.pin120.carwashAPI.dtos.UserDTO;
import ru.pin120.carwashAPI.models.User;
import ru.pin120.carwashAPI.models.UserRole;
import ru.pin120.carwashAPI.repositories.UserRepository;
import ru.pin120.carwashAPI.security.Aes;
import ru.pin120.carwashAPI.security.details.UserDetailsImpl;
import ru.pin120.carwashAPI.security.jwt.JwtUtils;
import ru.pin120.carwashAPI.security.requests.LoginRequest;
import ru.pin120.carwashAPI.security.requests.RegisterRequest;
import ru.pin120.carwashAPI.security.responses.JwtResponse;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Aes aes;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, Aes aes, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.aes = aes;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public JwtResponse create(RegisterRequest registerRequest) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        User user = new User();
        user.setUsName(aes.decrypt(registerRequest.getUsername()));
        Optional<User> existsUser = userRepository.findByUsName(user.getUsName());
        if(existsUser.isPresent()){
            throw new UserExistsException(String.format("Имя %s уже занято",user.getUsName()));
        }

        user.setUsPassword(encoder.encode(aes.decrypt(registerRequest.getPassword())));
        user.setUsRole(UserRole.valueOf(aes.decrypt(registerRequest.getRole())));

        userRepository.save(user);

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String jwtToken = jwtUtils.generateJwtToken(userDetails);
        jwtToken = aes.encrypt(jwtToken);

        return new JwtResponse(jwtToken, toDTO(user));
    }

    private UserDTO toDTO(User user) throws IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsId(aes.encrypt(user.getUsId().toString()));
        userDTO.setUsName(aes.encrypt(user.getUsName()));
        userDTO.setUsRole(aes.encrypt(user.getUsRole().name()));
        return userDTO;
    }

    public List<UserDTO> getAll() throws IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Sort sort = Sort.by(Sort.Order.desc("usRole"), Sort.Order.asc("usName"));
        List<User> users = userRepository.findAll(sort);

        List<UserDTO> userDTOS = new ArrayList<>();
        for(User user: users){
            userDTOS.add(toDTO(user));
        }

        return userDTOS;
    }

    public JwtResponse login(LoginRequest loginRequest) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String username = aes.decrypt(loginRequest.getUsername());
        String password = aes.decrypt(loginRequest.getPassword());
        //String username = loginRequest.getUsername();
        //String password = loginRequest.getPassword();

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwtToken = jwtUtils.generateJwtToken((UserDetailsImpl) authentication.getPrincipal());
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        jwtToken = aes.encrypt(jwtToken);

        return new JwtResponse(jwtToken, toDTO(userDetails.getUser()));
    }

    public Optional<User> getByUsName(String usName) {
        return userRepository.findByUsName(usName);
    }

    @Transactional
    public void delete(User user) {
        if(user.getUsRole() == UserRole.OWNER){
            throw new IllegalArgumentException("Нельзя удалить пользователя с ролью Владелец");
        }else{
            userRepository.delete(user);
        }
    }

    public JwtResponse editPassword(User user, String password, String authorizationHeader) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String jwtToken = jwtUtils.getJwtToken(authorizationHeader);
        if(jwtToken != null){
            Optional<User> userOptional = userRepository.findByUsName(jwtUtils.getUsernameFromJwtToken(jwtToken));
            if(userOptional.isEmpty()){
                throw new EntityNotFoundException("Пользователь, выполняющий операцию, не найден");
            }else{
                User operateUser = userOptional.get();
                String newPassword = aes.decrypt(password);
                if(operateUser.getUsRole() == UserRole.OWNER){
                    if(user.getUsRole() == UserRole.OWNER) {
                        if (!Objects.equals(user.getUsId(), operateUser.getUsId())) {
                            throw new IllegalArgumentException("Нельзя менять пароль другому владельцу");
                        }
                        user.setUsPassword(encoder.encode(newPassword));
                    }else{
                        user.setUsPassword(encoder.encode(newPassword));
                    }
                }else{
                    throw new IllegalArgumentException("Пользователь не имеет права менять пароль");
                }

                userRepository.save(user);

                if(Objects.equals(user.getUsId(), operateUser.getUsId())) {
                    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsName(), newPassword));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

                //UserDetailsImpl userDetails = UserDetailsImpl.build(user);
                //String jwtTokenUser = jwtUtils.generateJwtToken(userDetails);
                //jwtTokenUser = aes.encrypt(jwtTokenUser);
                jwtToken = aes.encrypt(jwtToken);
                return new JwtResponse(jwtToken, toDTO(user));
            }

        }else{
            throw new IllegalArgumentException("Отсутствует jwt токен");
        }
    }
}
