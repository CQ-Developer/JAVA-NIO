package com.huhu.nio;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.SortedMap;

/**
 * Channel
 *
 * 用于原节点与目标节点的连接,在NIO中负责缓存缓冲区中的数据传输
 * 本身不存储任何数据,需要配合Buffer进行传输
 *
 * 通道的主要实现类
 * java.nio.channels.Channel接口
 * FileChannel, SocketChannel, ServerSocketChannel, DatagramChannel
 *
 * 获取通道
 * java针对支持通道的类提供了getChannel()方法
 * 本地IO: FileInputStream, FileOutputStream, RandomAccessFile
 * 网络IO: Socket, ServerSocket, DatagramSocket
 * 在jdk1.7中NIO2针对各个通道提供了静态方法open()
 * 还有Files工具类中提供的newByteChannel()
 *
 * 通道之间的数据传输
 * transferFrom(), transferTo()
 *
 * 分散(Scatter)与聚集(Gather)
 * 分散读取(Scatter Reads): 将通道中的数据分散到多个缓冲区中
 * 聚集写入(Gather Writes): 将多个缓冲区中的数据聚集到通道中
 *
 * 字符集 Charset
 * 编码: 字符串->字节数组
 * 解码: 字符数组->字符串
 */
public class Test_02_Channel {

    /** 使用非直接缓冲区 */

    @Test
    public void test01() {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            //使用通道完成文件的复制
            inputStream = new FileInputStream("1.jpg");
            outputStream = new FileOutputStream("2.jpg");

            //获取通道
            inChannel = inputStream.getChannel();
            outChannel = outputStream.getChannel();

            //分配一个指定大小的缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            //将通道中的数据写入缓冲区
            while (inChannel.read(buffer) != -1) {
                //切换读取数据的模式
                buffer.flip();
                //将缓冲区的数据写入通道
                outChannel.write(buffer);
                //清空缓冲区
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outChannel != null) outChannel.close();
                if (inChannel != null) inChannel.close();
                if (outputStream != null) outputStream.close();
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** 使用直接缓冲区,内存映射文件的方式 */

    @Test
    public void test02() {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.READ,
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            //内存映射文件,类似allocateDirect()
            MappedByteBuffer inMapBuffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0L, inChannel.size());
            MappedByteBuffer outMapBuffer = outChannel.map(FileChannel.MapMode.READ_WRITE, 0L, inChannel.size());

            //直接对缓冲区进行读写操作
            byte[] dts = new byte[inMapBuffer.limit()];
            inMapBuffer.get(dts);
            outMapBuffer.put(dts);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inChannel != null) inChannel.close();
                if (outChannel != null) outChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** 通道之间的数据传输 */

    @Test
    public void test03() {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = FileChannel.open(Paths.get("D:/dev/NIO-Demo/1.jpg"), StandardOpenOption.READ);
            outputChannel = FileChannel.open(Paths.get("D:/dev/NIO-Demo/2.jpg"), StandardOpenOption.READ,
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE);

            /*
             * inputChannel.transferTo(0, inputChannel.size(), outputChannel);
             * outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
             * 上面两个方法的作用是一样的
             */
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputChannel != null) inputChannel.close();
                if (outputChannel != null) outputChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** 分散与聚集 */

    @Test
    public void test04() {
        RandomAccessFile inputRaf = null;
        RandomAccessFile outputRaf = null;
        try {
            inputRaf = new RandomAccessFile("1.txt", "rw");

            //获取通道
            FileChannel inputChannel = inputRaf.getChannel();

            //创建多个缓冲区
            ByteBuffer buffer1 = ByteBuffer.allocate(100);
            ByteBuffer buffer2 = ByteBuffer.allocate(1024);

            //分散读取
            ByteBuffer[] buffers = {buffer1, buffer2};
            inputChannel.read(buffers);

            //切换到读取模式
            for (ByteBuffer buffer : buffers) {
                buffer.flip();
            }

            //获取输出
            System.out.println(new String(buffer1.array(), buffer1.position(), buffer1.limit()));
            System.out.println(new String(buffer2.array(), buffer2.position(), buffer2.limit()));

            //聚集写入
            outputRaf = new RandomAccessFile("2.txt", "rw");
            FileChannel outChannel = outputRaf.getChannel();
            outChannel.write(buffers);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputRaf != null) inputRaf.close();
                if (outputRaf != null) outputRaf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** 字符集编解码 */

    @Test
    public void test05() {
        //查看字符集
        SortedMap<String, Charset> charsetMap = Charset.availableCharsets();
        System.out.println("支持的字符集数量: " + charsetMap.size());
        charsetMap.forEach((key, value) -> System.out.println(key + ":" + value));
    }

    /** 字符集 */

    @Test
    public void test06() throws CharacterCodingException {
        CharBuffer buffer = CharBuffer.allocate(1024);
        buffer.put("我们保护地球环境");
        buffer.flip();

        //GBK编码
        CharsetEncoder encoder = Charset.forName("GBK").newEncoder();
        ByteBuffer byteBuffer = encoder.encode(buffer);
        for (int i = 0; i < byteBuffer.limit(); i++) {
            System.out.println(byteBuffer.get());
        }

        //GBK解码
        CharsetDecoder GBKDecoder = Charset.forName("GBK").newDecoder();
        byteBuffer.flip();
        CharBuffer charBuffer1 = GBKDecoder.decode(byteBuffer);
        System.out.println(charBuffer1.toString());

        //UTF-8解码
        CharsetDecoder UTF8Dcoder = StandardCharsets.UTF_8.newDecoder();
        byteBuffer.flip();
        CharBuffer charBuffer2 = UTF8Dcoder.decode(byteBuffer);
        System.out.println(charBuffer2.toString());
    }

}
