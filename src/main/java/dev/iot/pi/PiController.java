package dev.iot.pi;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Profile("prod")
@Slf4j
public class PiController extends AbstractPiController {

    private static final int PIN_LED = 22;
    private DigitalOutput led;

    @PostConstruct
    private void init() {
        Context pi4j = Pi4J.newAutoContext();
        DigitalOutputConfigBuilder ledConfig = DigitalOutput.newConfigBuilder(pi4j)
                .id("led")
                .name("LED Flasher")
                .address(PIN_LED)
                .shutdown(DigitalState.LOW)
                .initial(DigitalState.LOW)
                .provider("pigpio-digital-output");
        led = pi4j.create(ledConfig);
    }

    @Override
    public void switchLedOn(boolean on) {
        log.info("Switch LED on? " + on);
        if (on) {
            led.high();
        } else {
            led.low();
        }
    }

}
