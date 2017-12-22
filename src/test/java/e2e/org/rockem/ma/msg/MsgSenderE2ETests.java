package e2e.org.rockem.ma.msg;

import org.junit.Test;

import java.util.Date;

public class MsgSenderE2ETests {

    private static final int SECOND = 1000;

    private final AppDriver app = new AppDriver();

    @Test
    public void failOnRequestWithPastTime() throws Exception {
        app.start();
        app.echoAtTime("kuku", new Date().getTime() - SECOND);
        app.receivedUserError();
    }
}