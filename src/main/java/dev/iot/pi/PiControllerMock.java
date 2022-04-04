package dev.iot.pi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!prod")
@Slf4j
public class PiControllerMock extends AbstractPiController {

    public void switchLedOn(boolean on) {
        log.info("Switch LED on? " + on);
    }

}
