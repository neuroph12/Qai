package qube.qai.data;

/**
 * Created by rainbird on 12/27/15.
 */
public interface AcceptsVisitors {

    public Object accept(DataVisitor visitor, Object data);
}
