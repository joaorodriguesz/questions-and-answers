package org.questionsandanswers;

import org.questionsandanswers.client.ForumClient;
import org.questionsandanswers.server.ForumServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Iniciar o servidor em uma thread separada
        Thread serverThread = new Thread(() -> {
            try {
                ForumServer.main(new String[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();

        Thread.sleep(300);

        ForumClient.main(new String[0]);
    }
}