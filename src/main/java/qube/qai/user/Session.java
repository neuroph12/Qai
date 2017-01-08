package qube.qai.user;

import qube.qai.services.implementation.UUIDService;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.util.Date;

/**
 * Created by rainbird on 1/8/17.
 */
@Entity
public class Session {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @JoinColumn(name="PARENTID", nullable = false)
    private String userdId;

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

    public String getUserdId() {
        return userdId;
    }

    public void setUserdId(String userdId) {
        this.userdId = userdId;
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
