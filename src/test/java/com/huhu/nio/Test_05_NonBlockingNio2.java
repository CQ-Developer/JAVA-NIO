package com.huhu.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

public class Test_05_NonBlockingNio2 {

    @Test
    public void send() throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String msg = scanner.next();
            byteBuffer.put((new Date().toString() + "\n" + msg).getBytes());
            byteBuffer.flip();
            datagramChannel.send(byteBuffer, new InetSocketAddress("127.0.0.1", 8080));
            byteBuffer.clear();
        }

        datagramChannel.close();
    }

    @Test
    public void receive() throws IOException {
        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);
        datagramChannel.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();
        datagramChannel.register(selector, SelectionKey.OP_READ);

        while (selector.select() > 0) {
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isReadable()) {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    datagramChannel.receive(byteBuffer);
                    byteBuffer.flip();
                    System.out.println(new String(byteBuffer.array(), 0, byteBuffer.limit()));
                    byteBuffer.clear();
                }
            }
            iterator.remove();
        }
    }

}
