package qube.qai.data.selectors;

import qube.qai.data.Selector;

/**
 * Created by rainbird on 11/27/15.
 */
public class DataSelector<T> implements Selector {

    private T data;

    public DataSelector() {
    }

    public DataSelector(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
