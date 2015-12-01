package qube.qai.services.implementation;

import qube.qai.data.Selector;
import qube.qai.data.selectors.DataSelector;
import qube.qai.services.SelectorFactoryInterface;

/**
 * Created by rainbird on 11/30/15.
 */
public class DataSelectorFactory<T> implements SelectorFactoryInterface<T> {

    public Selector<T> buildSelector(String dataSource, String uuid, T data) {
        Selector<T> selector = new DataSelector<T>(data);
        return selector;
    }

}
