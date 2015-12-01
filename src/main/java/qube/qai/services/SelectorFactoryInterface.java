package qube.qai.services;

import qube.qai.data.Selector;

/**
 * Created by rainbird on 11/30/15.
 */
public interface SelectorFactoryInterface<T> {

    public Selector<T> buildSelector(String dataSource, String uuid, T data);
}
