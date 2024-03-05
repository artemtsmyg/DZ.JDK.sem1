package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

/**

 Программа реализует клиент-серверное приложение чата.
 Клиент отправляет сообщения на сервер и получает ответы.
 История чата сохраняется и загружается из файла. */
// Клиентская часть
public class ChatClient {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JTextArea chatHistory;
    private JTextField messageField;

    public ChatClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            chatHistory = new JTextArea();
            messageField = new JTextField();

            // Загрузка истории чата из файла
            loadChatHistory();

            // Отправка сообщения при нажатии кнопки или Enter
            messageField.addActionListener(e -> sendMessage());
            JButton sendButton = new JButton("Отправить");
            sendButton.addActionListener(e -> sendMessage());

            // Добавление компонентов на форму
            JFrame frame = new JFrame("Чат-клиент");
            frame.getContentPane().add(new JScrollPane(chatHistory), BorderLayout.CENTER);
            frame.getContentPane().add(messageField, BorderLayout.SOUTH);
            frame.getContentPane().add(sendButton, BorderLayout.EAST);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = messageField.getText();
        out.println(message);
        messageField.setText("");
        addToChatHistory(message);
    }

    private void addToChatHistory(String message) {
        chatHistory.append(message + "\n");
        // Дублирование сообщения в файл
        try (PrintWriter fileOut = new PrintWriter(new FileWriter("chatlog.txt", true))) {
            fileOut.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        try (BufferedReader fileIn = new BufferedReader(new FileReader("chatlog.txt"))) {
            String line;
            while ((line = fileIn.readLine()) != null) {
                chatHistory.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 12345;
        new ChatClient(serverAddress, port);
    }
}