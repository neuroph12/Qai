package qube.qai.services.implementation;

import com.fasterxml.uuid.Generators;
import qube.qai.services.UUIDServiceInterface;

import java.util.UUID;

/**
 * Created by rainbird on 11/2/15.
 */
public class UUIDService implements UUIDServiceInterface {

    public UUID createUUID() {
        return Generators.timeBasedGenerator().generate();
    }

    public String createUUIDString() {
        return Generators.timeBasedGenerator().generate().toString();
    }
}
