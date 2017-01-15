package qube.qai.persistence.mapstores;

import com.google.inject.Injector;
import junit.framework.TestCase;
import qube.qai.main.QaiTestServerModule;
import qube.qai.user.Session;
import qube.qai.user.User;

import java.util.Date;

/**
 * Created by rainbird on 1/8/17.
 * @Deprecated class is to be replaced by DatabaseMapStore
 */
@Deprecated
public class TestSessionMapStore extends TestCase {

    public void testSessionmapStore() throws Exception {

        Injector injector = QaiTestServerModule.initUsersInjector();

        SessionMapStore mapStore = new SessionMapStore();
        injector.injectMembers(mapStore);

        User user = TestUserMapStore.createUser();
        Session session = new Session(TestUserMapStore.randomWord(10), new Date());
        session.setUserId(user);

        mapStore.store(session.getUuid(), session);

        Session foundSession = mapStore.load(session.getUuid());
        assertNotNull(foundSession);
        assertTrue(session.equals(foundSession));

        mapStore.delete(session.getUuid());

        Session lostSession = mapStore.load(session.getUuid());
        assertTrue(lostSession == null);
    }
}
