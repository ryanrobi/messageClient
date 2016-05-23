import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{
    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String message;
    private String serverIP;
    private Socket connection;

    public Client(String host){
        super("Client mofo!");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                sendMessage(event.getActionCommand());
                userText.setText("");
            }
        });
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);
    }

    public void startRunning(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }catch(EOFException ex){
            showMessage("\nCLIENT TERMINATED CONNECTION");
        }catch(IOException ex){
            ex.printStackTrace();
        }finally{
            closeCrap();
        }
    }

    private void connectToServer() throws IOException{
        showMessage("\nAttempting connection...");
        connection = new Socket(InetAddress.getByName(serverIP), 6789);
        showMessage("\nConnected to: " + connection.getInetAddress().getHostName());
    }

    private void setupStreams() throws IOException{
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        showMessage("\nStreams are now connected\n");
    }

    private void whileChatting() throws IOException{
        ableToType(true);
        do{
            try{
                message = (String)inputStream.readObject();
                showMessage("\n" + message);
            }catch(ClassNotFoundException ex) {
                showMessage("\nI dont know that object type");
            }
        }while(!message.equals("SERVER - END"));
    }

    private void closeCrap(){
        showMessage("\nClosing chat down...");
        ableToType(false);
        try{
            outputStream.close();
            inputStream.close();
            connection.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
        showMessage("\nYou can now close the window");
    }

    private void sendMessage(String message){
        try{
            outputStream.writeObject("CLIENT - " + message);
            outputStream.flush();
            showMessage("\nCLIENT - " + message);
        }catch(IOException ex){
            chatWindow.append("\n something went way wrong");
        }
    }

    private void showMessage(final String text){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                chatWindow.append(text);
            }
        });
    }

    private void ableToType(final boolean canType){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                userText.setEditable(canType);
            }
        });
    }
}
