/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.user;

import org.openrdf.annotations.Iri;
import qube.qai.services.implementation.UUIDService;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static qube.qai.main.QaiConstants.BASE_URL;

/**
 * Created by rainbird on 12/2/15.
 */
@Entity
@Iri(BASE_URL + "User")
public class User implements Serializable {

    @Id
    @Column(name = "uuid")
    @Iri(BASE_URL + "uuid")
    protected String uuid;

    @Column(name = "username", nullable = false, unique = true)
    @Iri(BASE_URL + "username")
    protected String username;

    @Column(name = "password")
    @Iri(BASE_URL + "password")
    protected String password;

    @Column(name = "email")
    @Iri(BASE_URL + "email")
    protected String email;

    @Column(name = "passwordSalt")
    protected String passwordSalt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    protected Set<Session> sessions = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    protected Set<Role> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    protected Set<Permission> permissions = new HashSet<>();

    public User() {
        this.uuid = UUIDService.uuidString();
        this.passwordSalt = UUIDService.uuidString();
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
        role.setUser(this);
        roles.add(role);
    }

    public void addPermission(Permission permission) {
        permission.setUser(this);
        permissions.add(permission);
    }

    public Session createSession() {
        Session session = new Session();
        addSession(session);

        return session;
    }

    /*public static Model userAsModel(User user) {

        Model model = ModelFactory.createDefaultModel();

        String baseUrl = "http://www.qoan.org/data/";

        Resource userResource = model.createResource(baseUrl + "user/" + user.getUuid());
        userResource.addProperty(model.createProperty(baseUrl, "uuid"), user.getUuid());
        userResource.addProperty(model.createProperty(baseUrl, "username"), user.getUsername());
        userResource.addProperty(model.createProperty(baseUrl, "password"), user.getPassword());

        Set<Role> roles = user.getRoles();
        if (roles != null && !roles.isEmpty()) {
            for (Role role : roles) {
                Resource roleResource = model.createResource(baseUrl + "role/" + role.getUuid());
                roleResource.addProperty(model.createProperty(baseUrl, "uuid"), role.getUuid());
                roleResource.addProperty(model.createProperty(baseUrl, "name"), role.getName());
                roleResource.addProperty(model.createProperty(baseUrl, "description"), role.getDescription());
                roleResource.addProperty(model.createProperty(baseUrl, "userUuid"), role.getUser().getUuid());

                userResource.addProperty(model.createProperty(baseUrl, "ruleUuid"), role.getUuid());
            }
        }

        Set<Session> sessions = user.getSessions();
        if (sessions != null && !sessions.isEmpty()) {
            for (Session session : sessions) {
                Resource sessionResource = model.createResource(baseUrl + "session/" + session.getUuid());
                sessionResource.addProperty(model.createProperty(baseUrl, "uuid"), session.getUuid());
                sessionResource.addProperty(model.createProperty(baseUrl, "name"), session.getName());
                sessionResource.addProperty(model.createProperty(baseUrl, "userUuid"), session.getUser().getUuid());

                userResource.addProperty(model.createProperty(baseUrl, "sessionUuid"), session.getUuid());
            }
        }

        return model;
    }*/

    public Set<Role> getRoles() {
        return roles;
    }

    public Set<Session> getSessions() {
        return sessions;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
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
            if (username.equals(u.username) && password.equals(u.password)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        /*String rolesStr = "[";
        if (roles != null && !roles.isEmpty()) {
            for (Role role : roles) {
                rolesStr += role.getName() + " ";
            }
        }
        rolesStr += "] ";
        String permStr = "[";
        if (permissions != null && !permissions.isEmpty()) {
            for (Permission permission : permissions) {
                permStr += permission.getName() + " ";
            }
        }
        permStr += "]";
        return username + " " + rolesStr + permStr;*/
        return String.format("%s [%s]", username, uuid);
    }
}
