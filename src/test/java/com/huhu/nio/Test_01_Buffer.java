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
public class Test_01_Buffer {

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