package org.rockem.ma.msg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/echoes")
public class EchoesController {

    private MessagesRepository messageRepository;

    @Autowired
    public EchoesController(MessagesRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @RequestMapping
    public void echo(@Valid @RequestBody Message message) {
        messageRepository.save(message);
    }

    @Scheduled(fixedRate = 1000)
    public void printMessages() {
        new MessagePrinter(messageRepository).print();
    }
}
