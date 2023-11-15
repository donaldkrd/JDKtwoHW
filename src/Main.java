import client.ClientView;
import server.ServerView;

public class Main {
    public static void main(String[] args) {
        ServerView serverView = new ServerView();
        new ClientView(serverView);
        new ClientView(serverView);
    }
}
