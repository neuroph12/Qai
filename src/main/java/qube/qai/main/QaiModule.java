package qube.qai.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.services.ProcedureSource;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.SelectorFactoryInterface;
import qube.qai.services.UUIDServiceInterface;
import qube.qai.services.implementation.*;

/**
 * Created by rainbird on 11/9/15.
 */
public class QaiModule extends AbstractModule {

    private Logger logger = LoggerFactory.getLogger("Qai-Module");

    private boolean debug = true;

    private static String wiktionaryDirectory = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";

    private static String wiktionaryZipFileName = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";

    private static String wikipediaDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";

    private static String wikipediaZipFileName = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";

    @Override
    protected void configure() {

        logger("Guice initialization called- binding services");

        // search service
        //bind(SearchServiceInterface.class).to(WikiSearchService.class);

        // UUIDService
        bind(UUIDServiceInterface.class).to(UUIDService.class);

        // ProcedureSource
        //bind(ProcedureSource.class).to(CachedProcedureSourceService.class);
    }

    @Provides
    ProcedureSource provideProcedureSourceInterface() {
        return CachedProcedureSourceService.getInstance();
    }

    @Provides
    SelectorFactoryInterface provideSelectorFactoryInterface() {
        // @TODO this is supposed to return hazelcast-data-selector-factory instance instead
        SelectorFactoryInterface selectorfactory = new DataSelectorFactory();

        return selectorfactory;
    }

    @Provides @Named("Wiktionary_en")
    SearchServiceInterface provideWiktionarySearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(wiktionaryDirectory, wiktionaryZipFileName);

        return searchService;
    }

    @Provides @Named("Wikipedia_en")
    SearchServiceInterface provideWikipediaSearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(wikipediaDirectory, wikipediaZipFileName);

        return searchService;
    }

    private void logger(String message) {
        if (debug) {
            //System.out.println(message);
            logger.info(message);
        }
    }
}
