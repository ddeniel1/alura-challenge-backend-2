package application.jpa.service.impl;

import application.jpa.entities.Usuario;
import application.jpa.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario user = userService.getUsuarioByUsername(username);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return buildUserForAuthentication(user, authorities);
    }


    private UserDetails buildUserForAuthentication(Usuario user, Collection<? extends GrantedAuthority> authorities) {
        return new User(user.getUsername(), user.getPassword(),
                user.getActive(), true, true, true, authorities);
    }
}
