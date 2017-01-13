package qube.qai.data.selectors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import qube.qai.data.SelectionOperator;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rainbird on 11/19/15.
 */
public class HazelcastSelectionOperator<T> implements SelectionOperator, HazelcastInstanceAware {

    private String dataSource;

    private String uuid;

    private Object idObject;

    @Inject @Named("HAZELCAST_CLIENT")
    private HazelcastInstance hazelcastInstance;

    public HazelcastSelectionOperator(HazelcastInstance hazelcastInstance, String dataSource, String uuid) {
        this.dataSource = dataSource;
        this.uuid = uuid;
        this.hazelcastInstance = hazelcastInstance;
    }

    public HazelcastSelectionOperator(String dataSource, Object idObject) {
        this.dataSource = dataSource;
        this.idObject = idObject;
        this.hazelcastInstance = hazelcastInstance;
    }

    public HazelcastSelectionOperator(String dataSource, String uuid) {
        this.dataSource = dataSource;
        this.uuid = uuid;
    }

    public T getData() {

        IMap<String, T> map = hazelcastInstance.getMap(dataSource);

        if (uuid != null) {
            return map.get(uuid);
        } else {
            return map.get(idObject);
        }
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

    public Object getIdObject() {
        return idObject;
    }

    public void setIdObject(Object idObject) {
        this.idObject = idObject;
    }

    public HazelcastInstance getHazelcastInstance() {
        return hazelcastInstance;
    }

    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }
}
