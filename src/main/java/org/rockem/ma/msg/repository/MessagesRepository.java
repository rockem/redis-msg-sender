package org.rockem.ma.msg.repository;

import org.rockem.ma.msg.Message;

public interface MessagesRepository {

    void save(Message message);

    Iterable<Message> getPendingMessages();
}
