package dev.iot.sheetreader;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import dev.iot.pi.LEDState;
import dev.iot.pi.AbstractPiController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@ConditionalOnProperty(
        value="gsheet.module.enabled",
        havingValue = "true")
public class SheetReader {

    private final String spreadsheetId;
    private final String range;
    private final String key;
    private final Sheets service;
    private final AbstractPiController piController;

    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private LEDState lastState;

    public SheetReader(AbstractPiController piController, @Value("${gsheet.spreadsheet.id}") String spreadsheetId, @Value("${gsheet.range}") String range, @Value("${gsheet.apikey}") String key) throws GeneralSecurityException, IOException {
        this.piController = piController;
        this.spreadsheetId = spreadsheetId;
        this.range = range;
        this.key = key;
        HttpRequestInitializer httpRequestInitializer =
            request -> request.setInterceptor(intercepted -> intercepted.getUrl().set("key", key));

        service = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, httpRequestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Scheduled(fixedDelay = 2000)
    public void readData() throws Exception {
        Instant start = Instant.now();
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
        Instant finish = Instant.now();
        log.debug("Duration for retrieval: " + Duration.between(start, finish).toMillis());
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            log.info("No data found.");
        } else {
            String lastValue = String.valueOf(values.get(values.size()-1).get(0));
            switchLed(lastValue);
        }
    }

    private void switchLed(String ledState) {
        LEDState ledStateEnum = LEDState.valueOf(ledState.toUpperCase());
        if (ledStateEnum != lastState) {
            piController.switchLed(ledStateEnum);
            lastState = ledStateEnum;
        } else {
            if (ledStateEnum == LEDState.BLINK) {
                piController.switchLed(ledStateEnum);
            } else {
                log.debug("No action necessary in state " + ledState + ".");
            }
        }
    }
}
