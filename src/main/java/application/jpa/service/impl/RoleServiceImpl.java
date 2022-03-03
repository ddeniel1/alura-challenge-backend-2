package application.jpa.service.impl;

import application.jpa.entities.Role;
import application.jpa.repository.RoleRepository;
import application.jpa.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;


    @Override
    public HashSet<Role> getRoleByName(String roleName) {
        return new HashSet<>(List.of(roleRepository.findByRole(roleName)));
    }
}
