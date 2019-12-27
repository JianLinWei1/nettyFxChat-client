package com.jian.util;

import com.jian.observer.CidHandler;
import com.jian.observer.ConnectedHandler;
import com.jian.observer.MessageHanlder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @auther JianLinWei
 * @date 2019-12-06 11:41
 */
public class MainHanlder extends ChannelInboundHandlerAdapter{
    private static  final Logger logger = LoggerFactory.getLogger(MainHanlder.class);


    private static MessageHanlder messageHanlder;
    private static CidHandler cidHandler;
    private static ConnectedHandler connectedHandler;
    private ChannelFuture future ;


    private int s = 1 ;






    public   void  start(String host , int port) throws InterruptedException {

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
                        pipeline.addLast(new MainHanlder());
                    }


                });

        future = bootstrap.connect(host, port).sync();


        /*  eventLoopGroup.shutdownGracefully();*/


    }

    public    void send(String s){
        boolean suc =  future.channel().writeAndFlush(s).isVoid();
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

    }
}
