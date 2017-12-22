package e2e.org.rockem.ma.msg;

import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

public class MsgSenderE2ETests {

    private static final int SECOND = 1000;

    private final AppDriver app = new AppDriver();

    @Test
    public void failOnRequestWithPastTime() throws Exception {
        app.start();
        app.echoAtTime("kuku", LocalDateTime.now().minus(1, ChronoUnit.SECONDS).toEpochSecond(ZoneOffset.UTC));
        app.receivedUserError();
    }

}