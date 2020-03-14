package com.huhu.nio;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

public class Test_06_Pipe {

    @Test
    public void test01() throws IOException {
        Pipe pipe = Pipe.open();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("保护环境人人有责".getBytes());
        byteBuffer.flip();

        Pipe.SinkChannel sinkChannel = pipe.sink();
        sinkChannel.write(byteBuffer);

        Pipe.SourceChannel sourceChannel = pipe.source();
        byteBuffer.flip();
        int len = sourceChannel.read(byteBuffer);
        System.out.println(new String(byteBuffer.array(), 0, len));

        sinkChannel.close();
        sourceChannel.close();
    }

}
