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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Session> sessions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Role> roles = new HashSet<>();

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
        if (session.getUser() != null || !uuid.equals(session.getUser())) {
            session.setUser(this);
        }
        sessions.add(session);
    }

    public void addRole(Role role) {
        if (role.getUser() != null || !uuid.equals(role.getUser())) {
            role.setUser(this);
        }
        roles.add(role);
    }

    public Session createSession() {
        Session session = new Session();
        addSession(session);

        return session;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    public void setSessions(Set<Session> sessions) {
        this.sessions = sessions;
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

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User u = (User) obj;
            if (uuid.equals(u.uuid)) {
                return true;
            }
        }
        return false;
    }
}
