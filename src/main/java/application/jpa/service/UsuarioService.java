package application.jpa.service;

import application.jpa.entities.Role;
import application.jpa.entities.Usuario;

import java.util.HashSet;

public interface UsuarioService {

    Usuario getUsuarioByUsername(String username);

    void saveUser(Usuario usuario, HashSet<Role> roles);
}
