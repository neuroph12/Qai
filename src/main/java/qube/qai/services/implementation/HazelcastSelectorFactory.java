package qube.qai.services.implementation;

import com.hazelcast.core.HazelcastInstance;
import qube.qai.data.Selector;
import qube.qai.services.SelectorFactoryInterface;

import javax.inject.Inject;

/**
 * Created by rainbird on 11/30/15.
 */
public class HazelcastSelectorFactory<T> implements SelectorFactoryInterface {

    @Inject
    private HazelcastInstance hazelcastInstance;

    public Selector buildSelector(String dataSource, String uuid, Object data) {
        return null;
    }
}
