package qube.qai.services.implementation;

import com.fasterxml.uuid.Generators;
import junit.framework.TestCase;

import java.util.UUID;

/**
 * Created by rainbird on 12/13/15.
 */
public class TestUUIDGenerator extends TestCase {

    private boolean debug = true;
    /*
    depending on how you generate the UUID you have different fields which are accessible
    this test is in order to demonstrate the differences
     */
    public void testUUIDGenerators() throws Exception {

        int numerOfUUIDsToGenerate = 1;

        log("time based UUIDS:");
        for (int i = 0; i < numerOfUUIDsToGenerate; i++) {
            UUID uuid = Generators.timeBasedGenerator().generate();
            log("Generated: " + uuid.toString());
            log("least significant bits:" + uuid.getLeastSignificantBits());
            log("most significant bits:" + uuid.getMostSignificantBits());
            log("timestamp: " + uuid.timestamp());
            log("clock sequence:" + uuid.clockSequence());
            log("node: " + uuid.node());
            log("version: " + uuid.version());
            log("variant: " + uuid.variant());
        }

        log("name based UUIDS:");
        for (int i = 0; i < numerOfUUIDsToGenerate; i++) {
            String dummyName = "a name @" + i; // + "@" + System.currentTimeMillis();
            UUID uuid = Generators.nameBasedGenerator().generate(dummyName);
            log("name used for generation: " + dummyName);
            log("Generated: " + uuid.toString());
            log("least significant bits:" + uuid.getLeastSignificantBits());
            log("most significant bits:" + uuid.getMostSignificantBits());
            log("version: " + uuid.version());
            log("variant: " + uuid.variant());
            // in name based UUID these fields are not accessible
            //log("timestamp: " + uuid.timestamp());
            //log("clock sequence:" + uuid.clockSequence());
            //log("node: " + uuid.node());
        }
    }

    protected void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
