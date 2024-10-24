package com.backend.backenddbp.Security.Auth;

import com.backend.backenddbp.Mail.MailRegistroEvent;
import com.backend.backenddbp.S3.StorageService;
import com.backend.backenddbp.Security.Auth.DTOS.AuthJwtResponse;
import com.backend.backenddbp.Security.Auth.DTOS.AuthLoginRequest;
import com.backend.backenddbp.Security.Auth.DTOS.AuthRegisterRequest;
import com.backend.backenddbp.Security.JWT.JwtService;
import com.backend.backenddbp.User.Domain.User;
import com.backend.backenddbp.User.Infrastructure.UserRepository;
import com.backend.backenddbp.User.Domain.Rol;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class AuthService {
    final UserRepository userRepository;
    final JwtService jwtService;
    final PasswordEncoder passwordEncoder;
    final ModelMapper modelMapper;
    final StorageService storageService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder, ModelMapper modelMapper, StorageService storageService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.storageService = storageService;
    }
    public AuthJwtResponse login(AuthLoginRequest authLoginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(authLoginRequest.getEmail());
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        if(!passwordEncoder.matches(authLoginRequest.getPassword(), userOptional.get().getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        AuthJwtResponse response = new AuthJwtResponse();
        response.setToken(jwtService.generateToken(userOptional.get()));
        return response;
    }
    public AuthJwtResponse register(AuthRegisterRequest authRegisterRequest) throws Exception {
        Optional<User> userOptional = userRepository.findByEmail(authRegisterRequest.getEmail());
        if (userOptional.isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User newuser = mapear(authRegisterRequest);
        newuser.setPassword(passwordEncoder.encode(authRegisterRequest.getPassword()));
        newuser.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
        if(!authRegisterRequest.getRole().isEmpty()){
            if(authRegisterRequest.getRole().toUpperCase().equals("TRAVELER")){
                newuser.setRole(Rol.TRAVELER);}
            else if(authRegisterRequest.getRole().toUpperCase().equals("HOST")){
                newuser.setRole(Rol.HOST);}
        }
        newuser.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
        System.out.println(newuser);
        System.out.println(newuser.getRole());
        userRepository.save(newuser);

        AuthJwtResponse response = new AuthJwtResponse();
        response.setToken(jwtService.generateToken(newuser));
        eventPublisher.publishEvent(new MailRegistroEvent(newuser.getEmail()));
        return response;
    }
    public boolean logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return false;
        }
        jwtService.invalidateToken();
        return true;
    }


    private User mapear(AuthRegisterRequest authRegisterRequest) throws Exception {
        User user = new User();
        user.setPrimerNombre(authRegisterRequest.getPrimerNombre());
        user.setSegundoNombre(authRegisterRequest.getSegundoNombre());
        user.setPrimerApellido(authRegisterRequest.getPrimerApellido());
        user.setSegundoApellido(authRegisterRequest.getSegundoApellido());
        user.setUsername(authRegisterRequest.getUserName());
        user.setEdad(authRegisterRequest.getEdad());
        user.setTelefono(authRegisterRequest.getTelefono());
        user.setGenero(authRegisterRequest.getGenero());
        user.setCiudad(authRegisterRequest.getCiudad());
        user.setPais(authRegisterRequest.getPais());
        user.setEmail(authRegisterRequest.getEmail());
        return user;
    }
}
