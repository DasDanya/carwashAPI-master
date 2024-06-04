package ru.pin120.carwashAPI.services;

import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
        user.setUsRole(UserRole.valueOf(registerRequest.getRole()));
        user.setUsName(registerRequest.getUsername());
        user.setUsPassword(encoder.encode(registerRequest.getPassword()));
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
}
