package chat.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author lixiang
 * @Date 2020/12/25
 */
public class ClientHandler implements Runnable {
    private Selector selector;
    public ClientHandler(Selector selector) {this.selector = selector;}
    @Override
    public void run() {
        try {
            while (true) {
                //获取可用channel数量
                int channelCount = selector.select();
                if (channelCount == 0) {
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey selectionKey = iterator.next();
                    //对可读进行处理
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                    //处理完成移除
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector)
            throws IOException {
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
        String response = stringBuilder.toString();
        //将channel再次注册到selector上 多次注册相当于刷新
        socketChannel.register(selector, SelectionKey.OP_READ);
        //将消息打印到本地
        if (response.length() > 0) {
            System.out.println(response);
        }
    }

}
