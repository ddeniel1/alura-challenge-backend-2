package application.jpa.service;

import application.jpa.entities.Role;

import java.util.HashSet;

public interface RoleService {

    HashSet<Role> getRoleByName(String roleName);

}
