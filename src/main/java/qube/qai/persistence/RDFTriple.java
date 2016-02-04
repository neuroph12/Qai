package qube.qai.persistence;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by rainbird on 1/30/16.
 */
@Entity
public class RDFTriple implements Serializable{

    @Id
    private RDFKey id;

//    private String subject;
//
//    private String predicate;

    private String object;

    public RDFTriple() {
    }

    public RDFTriple(String subject, String predicate, String object) {
        this.id = new RDFKey(subject, predicate);
        this.object = object;
    }

    public RDFKey getId() {
        return id;
    }

    public void setId(RDFKey id) {
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

    @Embeddable
    public static class RDFKey implements Serializable {

        private String subject;

        private String predicate;

        public RDFKey() {
        }

        public RDFKey(String subject, String predicate) {
            this.subject = subject;
            this.predicate = predicate;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RDFKey)) return false;

            RDFKey rdfKey = (RDFKey) o;

            if (!getSubject().equals(rdfKey.getSubject())) return false;
            return getPredicate().equals(rdfKey.getPredicate());

        }

        @Override
        public int hashCode() {
            int result = getSubject().hashCode();
            result = 31 * result + getPredicate().hashCode();
            return result;
        }
    }
}
