package com.TelegramBot.Car.Bot;

import com.TelegramBot.Car.Bot.domain.User;
import com.TelegramBot.Car.Bot.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@Slf4j
public class MyBot extends TelegramLongPollingBot {
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public MyBot(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (message.equals("/start")) {
                sendMsgToUser(update.getMessage().getChatId(), "Hello! I'm a bot. I can echo your messages.");
            } else if (message.startsWith("/reg")) {
                registerUserByEmail(update);
            } else if (message.startsWith("/sales")) {
                sendAllSales(chatId);
            } else{
                echoSentMessage(update);
            }
        }
    }

    private void echoSentMessage(Update update) {
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();


        sendMsgToUser(chatId, message);
    }

    private void registerUserByEmail(Update update) {
        String message = update.getMessage().getText();
        String[] messageParts = message.split(" ");
        String email = messageParts[1];
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getChat().getUserName();
        if (isUserExist(chatId)) {
            sendMsgToUser(chatId, "This user already exists");
            return;
        }
        User user = User.builder()
                .name(username)
                .email(email)
                .chatId(chatId)
                .build();
        if (email.contains("@gmail.com")) {
            sendMsgToUser(chatId, "You successfully registered");
            User saveUser = userServiceImpl.saveUser(user);
        } else {
            sendMsgToUser(chatId, "Error occurred while registering user");
        }
    }

    private void sendMsgToUser(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (Exception e) {

        }
    }

    private boolean isUserExist(Long chatId) {
        User user = userServiceImpl.findUserByChatId(chatId);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    private void sendAllSales(Long chatId) {
        List<String> salesList = List.of("Sale1", "Sale2", "Sale3");
        StringBuilder stringBuilder = new StringBuilder("Your Sales\n");
        int index = 1;
        for (String s : salesList) {
            stringBuilder.append(index).append(". ").append(s).append("\n");
            index++;
        }
        sendMsgToUser(chatId, stringBuilder.toString());

    }

    @Override
    public String getBotUsername() {
        return "Car124_Bot";
    }

    @Override
    public String getBotToken() {
        return "6530709437:AAGt_AlLR9OTWozA-aDpeKpjuOUdJjDcaOs";
    }
}
