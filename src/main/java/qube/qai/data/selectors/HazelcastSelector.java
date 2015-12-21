package qube.qai.data.selectors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import qube.qai.data.Selector;

import javax.inject.Inject;

/**
 * Created by rainbird on 11/19/15.
 */
public class HazelcastSelector<T> implements Selector, HazelcastInstanceAware {

    private String dataSource;

    private String uuid;

    @Inject
    private HazelcastInstance hazelcastInstance;

    public HazelcastSelector(HazelcastInstance hazelcastInstance, String dataSource, String uuid) {
        this.dataSource = dataSource;
        this.uuid = uuid;
        this.hazelcastInstance = hazelcastInstance;
    }

    public HazelcastSelector(String dataSource, String uuid) {
        this.dataSource = dataSource;
        this.uuid = uuid;
    }

    public T getData() {

        IMap<String, T> map = hazelcastInstance.getMap(dataSource);

        return map.get(uuid);
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
