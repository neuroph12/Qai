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
import qube.qai.services.implementation.DataSelectorFactory;
import qube.qai.services.implementation.ProcedureSourceService;
import qube.qai.services.implementation.UUIDService;
import qube.qai.services.implementation.WikiSearchService;

/**
 * Created by rainbird on 11/19/15.
 */
public class QaiTestModule extends AbstractModule {

    private Logger logger = LoggerFactory.getLogger("Qai-Module");

    private static String wikipediaDirectory = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.index";

    private static String wikipediaZipFileName = "/media/rainbird/ALEPH/wiki-archives/wikipedia_en.zip";

    private static String wiktionaryDirectory = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.index";

    private static String wiktionaryZipFileName = "/media/rainbird/ALEPH/wiki-archives/wiktionary_en.zip";


    @Override
    protected void configure() {

        // UUIDService
        bind(UUIDServiceInterface.class).to(UUIDService.class);

        // ProcedureSource
        bind(ProcedureSource.class).to(ProcedureSourceService.class);

    }

    @Provides
    SelectorFactoryInterface provideSelectorFactoryInterface() {
        SelectorFactoryInterface selectorfactory = new DataSelectorFactory();

        return selectorfactory;
    }

    @Provides @Named("Wiktionary_en")
    SearchServiceInterface provideWiktionarySearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(wiktionaryDirectory, wiktionaryZipFileName);

        return searchService;
    }

    @Provides
    @Named("Wikipedia_en")
    SearchServiceInterface provideWikipediaSearchServiceInterface() {
        SearchServiceInterface searchService = new WikiSearchService(wikipediaDirectory, wikipediaZipFileName);

        return searchService;
    }
}
