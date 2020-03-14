package com.huhu.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
public class Test_03_BlockingNio {

    /** 客户端1 */

    @Test
    public void client1() throws IOException {
        //建立网络通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));

        //创建本地文件通道
        FileChannel fileChannel = FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);

        //读取本地文件并写入到网络通道中
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (fileChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        //关闭通道
        fileChannel.close();
        socketChannel.close();
    }

    /** 服务端1 */

    @Test
    public void server1() throws IOException {
        //获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));

        //获取客户端连接的通道
        SocketChannel socketChannel = serverSocketChannel.accept();

        //创建文件通道并分配缓冲区
        FileChannel fileChannel = FileChannel.open(Paths.get("2.png"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //读取文件并写道本地
        while (socketChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        //关闭通道
        fileChannel.close();
        socketChannel.close();
        serverSocketChannel.close();
    }

    /** 客户端2 */

    @Test
    public void client2() throws IOException {
        //建立与服务器的通信的通道
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8080));

        //创建文件通道和字节缓冲区
        FileChannel fileChannel = FileChannel.open(Paths.get("1.png"), StandardOpenOption.READ);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //读取文件
        while (fileChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            socketChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        socketChannel.shutdownOutput();

        //接收服务器响应
        int len = 0;
        while ((len = socketChannel.read(byteBuffer)) != -1) {
            byteBuffer.flip();
            System.out.println(new String(byteBuffer.array(), 0, len));
            byteBuffer.clear();
        }

        fileChannel.close();
        socketChannel.close();
    }

    /** 服务端2 */

    @Test
    public void server2() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        SocketChannel socketChannel = serverSocketChannel.accept();

        FileChannel fileChannel = FileChannel.open(Paths.get("3.png"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (socketChannel.read(byteBuffer) != -1) {
            byteBuffer.flip();
            fileChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        byteBuffer.put("接收成功".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);

        fileChannel.close();
        socketChannel.close();
        serverSocketChannel.close();
    }

}
