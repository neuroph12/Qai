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

    void run();

    void setHazelcastInstance(HazelcastInstance hazelcastInstance);

    Object accept(ProcedureVisitor visitor, Object data);

    Object childrenAccept(ProcedureVisitor visitor, Object data);

    boolean haveChildren();

    boolean hasExecuted();

    User getUser();

    String getName();

    String getUuid();

    String getDescription();

    double getProgressPercentage();

    Arguments getArguments();
}
