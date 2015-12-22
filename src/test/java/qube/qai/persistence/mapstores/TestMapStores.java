package qube.qai.persistence.mapstores;

import qube.qai.main.QaiBaseTestCase;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.WikiArticle;
import qube.qai.procedure.Procedure;
import qube.qai.services.ProcedureSourceInterface;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

/**
 * Created by rainbird on 11/19/15.
 */
public class TestMapStores extends QaiBaseTestCase {

    @Inject
    private ProcedureSourceInterface procedureSource;

    @Inject @Named("Wikipedia_en")
    private SearchServiceInterface searchService;

    private String testDirectory = "./test/procedures/";

    private String testWikiArchive = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";

    /**
     * this is mainly to test how reading from the tarballs will be
     */
    /*
    private String testZipFile = "/media/rainbird/ALEPH/wiki-data/enwiki-20121104-local-media-1.tar";

    public void testZipFileMapStore() throws Exception {

        TarballMapStore mapStore = new TarballMapStore(testZipFile);
        Iterable<String> names = mapStore.loadAllKeys();
        for (String name : names) {
            logger.info("as in zip file: '" + name + "'");
        }

        // while we are at it, we can experiment with tar-balls
        // which are supposed to be the wikipedia resource dumps
        // the images and all, i guess...
    }*/
    // @TODO these two are every similar- i should perhaps delete one of them
    /*public void testWikiTarballMapStore() throws Exception {

        WikiTarballMapStore mapStore = new WikiTarballMapStore();

        fail("implementation missing");
    }*/

    public void testWikiArticleMapStore() throws Exception {

        // well, this is all there is to it really...
        String[] someWikiArticles = {"mickey mouse", "mouse", "crow", "stock market"};

        WikiArticleMapStore mapStore = new WikiArticleMapStore(testWikiArchive);
        for (String name : someWikiArticles) {
            Collection<SearchResult> results = searchService.searchInputString(name, "title", 100);
            SearchResult result = results.iterator().next();
            logger.info("searching result: " + result.getTitle() + " with " + result.getRelevance() + " %relevance");
            WikiArticle article = mapStore.load(result.getFilename());
            assertNotNull("there has to be something", article);
        }

    }

    /**
     * in this case, we will be storing the Stock-Quotes in HsqlDb
     * i am not sure i really want to keep a copy of them, to be honest
     * but for completeness' sake, i will be storing them away in HsqlDb
     * so that they can be collected with sql-statements as well, if need be
     * @throws Exception
     */
    public void testHsqlDBMapStore() throws Exception {

        // the fields in class will have to be injected
        HqslDBMapStore mapStore = new HqslDBMapStore();
        injector.injectMembers(mapStore);

        int number = 100;
        Map<String, StockEntity> entityMap = new HashMap<String, StockEntity>();
        for (int i = 0; i < number; i++) {
            String name = "entity(" + i + ")";
            StockEntity entity = createEntity(name);
            String uuid = entity.getUuid();
            mapStore.store(uuid, entity);
            entityMap.put(uuid, entity);
        }

        // in this case the map-store should be returning all keys
        Iterable<String> storedKeys = mapStore.loadAllKeys();
        assertNotNull("stored keys may not be null", storedKeys);

        // now read them back from database
        for (String uuid : entityMap.keySet()) {
            StockEntity cachedEntity = entityMap.get(uuid);
            StockEntity storedEntity = mapStore.load(uuid);
            assertNotNull("there has to be an entity", storedEntity);
            assertTrue("entities have to be equal", cachedEntity.equals(storedEntity));
        }

        // when we are done we delete the things as well, just to keep things managable
        for (String uuid : entityMap.keySet()) {
            mapStore.delete(uuid);
        }
    }

    /**
     * in this case we will be storing away the procedures on
     * file-system... in xml-format, with x-stream, so that there are
     * no hassles because of serializable
     * @throws Exception
     */
    public void testDirectoryMapStore() throws Exception {

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
                logger.info("uuid " + uuid + " was not found on the list of keys which are available on map-store");
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
            assertNotNull("procedure cannot be null", procedure);
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
    }

    /**
     * creates a silly StockEntity
     * @param name
     * @return
     */
    public static StockEntity createEntity(String name) {
        StockEntity entity = new StockEntity();
        entity.setName(name);
        entity.setAddress("address of " + name);
        entity.setGicsSector("gicsSector of " + name);
        entity.setGicsSubIndustry("gicsSubIndustry of " + name);
        entity.setSecurity("security of " + name);
        return entity;
    }
}