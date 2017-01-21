package qube.qai.persistence;

import junit.framework.TestCase;

/**
 * Created by rainbird on 1/20/17.
 */
public class TestModelStore extends TestCase {

    public void testModelStore() throws Exception {

        ModelStore modelStore = new ModelStore("./test/dummy.model.directory");
        modelStore.init();

    }
}
