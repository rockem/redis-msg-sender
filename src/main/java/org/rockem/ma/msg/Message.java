package org.rockem.ma.msg;

import lombok.*;
import org.rockem.ma.msg.validator.OnlyFuture;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Message {

    @NotNull
    private String message;
    @NotNull
    @OnlyFuture
    private long time;
}
