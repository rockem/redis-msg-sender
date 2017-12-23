package e2e.org.rockem.ma.msg;

import e2e.org.rockem.ma.msg.support.AppDriver;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static e2e.org.rockem.ma.msg.support.AppDriver.at;

public class MsgSenderE2ETests {

    private static final AppDriver app = new AppDriver();

    @BeforeClass
    public static void startApp() throws Exception {
        app.start();
    }

    @Test
    public void failOnRequestWithPastTime() throws Exception {
        app.echoAtTime("kuku", fiveSecondsBeforeNow());
        app.receivedUserError();
    }

    private long fiveSecondsBeforeNow() {
        return Instant.now(Clock.systemUTC()).minus(5, ChronoUnit.SECONDS).getEpochSecond();
    }

    @Test
    public void printAMessageAtRequestedTime() throws Exception {
        long msgTime = twoSecondsFromNow();
        app.echoAtTime("kuku", msgTime);
        app.printedTheMessage("kuku", at(msgTime));
    }

    private long twoSecondsFromNow() {
        return Instant.now(Clock.systemUTC()).plus(2, ChronoUnit.SECONDS).getEpochSecond();
    }
}