package application.jpa.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "usuario_role")
public class UsuarioRole {
    @EmbeddedId
    private UsuarioRoleId id;

    public UsuarioRoleId getId() {
        return id;
    }

    public void setId(UsuarioRoleId id) {
        this.id = id;
    }
}