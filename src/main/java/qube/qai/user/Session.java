package qube.qai.user;

import qube.qai.services.implementation.UUIDService;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by rainbird on 1/8/17.
 */
@Entity
public class Session {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name="userId", nullable=false)
    private User userId;

    @Column(name = "name")
    private String name;

    @Column(name = "sessionDate")
    private Date sessionDate;

    @Column(name = "active")
    private boolean active;


    public Session() {
        this.uuid = UUIDService.uuidString();
    }

    public Session(String uuid, String name, Date sessionDate) {
        this.uuid = uuid;
        this.name = name;
        this.sessionDate = sessionDate;
    }

    public Session(String name, Date sessionDate) {
        this();
        this.name = name;
        this.sessionDate = sessionDate;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userdId) {
        this.userId = userdId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(Date sessionDate) {
        this.sessionDate = sessionDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
