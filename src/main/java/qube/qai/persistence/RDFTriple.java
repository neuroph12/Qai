package qube.qai.persistence;

import qube.qai.services.implementation.UUIDService;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by rainbird on 1/30/16.
 */
@Entity
//@Table(name="RdfTripleSet")
public class RDFTriple implements Serializable{

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "subject")
    private String subject;

    @Column(name = "predicate")
    private String predicate;

    @Column(name = "object")
    private String object;

    public RDFTriple() {
        this.uuid = UUIDService.uuidString();
    }

    public RDFTriple(String subject, String predicate, String object) {
        this();
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
