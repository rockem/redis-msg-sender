package org.rockem.ma.msg.repository;

import org.rockem.ma.msg.Message;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class PendingMessages implements Iterable<Message> {

    @Override
    public Iterator<Message> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super Message> action) {

    }

    @Override
    public Spliterator<Message> spliterator() {
        return null;
    }
}
