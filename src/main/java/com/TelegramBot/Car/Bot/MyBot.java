package com.TelegramBot.Car.Bot;

import com.TelegramBot.Car.Bot.domain.Product;
import com.TelegramBot.Car.Bot.domain.User;
import com.TelegramBot.Car.Bot.service.ProductService;
import com.TelegramBot.Car.Bot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MyBot extends TelegramLongPollingBot {
    private final UserService userService;
    private final ProductService productService;
    private final Map<Long, List<Product>> map = new HashMap<>();

    @Autowired
    public MyBot(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            if (message.equals("/start")) {
                sendMsgToUser(update.getMessage().getChatId(), "Hello! I'm a bot. I can echo your messages.", null, null);
            } else if (message.startsWith("/reg")) {
                registerUserByEmail(update);
            } else if (message.startsWith("/sales")) {
                sendAllSales(chatId);
            } else if (message.startsWith("/product")) {
                getProductsFromTable(chatId);
            } else if (message.startsWith("/id")) {
                busketProducts(chatId, message);
            } else if (message.startsWith("/showCart")) {
                getProductsFromCart(chatId, map);
            } else if (message.startsWith("/remove")) {
                removeProductFromCart(chatId, map, message);
            } else if (message.startsWith("/image")) {
                sendPhotoToUser(chatId, "C:\\Users\\user\\Car-Bot\\src\\main\\java\\com\\TelegramBot\\Car\\Bot\\images\\Screenshot 2023-11-28 200549.png");
            }
        }
    }


    private void registerUserByEmail(Update update) {
        String message = update.getMessage().getText();
        String[] messageParts = message.split(" ");
        String email = messageParts[1];
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getChat().getUserName();
        if (isUserExist(chatId)) {
            sendMsgToUser(chatId, "This user already exists", null, null);
            return;
        }
        User user = User.builder().name(username).email(email).chatId(chatId).build();
        if (email.contains("@gmail.com")) {
            sendMsgToUser(chatId, "You successfully registered", null, null);
            User saveUser = userService.saveUser(user);
        } else {
            sendMsgToUser(chatId, "Error occurred while registering user", null, null);
        }
    }

    private void sendMsgToUser(Long chatId, String message, List<String> buttonList, Integer row) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        if (buttonList != null) {
            InlineKeyboardMarkup inlineKeyboardMarkup = createCustomKeyBoard(buttonList, row);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }
        try {
            execute(sendMessage);
        } catch (Exception e) {

        }
    }

    private boolean isUserExist(Long chatId) {
        User user = userService.findUserByChatId(chatId);
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
        sendMsgToUser(chatId, stringBuilder.toString(), null, null);

    }

    private void getProductsFromCart(Long chatId, Map<Long, List<Product>> productMap) {
        List<String> carIdButton = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder("Products in your cart\n");
        for (Map.Entry<Long, List<Product>> entry : productMap.entrySet()) {
            stringBuilder.append("Your id").append(" : ").append(entry.getKey()).append("\n").append("Products").append(" : ").append("\n").append("------").append("\n");
            List<Product> productList = entry.getValue();
            for (Product p : productList) {
                stringBuilder.append("Item id").append(" : ").append(p.getId()).append("\n").append("Name").append(" : ").append(p.getName()).append("\n").append("Description").append(" : ").append(p.getDescription()).append("\n").append("Price").append(" : ").append(p.getPrice()).append("$ \n").append("------ \n");
            }

            entry.getValue().stream().map(product -> String.valueOf(product.getId())).forEach(carIdButton::add);
        }
        if (carIdButton.isEmpty()) {
            sendMsgToUser(chatId, "your cart is empty", null, null);
        } else {
            sendMsgToUser(chatId, stringBuilder.toString(), carIdButton, carIdButton.size());
        }

    }

    private void sendPhotoToUser(Long chatId, String url) {
        File file = new File(url);
        InputFile inputFile = new InputFile(file);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(inputFile);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private void removeProductFromCart(Long chatId, Map<Long, List<Product>> productLongMap, String message) {
        Product product = getProductById(message, 7);
        productLongMap.get(chatId)
                .removeIf(productByDelete -> productByDelete.getId().equals(product.getId()));
        sendMsgToUser(chatId, "Your item successfully deleted", null, null);
    }

    private void getProductsFromTable(Long chatId) {
        List<Product> productList = productService.getAllProducts();
        if (productList.isEmpty()) {
            sendMsgToUser(chatId, "There is no products yet, sorry", null, null);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder("Products\n");
        int index = 1;
        for (Product p : productList) {
            stringBuilder.append(index).append(". ").append(p.getName()).append(" (").append("/id").append(p.getId()).append(") ").append("\n").append(p.getDescription()).append("\n").append(p.getPrice()).append("\n");
            index++;
        }
        sendMsgToUser(chatId, stringBuilder.toString(), List.of("<-", "->", "Car", "New car"), 4);
    }

    private Product getProductById(String message, int index) {
        Long id = Long.valueOf(message.substring(index));
        Product product = productService.getProductById(id);
        return product;
    }

    private void busketProducts(Long chatId, String message) {
        Product productById = getProductById(message, 3);
        List<Product> listProduct = new ArrayList<>();
        if (map.containsKey(chatId)) {
            listProduct = map.get(chatId);
            listProduct.add(productById);
        } else {
            listProduct.add(productById);
            map.put(chatId, listProduct);
        }
        System.out.println(map);

    }

    private InlineKeyboardMarkup createCustomKeyBoard(List<String> buttonText, int buttonsRow) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        System.out.println(buttonsRow);
        int buttonPerRow = buttonText.size() / buttonsRow + buttonText.size() % buttonsRow;
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (String b : buttonText) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(b);
            button.setCallbackData(b);

            row.add(button);
            if (row.size() == buttonPerRow || button.equals(buttonText.get(buttonText.size() - 1))) {
                rows.add(row);
                row = new ArrayList<>();
            }
        }
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
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
