package qube.qai.procedure;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import qube.qai.data.Arguments;
import qube.qai.user.User;

import java.io.Serializable;

/**
 * Created by rainbird on 11/27/15.
 */
public interface Procedure extends Serializable, Runnable, HazelcastInstanceAware {

    public void run();

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance);

    public Object accept(ProcedureVisitor visitor, Object data);

    public Object childrenAccept(ProcedureVisitor visitor, Object data);

    public boolean haveChildren();

    public User getUser();

    public String getName();

    public String getUuid();

    public String getDescription();

    public double getProgressPercentage();

    public Arguments getArguments();
}
