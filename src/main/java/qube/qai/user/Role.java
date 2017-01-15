package qube.qai.user;

import qube.qai.services.implementation.UUIDService;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by rainbird on 1/15/17.
 */
@Entity
public class Role implements Serializable {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name="user", nullable=false)
    private User user;

    public Role() {
        this.uuid = UUIDService.uuidString();
        this.name = "dummy_role";
    }

    public Role(User user, String name, String description) {
        this();
        this.user = user;
        this.name = name;
        this.description = description;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    /**
     * in case of User-Roles we use only the name as check
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Role) {
            Role r = (Role) obj;
            if (name.equals(r.name)) {
                return true;
            }
        }
        return false;
    }
}
