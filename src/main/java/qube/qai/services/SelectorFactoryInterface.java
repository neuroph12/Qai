package qube.qai.services;

import qube.qai.data.SelectionOperator;

/**
 * Created by rainbird on 11/30/15.
 */
public interface SelectorFactoryInterface<T> {

    SelectionOperator<T> buildSelector(String dataSource, String uuid, T data);
}
