package org.rockem.ma.msg.repository.redis;

import org.rockem.ma.msg.Message;
import org.rockem.ma.msg.TimeProvider;
import org.rockem.ma.msg.repository.MessagesRepository;
import org.rockem.ma.msg.repository.PendingMessages;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.function.Consumer;

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
    public PendingMessages getPendingMessages() {
        Set<String> mlog = jedis.zrangeByScore(LOG_KEY, 0, timeProvider.now());
        return new RedisPendingMessages(mlog);
    }

    public class RedisPendingMessages implements PendingMessages {

        private final Set<String> keys;

        public RedisPendingMessages(Set<String> keys) {
            this.keys = keys;
        }

        @Override
        public void forEach(Consumer<Message> action) {
            keys.forEach(k -> {
                String msg;
                while((msg = jedis.spop(k)) != null) {
                    action.accept(new Message(msg, Long.valueOf(k)));
                }
            });
        }
    }
}
