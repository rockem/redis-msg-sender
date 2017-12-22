package org.rockem.ma.msg;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EchoMessage {

    @NotNull
    private String message;
    @NotNull
    @OnlyFuture
    private long time;
}
