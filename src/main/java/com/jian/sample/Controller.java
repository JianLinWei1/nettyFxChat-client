package com.jian.sample;

import com.jian.util.CmdCodeUtil;
import com.jian.util.MainHanlder;
import com.jian.util.ReslutUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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

    private  ChannelFuture   future ;



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
                if(StringUtils.isNotEmpty(pId.getText())){
                    ReslutUtil reslutUtil = new ReslutUtil();
                    reslutUtil.setCode(CmdCodeUtil.SEND_PRIVATE_OBJ);
                    reslutUtil.setCmd(pId.getText());
                    reslutUtil.setData(textField.getText());
                    mainHanlder.sendObj(reslutUtil, future);
                }else {
                    mainHanlder.send(textField.getText(), future);
                }


            }
        });


    }

    public  void  connectButton(){

        conButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    /*if(StringUtils.isEmpty(pId.getText())){
                        Alert  alert  = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("请输入参数");
                        alert.showAndWait();
                        return;
                    }*/

                    if (StringUtils.isEmpty(ip.getText()) && StringUtils.isEmpty(port.getText())) {
                         start(ip.getPromptText(), Integer.valueOf(port.getPromptText()));
                    }else{
                         start(ip.getText(), Integer.valueOf(port.getText()) );
                    }
                }catch (Exception e){
                    logger.error(e.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("连接服务器失败");
                    alert.showAndWait();

                }
            }
        });
    }

    //函数式回调
    public void callBackHandler(){
        mainHanlder.setTextArea((msg)->{
            textField.clear();
            textArea.appendText(msg +"\n");
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
        mainHanlder.alert((msg)->{
            Platform.runLater(() ->{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(msg
                );
                alert.showAndWait();
            });

        });

    }

    /**
     *连接netty服务器
     * @param host
     * @param port
     * @throws InterruptedException
     */
    public   void  start(String host , int port ) throws InterruptedException {

        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel SocketChannel) throws Exception {
                        ChannelPipeline pipeline = SocketChannel.pipeline();
                        /*pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new StringDecoder());*/
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE , ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(mainHanlder);
                    }


                });

         future = bootstrap.connect(host, port).sync();


        /*  eventLoopGroup.shutdownGracefully();*/


    }


}
