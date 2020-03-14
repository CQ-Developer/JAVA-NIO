# JAVA NIO



## 1. JAVA NIO

> - NIO全称New IO或Non Blocking IO , 也就是我们通常称的非阻塞式IO
> - 从JDK1.4版本开始引入一个新的API , 可以代替标准的JAVA IO API
> - NIO与原来的IO有相同的作用和目的 , 但使用的方式完全不同
> - NIO支持面向缓冲区的 , 基于通道的IO操作
> - NIO将以更高效的方式进行文件的读写操作



## 2. NIO & IO

| IO                     | NIO                         |
| :--------------------- | --------------------------- |
| 面向流 Stream Oriented | 面向缓冲区 Buffer Oriented  |
| 阻塞 IO (Blocking IO)  | 非阻塞 IO (Non Blocking IO) |
| 无                     | 选择器 Selectors            |



## 3. Buffer & Channel

> - JAVA NIO 系统的核心在于 : 通道和缓冲区 , 也就是Buffer 和 Channel
> - 通道表示打开到 IO 设备的连接 , 可以是文件或socket
> - 若要使用 NIO , 需要获取用于连接的 IO 设备的通道和可以容纳数据的缓冲区
> - 操作缓冲区对数据做处理
> - 理解 : Channel负责数据的传输 , Buffer负责数据的处理
>
> ### Buffer
>
> - 一个用于特定基本数据类型的容器 , 由java.nio包定义的所有缓冲区都是Buffer抽象类的子类
> - Buffer主要用于NIO通道进行交互 , 数据从通道读入缓冲区 , 从缓冲区写入通道
> - 需要自行理解直接缓冲区和非直接缓冲区

```java
package com.huhu.nio;

import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * Buffer
 *
 * 在NIO中负责数据的存取
 * 缓冲区就是数组,用于存储不同数据类型的数据
 *
 * 根据数据类型不同,提供了相应类型的缓冲区,除了布尔型
 * ByteBuffer, CharBuffer, ShortBuffer, IntBuffer, LongBuffer, FloatBuffer, DoubleBuffer
 *
 * 上述缓冲区的管理方式几乎一致,都是通过allocate()获取缓冲区
 *
 * 缓冲区存取数据的核心方法
 * put()存入数据到缓冲区中
 * get()从缓冲区中获取数据
 *
 * 缓冲区的核心数据{@link java.nio.Buffer}
 * capacity 容量:表示缓冲区中最大存储数据的容量,一旦声明不能改变
 * limit 边界:表示缓冲区中可以操作数据的大小,limit后面的数据不能进行读写
 * position 位置:表示缓冲区中正在操作数据的位置
 * mark 标记:表示记录当前position的位置,可以通过reset()恢复到mark位置
 *
 * mark <= position <= limit <= capacity
 *
 * 直接缓冲区与非直接缓冲区
 * 非直接缓冲区:通过allocate()方法分配的缓冲区,将缓冲区建立在JVM的内存中
 * 直接缓冲区:allocateDirect()方法分配直接缓冲区,将缓冲区建立在操作系统的物理内存中,可以提高效率
 */
public class BufferTests {

    @Test
    public void test01() {
        //1.分配一个指定大小的缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        System.out.println("---初始化缓冲区完成---");

        //输出查看
        System.out.println("操作位:" + byteBuffer.position());
        System.out.println("限制位:" + byteBuffer.limit());
        System.out.println("总容量" + byteBuffer.capacity());
        System.out.println();

        //2.存入数据
        String str = "abcde";
        byteBuffer.put(str.getBytes());
        System.out.println("---写入数据完成---");

        //输出查看
        System.out.println("操作位:" + byteBuffer.position());
        System.out.println("限制位:" + byteBuffer.limit());
        System.out.println("总容量" + byteBuffer.capacity());
        System.out.println();

        //3.切换到读取数据的模式
        byteBuffer.flip();
        System.out.println("---切换到读取模式---");

        //输出查看
        System.out.println("操作位:" + byteBuffer.position());
        System.out.println("限制位:" + byteBuffer.limit());
        System.out.println("总容量" + byteBuffer.capacity());
        System.out.println();

        //4.读取数据
        byte[] dst = new byte[byteBuffer.limit()];
        byteBuffer.get(dst);
        String getStr = new String(dst, 0, dst.length);
        System.out.println(getStr);
        System.out.println("---读取数据结束---");

        //输出查看
        System.out.println("操作位:" + byteBuffer.position());
        System.out.println("限制位:" + byteBuffer.limit());
        System.out.println("总容量" + byteBuffer.capacity());
        System.out.println();

        //5.可重复读取数据
        byteBuffer.rewind();
        System.out.println("---切换到重新读取模式---");

        //输出查看
        System.out.println("操作位:" + byteBuffer.position());
        System.out.println("限制位:" + byteBuffer.limit());
        System.out.println("总容量" + byteBuffer.capacity());
        System.out.println();

        //6.清空缓冲区,但是缓冲区的数据还存在,处于被遗忘状态
        byteBuffer.clear();
        System.out.println("---清空缓冲区---");
        System.out.println("缓冲区清空后获取的数据:" + (char) byteBuffer.get());

        //输出查看
        System.out.println("操作位:" + byteBuffer.position());
        System.out.println("限制位:" + byteBuffer.limit());
        System.out.println("总容量" + byteBuffer.capacity());
        System.out.println();

    }

    @Test
    public void test02() {
        String data = "abcde";
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put(data.getBytes());
        buffer.flip();

        //1.读取两个数据
        byte[] dst = new byte[buffer.limit()];
        buffer.get(dst, 0, 2);
        System.out.println(new String(dst, 0, 2));

        //获取position的位置
        System.out.println("读取两个数据后的操作位:" + buffer.position());

        //标记position的位置
        buffer.mark();

        //2.在读取两个数据
        buffer.get(dst, 2, 2);
        System.out.println(new String(dst, 2, 2));

        //获取position的位置
        System.out.println("再读取两个数据后的操作位:" + buffer.position());

        //回复到标记位
        buffer.reset();
        System.out.println("恢复后的操作位:" + buffer.position());

        //判断缓冲区是否还有可以操作的数据
        if (buffer.hasRemaining()) {
            //查看可以操作的数量
            System.out.println(buffer.remaining());
        }
    }

    @Test
    public void test03() {
        //1.分配直接缓冲区
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        //2.判断是直接缓冲区还是非直接缓冲区
        boolean direct = buffer.isDirect();
        System.out.println(direct);
    }

}
```

> ### Channel
>
> - 由java.nio.channels包定义 , Channel表示IO源于目标之间打开的连接
> - Channel类似于传统的流 , 只不过Channel本身不能直接访问数据
> - Channel只能和Buffer进行交互


```java
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
public class TestChannel {

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
```



## 4. FileChannel



## 5. NIO的非阻塞式网络通信

- 阻塞式IO常用类结构

- 阻塞式IO代码示例

```java
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
public class TestBlockingNio {

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
```

---

- 非阻塞式IO

```java
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
public class TestNonBlockingNio1 {

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
```

- UDP

```java
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
```



## 6. Pipe

- JAVA NIO管道是2个线程之间的单向数据连接
- Pipe有一个source通道和一个sink通道
- 数据会被写到sink通道
- 从source通道读取

```java
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
```





## 7. JAVA NIO2 (Path, Paths, Files)

- 查看演示代码