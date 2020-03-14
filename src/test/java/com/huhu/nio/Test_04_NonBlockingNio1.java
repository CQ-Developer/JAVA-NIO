package com.huhu.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

/**
 * 使用NIO完成网络通信的三个核心
 *
 * 1.通道 Channel 负责连接
 * java.nio.channels.Channel (interface)
 * java.nio.channels.SelectableChannel (abstract class)
 * java.nio.channels.SocketChannel (abstract class)
 * java.nio.channels.ServerSocketChannel (abstract class)
 * java.nio.channels.DatagramChannel (abstract class)
 * java.nio.channels.Pipe.SourceChannel (abstract class)
 * java.nio.channels.Pipe.SinkChannel (abstract class)
 *
 *
 * 2.缓冲区 Buffer 负责数据的存取
 * 3.选择器 Selector 是SelectableChannel的多路复用器,用于监控SelectableChannel的IO状况
 */
public class Test_04_NonBlockingNio1 {

    /** 客户端 */

    @Test
    public void client() throws IOException {
        //建立通道并设置为非阻塞状态
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));
        socketChannel.configureBlocking(false);

        //穿件缓冲区并存入数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);


        //循环写入:IDEA空值太不能输出可以baidu或者改用main测试
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            System.out.println("请输出:");
            String msg = scanner.next();

            //将数据写入通道中
            byteBuffer.put((new Date().toString() + "\n" + msg).getBytes());
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        //关闭连接
        socketChannel.close();
    }

    /** 服务端 */

    @Test
    public void server() throws IOException {
        //建立通道并设置为非阻塞状态
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8080));

        //获取选择器并并注册监听接收事件
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //遍历选择器上已准备就绪的事件
        while (selector.select() > 0) {
            //获取所有已注册的选择键
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                //判断事件类型
                if (selectionKey.isAcceptable()) {
                    //获取客户端连接的通道
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);

                    //将客户端通道注册到选择器上
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    //获取读状态就绪的通道
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

                    //读取数据
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int len = 0;
                    while ((len = socketChannel.read(byteBuffer)) != -1) {
                        byteBuffer.flip();
                        System.out.println(new String(byteBuffer.array(), 0, len));
                        byteBuffer.clear();
                    }
                }
                //取消选择器
                iterator.remove();
            }
        }
    }

}
