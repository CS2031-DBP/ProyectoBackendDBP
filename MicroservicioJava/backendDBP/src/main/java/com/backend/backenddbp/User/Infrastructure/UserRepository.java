package com.backend.backenddbp.User.Infrastructure;

import com.backend.backenddbp.User.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);
    @Query("Select u from User u where u.username like %:query% or u.primerNombre " +
            "like %:query% or u.segundoNombre like %:query%" +
            " or u.primerApellido like %:query% or u.segundoApellido like %:query%")
    List<User> searchUser(@Param("query") String query);
}
