package qube.qai.persistence.mapstores;

import net.didion.jwnl.data.Exc;
import qube.qai.main.QaiBaseTestCase;

/**
 * Created by rainbird on 11/19/15.
 */
public class TestMapStore extends QaiBaseTestCase {

    public void testZipFileMapStore() throws Exception {
        // @TODO implement the test to read files from a zip-file

        ZipFileMapStore mapStore = new ZipFileMapStore();




        fail("test not yet implemented!!!");
    }

    public void testHsqlDBMapStore() throws Exception {
        // @TODO implement the test to read files from a zip-file

        HqslDBMapStore mapStore = new HqslDBMapStore();

        fail("test not yet implemented!!!");
    }
}