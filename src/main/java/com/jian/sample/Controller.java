package com.jian.sample;

import com.jian.util.MainHanlder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private Logger logger = LoggerFactory.getLogger("MainContoller");

    @FXML
    private TextField textField;
    @FXML
    private Button sButton;
    @FXML
    private TextArea textArea;
    @FXML
    private  Button conButton;
    @FXML
    private Pane pane;
    @FXML
    private Label clientId;
    @FXML
    private TextField  ip;
    @FXML
    private TextField port;
    @FXML
    private TextField pId;



   private MainHanlder mainHanlder;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainHanlder = new MainHanlder();
        connectButton();
        sendButton();
        callBackHandler();
    }





    public  void  sendButton(){
        sButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainHanlder.send(textField.getText());
                logger.info("send");

            }
        });


    }

    public  void  connectButton(){

        conButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {

                    if (StringUtils.isEmpty(ip.getText()) && StringUtils.isEmpty(port.getText())) {
                         mainHanlder.start(ip.getPromptText(), Integer.valueOf(port.getPromptText()));
                    }else{
                         mainHanlder.start(ip.getText(), Integer.valueOf(port.getText()));
                    }
                }catch (Exception e){
                    logger.error(e.getMessage(), e);

                }
            }
        });
    }

    public void callBackHandler(){
        mainHanlder.setTextArea((msg)->{
            textField.clear();
            textArea.appendText("me : "+msg +"\n");
            return false;
        });

        mainHanlder.getCid((cid) -> {
            Platform.runLater(()->{
                clientId.setText("ID:"+cid);
            });

        });
        mainHanlder.onConnected(()->{
            conButton.setStyle("-fx-background-color: green;");
        });

    }


}
