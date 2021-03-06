package com.jian.util;

import com.jian.handler.*;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @auther JianLinWei
 * @date 2019-12-06 11:41
 */


@ChannelHandler.Sharable
public class MainHanlder extends ChannelInboundHandlerAdapter{
    private static  final Logger logger = LoggerFactory.getLogger(MainHanlder.class);


    private  MessageHanlder messageHanlder;
    private  CidHandler cidHandler;
    private  ConnectedHandler connectedHandler;
    private AlertHandler alertHandler;
    private VideoHanlder videoHanlder;

    private ChannelFuture channelFuture ;






    private int s = 1 ;

    public  void  setChannelFuture(ChannelFuture future){
        this.channelFuture = future;
    }

    public    void send(String s , ChannelFuture   future){
        if (future==null){
            alertHandler.alert("服务器通信失败");
            return;
        }

        boolean suc =  future.channel().writeAndFlush(s).isVoid();
        logger.info("发送信息"+suc);
    }

    public void sendObj(ReslutUtil reslutUtil , ChannelFuture future){
        future.channel().writeAndFlush(reslutUtil);
    }
    public void sendObj(ReslutUtil reslutUtil ){
        channelFuture.channel().writeAndFlush(reslutUtil);
    }
    public    void send(String s){

        boolean suc =  channelFuture.channel().writeAndFlush(s).isVoid();
        logger.info("发送信息"+suc);
    }


    public MainHanlder setTextArea(MessageHanlder _messageHanlder){
        this.messageHanlder = _messageHanlder;
        return  this;
    }

    public MainHanlder getCid(CidHandler _CidHandler){
        this.cidHandler = _CidHandler;
        return this;
    }

    public  MainHanlder onConnected(ConnectedHandler _ConnectedHandler){
        this.connectedHandler = _ConnectedHandler;
        return  this;
    }
    public  MainHanlder alert(AlertHandler _AlertHandler){
        this.alertHandler = _AlertHandler;
        return  this;
    }
     public  MainHanlder setVedioImage(VideoHanlder _VedioImage){
        this.videoHanlder = _VedioImage;
        return  this;
     }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      logger.info("连接服务器成功;通道ID:{}" ,ctx.channel().id());

      connectedHandler.onConnected();

    }


    /*@Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s);
        System.out.println(this);
       messageHanlder.setTextArea(s);
    }*/

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if( msg instanceof  ReslutUtil) {
            ReslutUtil reslutUtil = (ReslutUtil) msg;
            switch (reslutUtil.getCode()) {
                case CmdCodeUtil.CID:
                    cidHandler.GetCid(String.valueOf(reslutUtil.getData()));
                    break;

            }
        }
        if(msg instanceof  String)
            messageHanlder.setTextArea(((String)msg));
        if(msg instanceof byte[])
            videoHanlder.setVideoImage(msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info(cause.getMessage());
       ctx.close();
       alertHandler.alert(cause.getMessage());
    }
}
