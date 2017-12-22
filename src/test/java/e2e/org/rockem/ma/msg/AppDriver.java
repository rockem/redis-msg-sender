package e2e.org.rockem.ma.msg;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.rockem.ma.msg.Application;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.TimerTask;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AppDriver {

    private static final String APP_DOMAIN = "http://localhost:8080";

    private final HttpClient httpClient = HttpClientBuilder.create().build();
    private HttpResponse lastResponse;
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();


    public void start() {
        Application.main();
        System.setOut(new PrintStream(baos));
    }

    public void echoAtTime(String msg, long time) throws IOException {
        baos.reset();
        HttpPost post = new HttpPost(APP_DOMAIN + "/echoes");
        post.setEntity(new StringEntity(createMsgRequestFrom(msg, time), ContentType.APPLICATION_JSON));
        lastResponse = httpClient.execute(post);
    }

    private String createMsgRequestFrom(String msg, long time) {
        return new Gson().toJson(ImmutableMap.of("message", msg, "time", time));
    }

    public void receivedUserError() {
        assertThat(lastResponse.getStatusLine().getStatusCode(), is(400));
    }

    public void printedTheMessage(String message, Time at) {
        waitUntil(at.time());
        assertPrinterMessage(message, at.time());
    }

    private void assertPrinterMessage(String message, long at) {
        String stdOut = baos.toString();
        assertFalse(stdOut.isEmpty());
        String[] output = stdOut.split(":");assertTrue(utcTimeOf(Long.parseLong(output[0])).isAfter(utcTimeOf(at)));
        assertThat(output[1], is(message));
    }

    private LocalDateTime utcTimeOf(long s) {
        return LocalDateTime.ofEpochSecond(s, 0, ZoneOffset.UTC);
    }

    public void waitUntil(long seconds) {
        long timeToWait = Instant.now(Clock.systemUTC()).minus(seconds, ChronoUnit.SECONDS).toEpochMilli() + 2000;
        final Object o = new Object();
        TimerTask tt = new TimerTask() {
            public void run() {
                synchronized (o) {
                    o.notify();
                }
            }
        };
        Timer t = new Timer();
        t.schedule(tt, timeToWait);
        synchronized(o) {
            try {
                o.wait();
            } catch (InterruptedException ie) {}
        }
        t.cancel();
        t.purge();
    }


    public static Time at(long time) {
        return () -> time;
    }

    public interface Time {

        long time();
    }
}
