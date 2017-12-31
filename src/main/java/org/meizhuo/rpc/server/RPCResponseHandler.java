package org.meizhuo.rpc.server;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.meizhuo.rpc.client.RPCRequest;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by wephone on 17-12-30.
 */
public class RPCResponseHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        String requestJson= (String) msg;
        System.out.println("receive request:"+requestJson);
        RPCRequest request=new Gson().fromJson(requestJson,RPCRequest.class);
        System.out.println(request);
        //netty的write方法并没有直接写入通道(为避免多次唤醒多路复用选择器)
        //而是把待发送的消息放到缓冲数组中，flush方法再全部写到通道中
//        ctx.write(resp);
        //记得加分隔符 不然客户端一直不会处理
        String respStr=request.getRequestID()+System.getProperty("line.separator");;
        ByteBuf responseBuf= Unpooled.copiedBuffer(respStr.getBytes());
        ctx.writeAndFlush(responseBuf);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();//这句的作用是将消息发送队列中的消息写入到SocketChannel中发给对方
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
