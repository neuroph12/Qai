package qube.qai.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by rainbird on 5/30/16.
 */
@Embeddable
public class RDFId implements Serializable {

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "predicate", nullable = false)
    private String predicate;

    public RDFId() {
    }

    public RDFId(String subject, String predicate) {
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
        if (!(o instanceof RDFId)) return false;

        RDFId rdfId = (RDFId) o;

        if (getSubject() != null ? !getSubject().equals(rdfId.getSubject()) : rdfId.getSubject() != null) return false;
        return getPredicate() != null ? getPredicate().equals(rdfId.getPredicate()) : rdfId.getPredicate() == null;

    }

    @Override
    public int hashCode() {
        int result = getSubject() != null ? getSubject().hashCode() : 0;
        result = 31 * result + (getPredicate() != null ? getPredicate().hashCode() : 0);
        return result;
    }
}
