package org.rockem.ma.msg;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/echoes")
public class EchoesController {

    @RequestMapping
    public ResponseEntity<?> echo(@RequestBody EchoMessage message) {
        return ResponseEntity.badRequest().build();
    }
}
