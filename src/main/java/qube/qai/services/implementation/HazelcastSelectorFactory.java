package qube.qai.services.implementation;

import com.hazelcast.core.HazelcastInstance;
import qube.qai.data.SelectionOperator;
import qube.qai.data.selectors.HazelcastSelectionOperator;
import qube.qai.services.SelectorFactoryInterface;

import javax.inject.Inject;

/**
 * Created by rainbird on 11/30/15.
 */
public class HazelcastSelectorFactory<T> implements SelectorFactoryInterface {

    @Inject //@Named("HAZELCAST_CLIENT")
    private HazelcastInstance hazelcastInstance;

    public SelectionOperator buildSelector(String dataSource, String uuid, Object data) {
        SelectionOperator<T> selectionOperator = new HazelcastSelectionOperator<T>(hazelcastInstance, dataSource, uuid);
        return selectionOperator;
    }
}
