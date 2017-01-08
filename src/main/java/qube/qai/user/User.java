package qube.qai.user;

import qube.qai.services.implementation.UUIDService;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "parent")
    private Set<Session> userSessions = new HashSet<>();

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

    public void addSession(Session session) {
        if (session.getUserdId() != null || !uuid.equals(session.getUserdId())) {
            session.setUserdId(uuid);
        }
        userSessions.add(session);
    }

    public Session createSession() {
        Session session = new Session();
        addSession(session);

        return session;
    }

    public Set<Session> getUserSessions() {
        return userSessions;
    }

    public void setUserSessions(Set<Session> userSessions) {
        this.userSessions = userSessions;
    }

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
