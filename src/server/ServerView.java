package server;
import client.ClientView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServerView extends JFrame {
    // Локация
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 500;
    private static final int POS_X = 100;
    private static final int POS_Y = 150;
    private static final String FILE_CHAT = "src/main/resources/chat.txt";
    private static final String TITLE = "Сервер";

    private List<ClientView> clients;

    private JButton btnStart, btnStop;

    private JTextArea textCenter;
    private boolean working;

    public ServerView() {
        clients = new ArrayList<>();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocation(POS_X, POS_Y);
        setTitle(TITLE);

        createPanel();

        setVisible(true);
    }

    /**
     * Метод создания панели из вложенных панелей
     */
    private void createPanel() {
        textCenter = new JTextArea();
        add(textCenter, BorderLayout.CENTER);
        add(createButtons(), BorderLayout.SOUTH);
    }

    /**
     * Метод создания кнопок
     * @return - собранную из кнопок панель
     */
    private Component createButtons() {
        btnStart = new JButton("Старт");
        btnStop = new JButton("Стоп");

        JPanel panel = new JPanel(new GridLayout(1, 2));

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (working) {
                    appendChat("Сервер уже работает");
                } else {
                    working = true;
                    appendChat("Сервер запустился... ");
                }
                working = true;
            }
        });

        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (working) {
                    working = false;
                    for (ClientView client: clients
                    ) {
                        disConnectUser(client);
                    }
                    sendMessage("Сервер перестал работать.");
                } else {
                    sendMessage("Сервер уже не работает");
                }
            }
        });

        panel.add(btnStart);
        panel.add(btnStop);
        return panel;
    }

    /**
     * Метод выводит сообщания на экран
     * @param text
     */
    private void appendChat(String text) {
        textCenter.setEditable(true);
        textCenter.append(text + "\n");
        textCenter.setEditable(false);
    }

    /**
     * Метод отключения клиента от сервера
     * @param client - клиент, который будет отключён.
     */
    public void disConnectUser(ClientView client) {
        clients.remove(client);
        if(client != null) {
            client.disConnectFromServer();
        }
    }

    /**
     * Метод подключения клиента к серверу
     * @param client
     * @return
     */
    public boolean connectUser (ClientView client){
        if(!working) {
            return false;
        }
        clients.add(client);
        return true;
    }

    /**
     * Метод, который вытаскивает из переписку в окно чата
     * @return - возвращает метод чтения файла логов
     */
    public String getTextCenter() {
        return readLog();
    }

    /**
     * Метод на чтение логов с текста
     * Сделать через стрингбилдер.
     * @return - вовзращает строку
     */
    private String readLog() {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader fileReader = new FileReader(FILE_CHAT);) {
            int sym;
            while ((sym = fileReader.read()) != - 1) {
                stringBuilder.append((char) sym);
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Метод отаравки сообщения
     * @param text
     */
    public void sendMessage(String text) {
        if (!working){
            return;
        }
        text += "";
        appendChat(text);
        sendToAll(text);
        saveInLog(text);
    }


    private void sendToAll(String text) {
        for (ClientView client: clients
        ) {
            client.response(text);
        }
    }

    /**
     * Метод сохраняет переписку в файл.
     * @param text
     */
    private void saveInLog(String text) {
        try (FileWriter fileWriter = new FileWriter(FILE_CHAT, true)) {
            fileWriter.write(text + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}