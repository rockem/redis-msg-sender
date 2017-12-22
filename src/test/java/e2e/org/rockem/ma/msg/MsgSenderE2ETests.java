package e2e.org.rockem.ma.msg;

import org.junit.BeforeClass;
import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static e2e.org.rockem.ma.msg.AppDriver.at;

public class MsgSenderE2ETests {

    private static final AppDriver app = new AppDriver();

    @BeforeClass
    public static void startApp() throws Exception {
        app.start();
    }

    @Test
    public void failOnRequestWithPastTime() throws Exception {
        app.echoAtTime("kuku", Instant.now(Clock.systemUTC()).minus(5, ChronoUnit.SECONDS).getEpochSecond());
        app.receivedUserError();
    }

    @Test
    public void printAMessageAtRequestedTime() throws Exception {
        long msgTime = Instant.now(Clock.systemUTC()).plus(2, ChronoUnit.SECONDS).getEpochSecond();
        app.echoAtTime("kuku", msgTime);
        app.printedTheMessage("kuku", at(msgTime));
    }
}