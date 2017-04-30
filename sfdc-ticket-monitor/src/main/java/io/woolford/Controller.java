package io.woolford;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
public class Controller {

    //TODO: do better healthcheck (e.g. check MySQL, any recent exceptions, etc...)
    @RequestMapping("/healthcheck")
    String healtcheck() {
        return "{\"status\": \"ok\"}";
    }

    //TODO: make stats call that returns counts for recent runs, etc...

}
