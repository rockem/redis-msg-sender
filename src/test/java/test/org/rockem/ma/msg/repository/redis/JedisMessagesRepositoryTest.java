package test.org.rockem.ma.msg.repository.redis;

import org.junit.Before;
import org.junit.Test;
import org.rockem.ma.msg.Message;
import org.rockem.ma.msg.TimeProvider;
import org.rockem.ma.msg.repository.redis.JedisMessagesRepository;
import redis.clients.jedis.Jedis;

import java.util.Set;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.rockem.ma.msg.repository.redis.JedisMessagesRepository.LOG_KEY;

public class JedisMessagesRepositoryTest {

    private static final long NOW = 111L;

    private final Jedis jedis = new Jedis();
    private TimeProvider timeProvider = mock(TimeProvider.class);
    private final JedisMessagesRepository repository = new JedisMessagesRepository(timeProvider);

    @Before
    public void setUp() throws Exception {
        jedis.flushAll();
        when(timeProvider.now()).thenReturn(NOW);
    }

    @Test
    public void shouldCreateMessageAsEntry() throws Exception {
        repository.save(new Message("lala", 1234));
        assertThat(jedis.spop("1234"), is("lala"));
    }

    @Test
    public void shouldLogMessageOrderedByTime() throws Exception {
        repository.save(new Message("lala", 1234));
        repository.save(new Message("kuku", 123));
        Set<String> mlog = jedis.zrange(LOG_KEY, 0, -1);
        assertThat(mlog.size(), is(2));
        assertThat(mlog.toArray()[0], is("123"));
        assertThat(mlog.toArray()[1], is("1234"));
    }

    @Test
    public void noSavedMessages() throws Exception {
        assertThat(repository.getPendingMessages(), iterableWithSize(0));
    }

    @Test
    public void noRelevantMessagesForNow() throws Exception {
        repository.save(new Message("popov", 150));
        assertThat(repository.getPendingMessages(), iterableWithSize(0));
    }

    @Test
    public void retrievePendingMessages() throws Exception {
        repository.save(new Message("popov", 90));
        repository.save(new Message("popov", 61));
        assertThat(repository.getPendingMessages(), containsInAnyOrder(
                new Message("popov", 90), new Message("popov", 61)));
    }

    @Test
    public void retrieveOnlyUprocessedMessages() throws Exception {
        repository.save(new Message("popov", 90));
        repository.save(new Message("popov", 61));
        fetchMessageFrom(90);
        assertThat(repository.getPendingMessages(), iterableWithSize(1));
        assertThat(repository.getPendingMessages(), not(containsInAnyOrder(new Message("popov", 90))));
    }

    private void fetchMessageFrom(long time) {
        jedis.spop(String.valueOf(time));
    }

    @Test
    public void retrieveMultipleMessagesForTheSameTime() throws Exception {
        repository.save(new Message("kuku", 90));
        repository.save(new Message("popov", 90));
        assertThat(repository.getPendingMessages(), containsInAnyOrder(
                new Message("kuku", 90), new Message("popov", 90)));
    }
}