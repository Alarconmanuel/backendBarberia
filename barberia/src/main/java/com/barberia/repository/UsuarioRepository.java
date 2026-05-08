package com.barberia.repository;

import com.barberia.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Consulta nativa 1: buscar usuario por correo
    @Query(value = "SELECT * FROM usuario WHERE correo = :correo", nativeQuery = true)
    Optional<Usuario> findByCorreo(@Param("correo") String correo);

    // Consulta nativa 2: usuarios activos por rol
    @Query(value = "SELECT * FROM usuario WHERE rol = :rol AND activo = true", nativeQuery = true)
    List<Usuario> findActivosByRol(@Param("rol") String rol);

    // Buscar usuario por nombre (contiene, ignora mayúsculas)
    @Query(value = "SELECT * FROM usuario WHERE LOWER(nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))", nativeQuery = true)
    List<Usuario> findByNombreContaining(@Param("nombre") String nombre);
}