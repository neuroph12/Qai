package qube.qai.procedure;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import qube.qai.procedure.wikiripper.ChainVisitor;

import java.io.Serializable;

/**
 * Created by rainbird on 11/27/15.
 */
public interface Procedure extends Serializable, Runnable, HazelcastInstanceAware {

    public void run();

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance);

    public Object accept(ChainVisitor visitor, Object data);

    public Object childrenAccept(ChainVisitor visitor, Object data);

}
