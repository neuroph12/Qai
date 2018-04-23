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

import static qube.qai.main.QaiConstants.BASE_URL;

@Entity
@Iri(BASE_URL + "Permission")
public class Permission implements Serializable, org.apache.shiro.authz.Permission {

    @Id
    @Column(name = "uuid")
    @Iri(BASE_URL + "uuid")
    protected String uuid;

    @Column(name = "name")
    @Iri(BASE_URL + "name")
    protected String name;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user", nullable = false)
    @Iri(BASE_URL + "user")
    private User user;

    public Permission() {
        makeUUID();
    }

    private void makeUUID() {
        this.uuid = UUIDService.uuidString();
    }

    public Permission(String name) {
        this();
        this.name = name;
    }

    @Override
    public boolean implies(org.apache.shiro.authz.Permission p) {
        String other = ((Permission) p).getName();
        return this.name.equalsIgnoreCase(other);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        if (uuid == null) {
            makeUUID();
        }
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Permission) {
            Permission p = (Permission) obj;
            if (name != null && name.equals(p.name)) {
                return true;
            }
        }
        return false;
    }
}
