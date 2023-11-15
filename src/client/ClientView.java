package client;
import server.ServerView;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientView extends JFrame {
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 500;

    private ServerView serverView;
    private boolean connected;
    private String name;

    private JTextArea chat;
    private JTextField addressIP, port, login, tfMessage;
    private JPasswordField password;
    private JPanel panelUp;
    private JPanel panelDown;
    private JButton btnLogin;
    private JButton btnSend;





    public ClientView (ServerView server) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(100, 150);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("Клиентское приложение... ");
        setResizable(false);
        this.serverView = server;

        createPanel();

        setVisible(true);
    }

    private void createPanel() {
        add(createPanelUp(), BorderLayout.NORTH);
        add(createChat());
        add(createPanelDown(), BorderLayout.SOUTH);
    }

    private Component createPanelUp() {
        panelUp = new JPanel(new GridLayout(2, 3));
        addressIP = new JTextField("10.0.0.1");
        port = new JTextField("3333");
        login = new JTextField();
        login.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                name = login.getText();
            }
        });
        password = new JPasswordField("0000");
        btnLogin = new JButton("Подключиться");
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });


        panelUp.add(addressIP);
        panelUp.add(port);
        panelUp.add(new JPanel());
        panelUp.add(login);
        panelUp.add(password);
        panelUp.add(btnLogin);

        return panelUp;
    }

    private Component createChat() {
        chat = new JTextArea();
        chat.setEditable(false);
        return new JScrollPane(chat); // TODO: 09.11.2023 Задать вопрос про крокрутку
    }

    private Component createPanelDown() {
        panelDown = new JPanel(new GridLayout(1, 2));
        tfMessage = new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                if(e.getKeyChar() == '\n') {
                    sendMessage();
                }
            }
        });
        btnSend = new JButton("Отпрвавить");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        panelDown.add(tfMessage);
        panelDown.add(btnSend, BorderLayout.EAST);
        return panelDown;
    }

    private void sendMessage() {
        if (connected) {
            String text = tfMessage.getText();
            if(!text.equals("")) {
                serverView.sendMessage(name + ": " + text + "\n");
                tfMessage.setText("");
            }
        } else {
            appendChat("Нед подключения к серверу... ");
        }
    }


    private void connectToServer() {
        if(serverView.connectUser(this)) {
            appendChat("Вы успешно подключились!\n");
            panelUp.setVisible(false);
            connected = true;
            String log = serverView.getTextCenter();
            if (log != null) {
                appendChat(log);
            }
        } else {
            appendChat("Подключение невозможно\n");
        }
    }

    public void response(String text) {
        appendChat(text);
    }

    public void disConnectFromServer() {
        if(connected) {
            panelUp.setVisible(true);
            connected = false;
            serverView.disConnectUser(this);
            appendChat("Отключились успешно... ");
        } else {
            appendChat("Вы уже отключены.. ");
        }
    }

    private void appendChat(String s) {
        chat.setEditable(true);
        chat.append(s);
        chat.setEditable(false);
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if(e.getID() == WindowEvent.WINDOW_CLOSING) {
            disConnectFromServer();
        }
    }
}