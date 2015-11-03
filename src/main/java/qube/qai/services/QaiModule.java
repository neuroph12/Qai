package qube.qai.services;

import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.services.implementation.UUIDService;

/**
 * Created by rainbird on 11/2/15.
 */
public class QaiModule extends AbstractModule {

    private Logger logger = LoggerFactory.getLogger(QaiModule.class);

    @Override
    protected void configure() {
        logger.info("Guice initialization called- binding services");

        bind(UUIDServiceInterface.class).to(UUIDService.class);
    }
}
