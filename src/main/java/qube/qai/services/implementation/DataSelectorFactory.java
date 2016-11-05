package qube.qai.services.implementation;

import qube.qai.data.SelectionOperator;
import qube.qai.data.selectors.DataSelectionOperator;
import qube.qai.services.SelectorFactoryInterface;

/**
 * Created by rainbird on 11/30/15.
 */
public class DataSelectorFactory<T> implements SelectorFactoryInterface<T> {

    public SelectionOperator<T> buildSelector(String dataSource, String uuid, T data) {
        SelectionOperator<T> selectionOperator = new DataSelectionOperator<T>(data);
        return selectionOperator;
    }

}
