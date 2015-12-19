package qube.qai.persistence.mapstores;

import qube.qai.main.QaiBaseTestCase;
import qube.qai.procedure.Procedure;
import qube.qai.services.ProcedureSource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rainbird on 11/19/15.
 */
public class TestMapStore extends QaiBaseTestCase {

    @Inject
    private ProcedureSource procedureSource;

    private String testDirectory = "./test/procedures/";
/*
    public void testZipFileMapStore() throws Exception {

        ZipFileMapStore mapStore = new ZipFileMapStore();

        // while we are at it, we can experiment with tar-balls
        // which are supposed to be the wikipedia resource dumps
        // the images and all, i guess...


    }
*/


    /**
     * in this case, we will be storing the Stock-Quotes in HsqlDb
     * i am not sure i really want to keep a copy of them, to be honest
     * but for completeness' sake, i will be storing them away in HsqlDb
     * so that they can be collected with sql-statements as well, if need be
     * @throws Exception
     */
    /*public void testHsqlDBMapStore() throws Exception {
        // @TODO implement the test to read files from a zip-file
        HqslDBMapStore mapStore = new HqslDBMapStore();

        fail("test not yet implemented!!!");
    }*/


    /**
     * in this case we will be storing away the procedures on
     * file-system... in xml-format, with x-stream, so that there are
     * no hassles because of serializable
     * @throws Exception
     */
    public void testDirectorymapStore() throws Exception {

        // begin with creating the thing
        DirectoryMapStore mapStore = new DirectoryMapStore(testDirectory);

        String[] names = procedureSource.getProcedureNames();
        List<String> uuidList = new ArrayList<String>();
        Map<String, Procedure> procedures = new HashMap<String, Procedure>();
        for (String name : names) {
            Procedure procedure = procedureSource.getProcedureWithName(name);
            String uuid = procedure.getUuid();
            procedures.put(uuid, procedure);
            uuidList.add(uuid);
            logger.info("storing procedure with uuid: " + uuid);
            mapStore.store(uuid, procedure);
        }

        Iterable<String> keys = mapStore.loadAllKeys();
        for (String uuid : uuidList) {
            if (procedures.containsKey(uuid)) {
                continue;
            }
            Procedure procedure = procedures.get(uuid);
            logger.info("procedure: " + procedure.getName() +" with uuid: " + uuid + " has not been written on file-system");
        }

        logger.info("writing was easy, and is done. now comes reading that was written:");
        // now read the things back
        for (String uuid : uuidList) {
            logger.info("loading procedure with uuid: " + uuid);
            Procedure procedure = mapStore.load(uuid);
            //assertNotNull("procedure cannot be null", procedure);
            if (procedure == null) {
                logger.info("loading procedure with uuid: " + uuid + " has failed");
            } else {
                logger.info("procedure: " + procedure.getName() + " was loaded alright");
            }
        }

        // we are almost there, we now delete the things
        for (String uuid : uuidList) {
            logger.info("deleting procedure with uuid: " + uuid);
            mapStore.delete(uuid);
        }

        // as last check we ask for what is left- has to be an empty list
//        Iterable<String> keys = mapStore.loadAllKeys();
//        assertTrue(!keys.iterator().hasNext());
    }

}
