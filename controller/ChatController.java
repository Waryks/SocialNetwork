package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.service.FriendService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class ChatController {
    private User user;
    private UserService userService;
    private MessageService msgService;
    private FriendService friendService;
    public void setService(UserService u, MessageService m, FriendService f){
        userService = u;
        msgService = m;
        friendService = f;
    }
    public void setUser(User u){
        user = u;
    }
    @FXML
    private TextArea chatText;

    @FXML
    private TableView<String> chatTable;

    @FXML
    private Button sendButton;

    @FXML
    private TextField messageText;

    @FXML
    private Button closeButton;

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleMessage(ActionEvent event) {
        String txt = messageText.getText();
        if(!txt.equals("")){
            chatText.appendText(txt + '\n');
            messageText.setText("");
        }
    }
    @FXML
    void enterMessage(ActionEvent event) {
        String txt = messageText.getText();
        messageText.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER && !txt.equals("")){
                chatText.appendText(txt + '\n');
                messageText.setText("");
            }
        });
    }
    String textFormatter(String text){
        List<String> lText = Arrays.asList(text.split(" "));
        StringBuffer newText = new StringBuffer();
        newText.append(text);
        int poz = -1, len = 1, index;
        for(index = 0; index < text.length(); index++){
            if(text.charAt(index) == ' '){
                poz = index;
            }
            if(index == text.length()-1)
                break;
            if(len == 19){
                if(poz != -1){
                    newText.insert(index-(18-poz%18), '\n');
                    poz = -1;
                    len = poz%18;
                }
                else{
                    newText.insert(index, '\n');
                    len = 0;
                }
            }
            len++;
        }

        /*StringBuffer stxt = new StringBuffer();
        stxt.append(text);
        for(int index=1; index*20 < text.length(); index++)
            stxt.insert(index*20,'\n');*/
        return newText.toString();
    }
}
