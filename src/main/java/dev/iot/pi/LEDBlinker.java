package dev.iot.pi;

public class LEDBlinker {

    private boolean ledLastBlinkStateOn;

    public boolean switchLedOn() {
        ledLastBlinkStateOn = !ledLastBlinkStateOn;
        return ledLastBlinkStateOn;
    }

}
