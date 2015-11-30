package qube.qai.data;

import java.io.Serializable;

/**
 * Created by rainbird on 11/19/15.
 */
public interface Selector<T> extends Serializable {

    T getData();
}
