package qube.qai.persistence;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by rainbird on 1/30/16.
 */
@Entity
//@Table(name="RdfTripleSet")
public class RDFTriple implements Serializable{

    @Id
    private RDFId id;

    @Column(name = "object")
    private String object;

    public RDFTriple() {
    }

    public RDFTriple(String subject, String predicate, String object) {
        this.id = new RDFId(subject, predicate);
        this.object = object;
    }

    public RDFId getId() {
        return id;
    }

    public void setId(RDFId id) {
        this.id = id;
    }

    public String getSubject() {
        return id.getSubject();
    }

    public void setSubject(String subject) {
        this.id.setSubject(subject);
    }

    public String getPredicate() {
        return id.getPredicate();
    }

    public void setPredicate(String predicate) {
        this.id.setPredicate(predicate);
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }


}
