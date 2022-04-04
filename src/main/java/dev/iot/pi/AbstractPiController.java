package dev.iot.pi;

public abstract class AbstractPiController {

    private final LEDBlinker ledBlinker = new LEDBlinker();

    abstract void switchLedOn(boolean on);

    public void switchLed(LEDState ledState) {
        switch (ledState) {
            case ON: switchLedOn(true); break;
            case OFF: switchLedOn(false); break;
            case BLINK: {
                switchLedOn(ledBlinker.switchLedOn());
                break;
            }
        }
    }

}
