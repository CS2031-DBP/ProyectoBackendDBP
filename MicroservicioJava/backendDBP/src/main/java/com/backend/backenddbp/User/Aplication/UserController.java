package com.backend.backenddbp.User.Aplication;

import com.backend.backenddbp.User.DTO.*;
import com.backend.backenddbp.User.Domain.User;
import com.backend.backenddbp.User.Domain.UserService;
import com.backend.backenddbp.User.Exceptions.BadCredentialException;
import com.backend.backenddbp.User.Exceptions.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private  UserService userService;

    @GetMapping("/perfilMasInformacion")
    public ResponseEntity<UserProfileDTO> getProfile(@RequestHeader("Authorization") String token) throws BadCredentialException, UserException {
        UserProfileDTO userProfileDTO = userService.finddUserProfile(token);
        return new ResponseEntity<UserProfileDTO>(userProfileDTO, HttpStatus.ACCEPTED);
    }
    @GetMapping("{query}")
    public ResponseEntity<List<UserSearchDTO>> searchUser(@PathVariable  String query) {
        List<UserSearchDTO> userSearchDTOList = userService.searchUser(query);
        return new ResponseEntity<List<UserSearchDTO>>(userSearchDTOList, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateProfile(@RequestHeader("Authorization") String token,
                                              @RequestPart("Data") UpdateUserNameAndProfileDTO update,
                                              @RequestPart("file")MultipartFile foto
                                              ) throws Exception {
        UserProfileDTO userProfDTO = userService.finddUserProfile(token);
        userService.UpdateUser(userProfDTO.getId(), update,foto);
        return ResponseEntity.accepted().build();
    }
    @GetMapping("/perfil")
    public ResponseEntity<informacionDelusuario> obtenerPequeñaInfo(@RequestHeader("Authorization") String token)
            throws BadCredentialException, UserException {
        informacionDelusuario informacionDelusuario = userService.obtenerInformacionUsuario();
        return new ResponseEntity<informacionDelusuario>(informacionDelusuario, HttpStatus.ACCEPTED);
    }
    /*// Cambiar Contraseña
@PostMapping("/changePassword")
public ResponseEntity<Void> changePassword(@RequestHeader("Authorization") String token, @RequestBody ChangePasswordDTO changePasswordDTO) throws Exception {
    userService.changePassword(token, changePasswordDTO);
    return ResponseEntity.ok().build();
}

// Obtener Usuarios por ID*/
    @GetMapping("/findById/{userId}")
    public ResponseEntity<UserProfileDTO> getUserById(@PathVariable Long userId) {
        UserProfileDTO userProfileDTO = userService.findUserProfileById(userId);
        return new ResponseEntity<>(userProfileDTO, HttpStatus.OK);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String token) throws Exception {
        userService.deleteUser(token);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/profile/informacionPersonal")
    public ResponseEntity<Void> updatePersonalInformation(@RequestHeader("Authorization") String token,
                                                          @RequestBody UpdateUserDTO update) throws Exception {
        userService.updateInformacionPersonal(token, update);
        return ResponseEntity.accepted().build();
    }
/*
//Eliminar Usuario

@PostMapping("/actualizarEstado/{userId}")
public ResponseEntity<Void> actualizarEstado(@PathVariable Long userId, @RequestBody String estado) {
    userService.actualizarEstado(userId, estado);
    return ResponseEntity.ok().build();
}
@GetMapping("/sugerenciasAmigos/{userId}")
public ResponseEntity<List<UserProfileDTO>> obtenerSugerenciasAmigos(@PathVariable Long userId) {
    List<UserProfileDTO> sugerencias = userService.obtenerSugerenciasAmigos(userId);
    return ResponseEntity.ok(sugerencias);
}
@GetMapping("/verificarAmistad/{userId}/{otherUserId}")
public ResponseEntity<Boolean> verificarAmistad(@PathVariable Long userId, @PathVariable Long otherUserId) {
    boolean esAmigo = userService.verificarAmistad(userId, otherUserId);
    return ResponseEntity.ok(esAmigo);
}
*/
} //hello
