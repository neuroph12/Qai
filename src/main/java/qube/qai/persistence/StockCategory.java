package qube.qai.persistence;

import qube.qai.services.implementation.UUIDService;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rainbird on 1/13/17.
 */
@Entity
public class StockCategory {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<StockEntity> entities;

    public StockCategory() {
        this.uuid = UUIDService.uuidString();
        this.entities = new HashSet<>();
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
}