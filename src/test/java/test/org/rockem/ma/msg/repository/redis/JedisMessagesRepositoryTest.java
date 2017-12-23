package test.org.rockem.ma.msg.repository.redis;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.rockem.ma.msg.Message;
import org.rockem.ma.msg.time.TimeProvider;
import org.rockem.ma.msg.repository.PendingMessages;
import org.rockem.ma.msg.repository.redis.JedisMessagesRepository;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
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
        assertThat(toList(repository.getPendingMessages()), empty());
    }

    private List<Message> toList(PendingMessages pendingMessages) {
        List<Message> messages = new ArrayList<>();
        pendingMessages.forEach(messages::add);
        return messages;
    }

    @Test
    public void noRelevantMessagesForNow() throws Exception {
        repository.save(new Message("popov", 150));
        assertThat(toList(repository.getPendingMessages()), empty());
    }

    @Test
    public void retrievePendingMessages() throws Exception {
        repository.save(new Message("popov", 90));
        repository.save(new Message("popov", 61));
        assertThat(toList(repository.getPendingMessages()), hasItems(
                new Message("popov", 90), new Message("popov", 61)));
    }

    @Test
    public void retrieveOnlyUnprocessedMessages() throws Exception {
        repository.save(new Message("kuku", 90));
        repository.save(new Message("popov", 61));
        fetchMessageFrom(90);
        List<Message> messages = toList(repository.getPendingMessages());
        assertThat(messages, hasSize(1));
        assertThat(messages, hasItems(new Message("popov", 61)));
    }

    private void fetchMessageFrom(long time) {
        jedis.spop(String.valueOf(time));
    }

    @Test
    public void retrieveMultipleMessagesForTheSameTime() throws Exception {
        repository.save(new Message("kuku", 90));
        repository.save(new Message("popov", 90));
        assertThat(toList(repository.getPendingMessages()), hasItems(
                new Message("kuku", 90), new Message("popov", 90)));
    }

    @Test
    public void skipOnJustProcessedMessages() throws Exception {
        repository.save(new Message("popov", 33));
        PendingMessages messages = repository.getPendingMessages();
        fetchMessageFrom(33);
        assertThat(toList(messages), empty());
    }

    @Test
    public void deleteLogEntryWhenNoMessagesForTime() throws Exception {
        repository.save(new Message("popov", 33));
        toList(repository.getPendingMessages());
        assertThat(jedis.zrangeByScore(LOG_KEY, 33, 33), empty());
    }

    /**
     * This feature can create an infinite loop problem and therefore has been removed
     */
    @Ignore
    @Test
    public void restoreMsgIfIFailedToProcessIt() throws Exception {
        repository.save(new Message("popov", 33));
        repository.getPendingMessages().forEach(m -> {
            throw new RuntimeException();
        });
        assertThat(jedis.spop("33"), is("popov"));
    }

}