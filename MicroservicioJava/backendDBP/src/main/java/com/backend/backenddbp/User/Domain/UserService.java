package com.backend.backenddbp.User.Domain;

import com.backend.backenddbp.S3.StorageService;
import com.backend.backenddbp.Security.JWT.JwtService;
import com.backend.backenddbp.Security.Utils.AuthorizationUtils;
import com.backend.backenddbp.User.DTO.*;
import com.backend.backenddbp.User.Exceptions.BadCredentialException;
import com.backend.backenddbp.User.Exceptions.UserException;
import com.backend.backenddbp.User.Infrastructure.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.management.Query;
import javax.swing.text.html.Option;
import java.util.*;


@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private AuthorizationUtils authorizationUtils;

/*
    @Transactional
    public void addFriend(Long userId, Long  friendId) {
        User user = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();
        user.addFriend(friend);
        userRepository.save(user);
        userRepository.save(friend); // Guardar explícitamente el amigo también
    }

    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow();
        User friend = userRepository.findById(friendId).orElseThrow();
        user.removeFriend(friend);
        userRepository.save(user);
        userRepository.save(fri end); // Guardar explícitamente el amigo también
    }
    public List<UserSearchDTO> searchuser(Query query) {
    }

*/
    @Autowired
    ModelMapper modelMapper;

    public UserProfileDTO findUserProfileById(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(user.getId());
        userProfileDTO.setUsername(user.getUsername());
        userProfileDTO.setPNombre(user.getPrimerNombre());
        userProfileDTO.setSNombre(user.getSegundoNombre());
        userProfileDTO.setPApellido(user.getPrimerApellido());
        userProfileDTO.setSApellido(user.getSegundoApellido());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setFotoUrl(user.getFotoUrl());
        userProfileDTO.setCiudad(user.getCiudad());
        userProfileDTO.setPais(user.getPais());
        userProfileDTO.setFechaNacimiento(user.getFechaNacimiento());
        userProfileDTO.setDireccion(user.getDireccion());
        userProfileDTO.setTelefono(user.getTelefono());
        userProfileDTO.setGenero(user.getGenero());
        userProfileDTO.setDescripcion(user.getDescripcion());
        userProfileDTO.setFechaCreacion(user.getCreatedAt());
        return userProfileDTO;
    }

    public UserDetailsService userDetailsService() {
        return username -> userRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserResponseDTO findUserDTOById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setFotoUrl(user.getFotoUrl());
        return userResponseDTO;
    }
    public User findUserById(Long id) throws UserException {
        Optional<User>  opt= userRepository.findById(id);
        if(opt.isPresent()){
            return opt.get();
        }
        throw new UserException("User not found"+id);
    }
    public UserProfileDTO finddUserProfile(String jwt) throws UserException, BadCredentialException {
        String email =  authorizationUtils.authenticateUser();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("User not found"));
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(user.getId());
        userProfileDTO.setUsername(user.getUsername());
        userProfileDTO.setPNombre(user.getPrimerNombre());
        userProfileDTO.setSNombre(user.getSegundoNombre());
        userProfileDTO.setPApellido(user.getPrimerApellido());
        userProfileDTO.setSApellido(user.getSegundoApellido());
        userProfileDTO.setEmail(user.getEmail());
        userProfileDTO.setFotoUrl(user.getFotoUrl());
        userProfileDTO.setCiudad(user.getCiudad());
        userProfileDTO.setPais(user.getPais());
        userProfileDTO.setFechaNacimiento(user.getFechaNacimiento());
        userProfileDTO.setDireccion(user.getDireccion());
        userProfileDTO.setTelefono(user.getTelefono());
        userProfileDTO.setGenero(user.getGenero());
        userProfileDTO.setDescripcion(user.getDescripcion());
        userProfileDTO.setFechaCreacion(user.getCreatedAt());
        userProfileDTO.setRol(user.getRole());
        return userProfileDTO;
    }
    public void UpdateUser(Long userId, UpdateUserNameAndProfileDTO update, MultipartFile foto) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setUsername(update.getUserName());
        if(foto != null) {
            String objectKey = storageService.subiralS3File(foto,serializarId(userId));
            String fotoUrl = storageService.obtenerURL(objectKey);
            user.setFotoUrl(fotoUrl);
        }
        user.setDireccion(update.getDireccion());
        user.setDescripcion(update.getDescripcion());
        user.setFechaNacimiento(update.getFechaNacimiento());
        userRepository.save(user);
    }
    public List<UserSearchDTO> searchUser(String query) {
        List<User> users = userRepository.searchUser(query);
        List<UserSearchDTO> userSearchDTOList = new ArrayList<>();
        for (User user : users) {
            UserSearchDTO userSearchDTO = new UserSearchDTO();
            userSearchDTO.setId(user.getId());
            userSearchDTO.setUsername(user.getUsername());
            userSearchDTO.setFotoUrl(user.getFotoUrl());
            userSearchDTO.setFullName(user.getPrimerNombre()
                    + " " + user.getPrimerApellido() + " " + user.getSegundoApellido());
            userSearchDTOList.add(userSearchDTO);
        }
        return userSearchDTOList;
    }
    public informacionDelusuario obtenerInformacionUsuario(){
        String email = authorizationUtils.authenticateUser();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        informacionDelusuario informacionDelusuario = new informacionDelusuario();
        informacionDelusuario.setId(user.getId());
        informacionDelusuario.setUserName(user.getPrimerNombre() +" " + user.getPrimerApellido());
        informacionDelusuario.setFotoPerfil(user.getFotoUrl());
        informacionDelusuario.setEmail(user.getEmail());
        informacionDelusuario.setRol(user.getRole());
        return informacionDelusuario;
    }
    public void deleteUser(String jwt) throws Exception {
        String email = authorizationUtils.authenticateUser();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        userRepository.delete(user);
    }
    public void updateInformacionPersonal(String jwt, UpdateUserDTO update) throws Exception {
        String email = authorizationUtils.authenticateUser();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setPrimerNombre(update.getPrimerNombre());
        user.setSegundoNombre(update.getSegundoNombre());
        user.setPrimerApellido(update.getPrimerApellido());
        user.setSegundoApellido(update.getSegundoApellido());

        userRepository.save(user);
    }

    private String serializarId(Long imagenId){
        return "imagen-" + imagenId;
    }


}
