package test.org.rockem.ma.msg;

import org.junit.Before;
import org.junit.Test;
import org.rockem.ma.msg.Message;
import org.rockem.ma.msg.MessagePrinter;
import org.rockem.ma.msg.repository.MessagesRepository;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessagePrinterTest {

    private static final List<Message> MESSAGES = Arrays.asList(
            new Message("popov goes to town", 1212),
            new Message("kuku is the guy", 1111)
    );
    private final MessagesRepository messageRepository = mock(MessagesRepository.class);
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final MessagePrinter printer = new MessagePrinter(messageRepository);

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(baos));
    }

    @Test
    public void doNothingWhenNoPendingMessages() throws Exception {
        when(messageRepository.getPendingMessages()).thenReturn(new ArrayList<>());
        printer.print();
        assertThat(baos.toString(), isEmptyString());
    }

    @Test
    public void shouldPrintPendingMessages() throws Exception {
        when(messageRepository.getPendingMessages()).thenReturn(MESSAGES);
        printer.print();
        assertThat(baos.toString(), is(printedMessages()));
    }

    private String printedMessages() {
        StringBuilder sb = new StringBuilder();
        for(Message m : MESSAGES) {
            sb.append(m.getTime()).append(":").append(m.getMessage()).append('\n');
        }
        return sb.toString();
    }
}