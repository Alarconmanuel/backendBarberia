package com.barberia.config;

import com.barberia.enums.RolEnum;
import com.barberia.model.Usuario;
import com.barberia.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        boolean exists = usuarioRepository.findByCorreo("admin@barberia.com").isPresent();
        if (!exists) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin Barberia");
            admin.setCorreo("admin@barberia.com");
            admin.setTelefono("3000000000");
            admin.setPassword(passwordEncoder.encode("Admin1234"));
            admin.setRol(RolEnum.SUPERADMIN);
            admin.setActivo(true);
            admin.setFechaRegistro(LocalDateTime.now());
            usuarioRepository.save(admin);
            log.info("Superadmin creado: admin@barberia.com / Admin1234");
        } else {
            log.info("Superadmin ya existe, no se crea.");
        }
    }
}
