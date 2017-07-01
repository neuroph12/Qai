/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
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

package qube.qai.persistence;

import qube.qai.services.implementation.UUIDService;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rainbird on 1/13/17.
 */
@Entity
public class StockGroup implements Serializable {

    @Id
    @thewebsemantic.Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<StockEntity> entities;

    public StockGroup() {
        this.uuid = UUIDService.uuidString();
        this.entities = new HashSet<>();
    }

    public StockGroup(String name) {
        this();
        this.name = name;
    }

    public void addStockEntity(StockEntity entity) {
        entities.add(entity);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<StockEntity> getEntities() {
        return entities;
    }

    public void setEntities(Set<StockEntity> entities) {
        this.entities = entities;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StockGroup) {
            StockGroup s = (StockGroup) obj;
            if (name.equals(s.name)) {
                return true;
            }
        }
        return false;
    }
}
