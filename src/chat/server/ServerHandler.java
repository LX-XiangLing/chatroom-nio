package chat.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * 服务器端处理器
 *
 * @Author lixiang
 * @Date 2020/12/25
 */
public class ServerHandler {
    /**
     * 接入事件处理器
     *
     * @param serverSocketChannel
     * @param selector
     * @throws IOException
     */
    public static void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        //创建socketChannel
        SocketChannel socketChannel = serverSocketChannel.accept();
        //将socketChannel设置为非阻塞工作模式
        socketChannel.configureBlocking(false);
        //将channel注册到selector上，读就绪事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        //客户端连接成功提示信息
        socketChannel.write(Charset.forName("UTF-8").encode("欢迎进入聊天室(Welcome to the chat room)"));
    }

    /**
     * @param selectionKey
     * @param selector
     * @throws IOException
     */
    public static void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        //要从 selectionKey 中获取到已经就绪的channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        //创建ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        StringBuilder stringBuilder = new StringBuilder();
        // 循环读取客户端信息写入ByteBuffer
        while (socketChannel.read(byteBuffer) > 0) {
            //更改ByteBuffer为读模式
            byteBuffer.flip();
            //读取ByteBuffer中的内容
            stringBuilder.append(Charset.forName("UTF-8").decode(byteBuffer));
        }

        String request = stringBuilder.toString();
        //将channel再次注册到selector上 多次注册相当于刷新
        socketChannel.register(selector, SelectionKey.OP_READ);

        if (request.length() > 0) {
            // 广播给其他客户端
            fanoutHandler(selector, socketChannel, request);
        }
    }

    public static void fanoutHandler(Selector selector, SocketChannel sendChannel, String request) {
        //获取到所有已接入的客户端channel，通过selector就可以获取
        Set<SelectionKey> selectionKeys = selector.keys();
        //向所有channel广播
        selectionKeys.forEach(selectionKey -> {
            //获取目标的channel
            SocketChannel ClientChannel = (SocketChannel)selectionKey.channel();
            //不用给发送消息的客户端发送
            if (ClientChannel != sendChannel) {
                try {
                    // 将信息发送到给客户端
                    ClientChannel.write(Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
