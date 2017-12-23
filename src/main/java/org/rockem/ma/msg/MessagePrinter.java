package org.rockem.ma.msg;

import org.rockem.ma.msg.repository.MessagesRepository;

public class MessagePrinter {

    private final MessagesRepository messageRepository;

    public MessagePrinter(MessagesRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void print() {
        messageRepository.getPendingMessages().forEach(m ->
            System.out.println(String.format("%s:%s", m.getTime(), m.getMessage())));
    }
}
