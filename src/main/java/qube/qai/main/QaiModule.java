package qube.qai.main;

import com.google.inject.AbstractModule;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.UUIDServiceInterface;
import qube.qai.services.implementation.UUIDService;
import qube.qai.services.implementation.WikiSearchService;

/**
 * Created by rainbird on 11/9/15.
 */
public class QaiModule extends AbstractModule {

    private boolean debug = true;

    @Override
    protected void configure() {

        logger("Guice initialization called- binding services");

        // search service
        bind(SearchServiceInterface.class).to(WikiSearchService.class);

        // UUIDService
        bind(UUIDServiceInterface.class).to(UUIDService.class);
    }

    private void logger(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
