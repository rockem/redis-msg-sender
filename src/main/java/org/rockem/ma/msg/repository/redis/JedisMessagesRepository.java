package org.rockem.ma.msg.repository.redis;

import org.rockem.ma.msg.Message;
import org.rockem.ma.msg.TimeProvider;
import org.rockem.ma.msg.repository.MessagesRepository;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class JedisMessagesRepository implements MessagesRepository {

    public static final String LOG_KEY = "mlog";

    private final Jedis jedis = new Jedis();
    private final TimeProvider timeProvider;

    public JedisMessagesRepository(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public void save(Message message) {
        jedis.sadd(String.valueOf(message.getTime()), message.getMessage());
        jedis.zadd(LOG_KEY, (double) message.getTime(), String.valueOf(message.getTime()));
    }

    @Override
    public Iterable<Message> getPendingMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        Set<String> mlog = jedis.zrangeByScore(LOG_KEY, 0, timeProvider.now());
        mlog.forEach(l -> messages.addAll(allMessagesIn(l)));
        return messages;
    }

    private List<Message> allMessagesIn(String time) {
        List<Message> messages = new ArrayList<>();
        String m;
        while((m = jedis.spop(time)) != null) {
            messages.add(new Message(m, Long.valueOf(time)));
        }
        return messages;
    }
}
