package qube.qai.persistence.mapstores;

import com.google.inject.Injector;
import junit.framework.TestCase;
import qube.qai.main.QaiTestServerModule;
import qube.qai.user.Session;
import qube.qai.user.User;

import java.util.Random;

/**
 * Created by rainbird on 1/8/17.
 * @Deprecated class is to be replaced by DatabaseMapStore
 */
@Deprecated
public class TestUserMapStore extends TestCase {

    public void testUserMapStore() throws Exception {

        // this way i can use the injector even when it is active for other tests
        Injector injector = QaiTestServerModule.initUsersInjector();

        UserMapStore mapStore = new UserMapStore();
        injector.injectMembers(mapStore);

        User user = createUser();
        Session session = user.createSession();

        mapStore.store(user.getUuid(), user);

        User readUser = mapStore.load(user.getUuid());
        assertNotNull(readUser);
        assertTrue(user.equals(readUser));
        assertTrue(!user.getSessions().isEmpty());
        Session readSession = readUser.getSessions().iterator().next();
        assertTrue(session.equals(readSession));
        mapStore.delete(user.getUuid());

        User lostUser = mapStore.load(user.getUuid());
        assertTrue(lostUser == null);

    }

    public static User createUser() {
        Random random = new Random();
        String username = randomWord(1 + random.nextInt(11));
        String password = randomWord(1 + random.nextInt(8));
        User user = new User(username, password);
        return user;
    }

    public static String randomWord(int length) {
        Random random = new Random();
        StringBuilder word = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            word.append((char)('a' + random.nextInt(26)));
        }

        return word.toString();
    }
}
