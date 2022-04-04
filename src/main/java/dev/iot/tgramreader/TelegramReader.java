package dev.iot.tgramreader;

import dev.iot.pi.AbstractPiController;
import dev.iot.pi.LEDState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Service
@ConditionalOnProperty(
        value="tgram.module.enabled",
        havingValue = "true")
public class TelegramReader extends TelegramLongPollingBot {

    private final String telegramBotName;
    private final String telegramBotKey;
    private final AbstractPiController piController;
    private LEDState ledState;

    public TelegramReader(@Value("${tgram.botname}") String telegramBotName, @Value("${tgram.botkey}") String telegramBotKey, AbstractPiController piController) {
        this.telegramBotName = telegramBotName;
        this.telegramBotKey = telegramBotKey;
        this.piController = piController;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String telegramName = update.getMessage().getFrom().getFirstName();
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText().split(" ")[1];
            log.info("Received Message '" + update.getMessage().getText() + "' from '" + telegramName + "' at chat '" + chatId + "'");
            ledState = LEDState.valueOf(text.toUpperCase());
            piController.switchLed(LEDState.valueOf(text.toUpperCase()));
            createAndSendMessage(chatId, "Switched LED to " + ledState);
        }
    }

    @Scheduled(fixedDelay = 2000)
    public void switchLed() {
        if (ledState == LEDState.BLINK) {
            piController.switchLed(ledState);
        } else {
            log.debug("No action necessary in state " + ledState + ".");
        }
    }

    private void createAndSendMessage(String chatId, String content) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(content);
        message.setParseMode("Markdown");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Cannot send message.", e);
        }
    }

    @Override
    public String getBotUsername() {
        return telegramBotName;
    }

    @Override
    public String getBotToken() {
        return telegramBotKey;
    }
}