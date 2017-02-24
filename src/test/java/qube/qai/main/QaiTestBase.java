package qube.qai.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rainbird on 11/19/15.
 */
public class QaiTestBase extends TestCase {

    protected Logger logger = LoggerFactory.getLogger("QaiTest");

    protected boolean debug = true;

    protected Injector injector;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        injector = Guice.createInjector(new QaiTestModule());
        injector.injectMembers(this);
    }

    public static void main(String[] params) {
        String[] tests = {QaiTestBase.class.getName()};
        TestRunner.main(tests);
    }

    protected void log(String message) {
        if (debug) {
            //System.out.println(message);
            logger.info(message);
        }
    }

}
