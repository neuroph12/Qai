package qube.qai.data.selectors;

import qube.qai.data.SelectionOperator;

/**
 * Created by rainbird on 11/27/15.
 */
public class DataSelectionOperator<T> implements SelectionOperator {

    private T data;

    public DataSelectionOperator() {
    }

    public DataSelectionOperator(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
