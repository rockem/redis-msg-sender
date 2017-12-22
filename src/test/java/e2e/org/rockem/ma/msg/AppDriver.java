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

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AppDriver {

    private static final String APP_DOMAIN = "http://localhost:8080";

    private final HttpClient httpClient = HttpClientBuilder.create().build();
    private HttpResponse lastResponse;


    public void start() {
        Application.main();
    }

    public void echoAtTime(String msg, long time) throws IOException {
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
}
