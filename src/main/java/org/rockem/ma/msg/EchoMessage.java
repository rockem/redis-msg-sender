package org.rockem.ma.msg;

import lombok.Data;

@Data
public class EchoMessage {

    private String message;
    private long time;
}
