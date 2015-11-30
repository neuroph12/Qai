package qube.qai.procedure;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import qube.qai.data.Arguments;
import qube.qai.services.implementation.UUIDService;

import java.util.Collection;

/**
 * Created by rainbird on 11/27/15.
 */
public abstract class ProcedureChain extends BaseProcedure {

    protected String uuid;

    protected String name;

    protected ProcedureChain parent;

    protected Collection<ProcedureChain> children;

    protected Arguments arguments;

    public ProcedureChain() {
        name = "AbstractProcedure";
        uuid = UUIDService.uuidString();
    }

    public ProcedureChain(String name) {
        this();
        this.name = name;
    }

    public ProcedureChain(String name, ProcedureChain parent) {
        this(name);
        this.parent = parent;
    }

    public ProcedureChain(ProcedureChain parent) {
        this();
        this.parent = parent;
    }

    /**
     * returns the first child with the name- names are not unique
     * if you need the unique child, use uuid instead
     * @param name
     * @return
     */
    public ProcedureChain getChild(String name) {
        ProcedureChain child = null;
        for (ProcedureChain ch : children) {
            if (name.equals(ch.name)) {
                child = ch;
                break;
            }
        }
        return child;
    }

    /**
     * returns the child with the uuid
     * @param uuid
     * @return
     */
    public ProcedureChain getChildWithUUID(String uuid) {
        ProcedureChain child = null;
        for (ProcedureChain ch : children) {
            if (uuid.equals(ch.uuid)) {
                child = ch;
                break;
            }
        }
        return child;
    }

    public void addChild(ProcedureChain child) {
        child.setParent(this);
        children.add(child);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(uuid.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProcedureChain) {
            ProcedureChain other = (ProcedureChain) obj;
            new EqualsBuilder().append(uuid, other.uuid).isEquals();
        }
        return false;
    }



    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Arguments getArguments() {
        return arguments;
    }

    @Override
    public void setArguments(Arguments arguments) {
        this.arguments = arguments;
    }

    public ProcedureChain getParent() {
        return parent;
    }

    public void setParent(ProcedureChain parent) {
        this.parent = parent;
    }

    public Collection<ProcedureChain> getChildren() {
        return children;
    }

    public void setChildren(Collection<ProcedureChain> children) {
        this.children = children;
    }
}
