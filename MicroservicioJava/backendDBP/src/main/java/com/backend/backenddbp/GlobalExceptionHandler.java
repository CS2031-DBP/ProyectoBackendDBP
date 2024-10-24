package com.backend.backenddbp;

import dbp.connect.Alojamiento.Excepciones.AlojamientoNotFound;
import dbp.connect.Alojamiento.Excepciones.DescripcionIgualException;
import dbp.connect.Chat.Exceptions.ChatNotFound;
import dbp.connect.Chat.Exceptions.NotAllowedPermissionChat;
import dbp.connect.Comentarios.Excepciones.ComentarioNoEncontradoException;
import dbp.connect.Comentarios.Excepciones.PublicacionNoEncontradoException;
import dbp.connect.Friendship.Exceptions.NotFriendException;
import dbp.connect.Mensaje.Exceptions.MessageException;
import dbp.connect.PublicacionInicio.Exceptions.UsuarioNoCoincideId;
import dbp.connect.Review.Exceptions.ReviewNotFoundException;
import dbp.connect.Security.Utils.ErrorDetail;
import dbp.connect.User.Exceptions.BadCredentialException;
import dbp.connect.User.Exceptions.UserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ComentarioNoEncontradoException.class)
    public ResponseEntity<String> exception(ComentarioNoEncontradoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(PublicacionNoEncontradoException.class)
    public ResponseEntity<String> exception(PublicacionNoEncontradoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DescripcionIgualException.class)
    public ResponseEntity<String> handleRecursoNoEncontradoException(DescripcionIgualException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<String> handleReviewNotFounException(ReviewNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsuarioNoCoincideId.class)
    public ResponseEntity<String> handleRecursoNoEncontradoException(UsuarioNoCoincideId ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDetail> UserExceptionHandler(UserException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MessageException.class)
    public ResponseEntity<ErrorDetail> MessageExceptionHandler(MessageException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadCredentialException.class)
    public ResponseEntity<ErrorDetail> BadCredentialExceptionHandler(BadCredentialException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(NotAllowedPermissionChat.class)
    public ResponseEntity<ErrorDetail> ChatNotFoundHandler(NotAllowedPermissionChat e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(ChatNotFound.class)
    public ResponseEntity<ErrorDetail> ChatNotFoundHandler(ChatNotFound e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(AlojamientoNotFound.class)
    public ResponseEntity<ErrorDetail> AlojamientoNotFoundHandler(AlojamientoNotFound e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(NotFriendException.class)
    public ResponseEntity<ErrorDetail> NotFriendExceptionHandler(NotFriendException e, WebRequest req){
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setMessage(e.getMessage());
        errorDetail.setError(req.getDescription(false));
        errorDetail.setTimestamp(java.time.ZonedDateTime.now());
        return new ResponseEntity<>(errorDetail, HttpStatus.UNAUTHORIZED);
    }
}
