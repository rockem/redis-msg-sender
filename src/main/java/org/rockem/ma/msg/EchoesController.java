package org.rockem.ma.msg;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/echoes")
public class EchoesController {

    @RequestMapping
    public ResponseEntity<?> echo(@Valid @RequestBody EchoMessage message) {
        return null;
    }
}
