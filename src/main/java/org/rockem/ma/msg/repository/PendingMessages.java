package org.rockem.ma.msg.repository;

import org.rockem.ma.msg.Message;

import java.util.function.Consumer;

public interface PendingMessages {

    void forEach(Consumer<Message> action);
}
