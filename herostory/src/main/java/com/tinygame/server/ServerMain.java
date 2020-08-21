package com.tinygame.server;
import com.tinygame.async.mq.MQProducer;
import com.tinygame.cmdhandler.CmdHandlerFactory;
import com.tinygame.rank.RankService;
import com.tinygame.util.RedisUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @author  wang
 * netty服务端基础代码
*
* */

public class ServerMain {

    static private final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    public static void main(String[] args) {
        CmdHandlerFactory.init();
        GameMsgRecognizer.init();
        MysqlSessionFactory.init();
        RedisUtil.init();
        MQProducer.init();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workGroup);
        b.channel(NioServerSocketChannel.class); //服务器信道处理方式

        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception { //客户端信道处理方式
                        ch.pipeline().addLast(
                            new HttpServerCodec(), //http服务器编码器
                            new HttpObjectAggregator(65535), //内容长度限制
                            new WebSocketServerProtocolHandler("/websocket"), //websocket协议处理器
                            new GameMsgDecoder(), //自定义消息解码器
                                new GameMsgEncoder(),//自定义消息编码器
                                new GameMsgHandler() //自定义协议处理器
                        );
            }
        });
        try {

            //
            ChannelFuture f = b.bind(12345).sync();
            if (f.isSuccess()){
                LOGGER.info("服务器启动成功");
            }

            System.out.println("开始监听端口"+f.toString());
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }


    }
}
