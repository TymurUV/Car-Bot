package com.TelegramBot.Car.Bot;

import com.TelegramBot.Car.Bot.domain.User;
import com.TelegramBot.Car.Bot.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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
            if (message.equals("/start")) {
                sendMsgToUser(update.getMessage().getChatId(), "Hello! I'm a bot. I can echo your messages.");
            } else if (message.startsWith("/reg")) {
                registerUserByEmail(update);
            } else {
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
        String regs = message;
        String email = messageParts[1];
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getChat().getUserName();

        User user = User.builder()
                .name(username)
                .email(email)
                .chatId(chatId)
                .build();

        User saveUser = userServiceImpl.saveUser(user);
        if (regs.contains("@gmail.com")) {
            sendMsgToUser(chatId, "You successfully registered");
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

    @Override
    public String getBotUsername() {
        return "Car124_Bot";
    }
}
