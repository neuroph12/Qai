package qube.qai.user;

import qube.qai.services.implementation.UUIDService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by rainbird on 12/2/15.
 */
@Entity
public class User {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    public User() {
        this.uuid = UUIDService.uuidString();
    }

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    /**
     * this class is supposed to be representing the
     * Qoan-users in order to regulate their rights and all
     * for the time being just a dummy class
     * will be implemented when the user rights and all
     * are to be decided
     */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
