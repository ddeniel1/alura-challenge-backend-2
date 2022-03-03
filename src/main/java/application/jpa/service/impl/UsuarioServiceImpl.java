package application.jpa.service.impl;

import application.jpa.entities.Role;
import application.jpa.entities.Usuario;
import application.jpa.repository.RoleRepository;
import application.jpa.repository.UsuarioRepository;
import application.jpa.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                       PasswordEncoder bCryptPasswordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Usuario getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public void saveUser(Usuario usuario, HashSet<Role> roles) {
        usuario.setCreated(LocalDate.now());
        usuario.setUpdated(LocalDate.now());
        usuario.setExpired(false);
        usuario.setLocked(false);
        usuario.setPassword(bCryptPasswordEncoder.encode(usuario.getPassword()));
        usuario.setActive(true);
        usuario.setRoles(roles);
        usuarioRepository.save(usuario);
    }
}
