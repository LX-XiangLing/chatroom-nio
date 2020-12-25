package chat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 服务器端
 *
 * @Author lixiang
 * @Date 2020/12/25
 */
public class ChatServer {
    /**
     * 启动方法
     *
     * @throws IOException
     */
    public void serverStart() throws IOException {
        //创建selector
        Selector selector = Selector.open();
        //通过ServerSocketChannel创建channel通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //**设置channel为非阻塞** 关键点
        serverSocketChannel.configureBlocking(false);
        //为serverSocketChannel绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 1216));
        /**
         * 将serverSocketChannel注册到selector上，监听连接事件
         * SelectionKey选择键
         * OP_ACCEPT —— 接收连接继续事件 表示服务器监听到了客户连接，服务器可以接收这个连接了
         * OP_CONNECT —— 连接就绪事件 表示客户与服务器的连接已经建立成功
         * OP_READ —— 读就绪事件 表示通道中已经有了可读的数据，可以执行读操作了（通道目前有数据，可以进行读操作了）
         * OP_WRITE —— 写就绪事件 表示已经可以向通道写数据了（通道目前可以用于写操作）
         */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动成功");
        while (true){
            //获取可用channel数量
            int channelCount = selector.select();
            if (channelCount==0){
                continue;
            }
            //获取注册到select上的所有可用channel的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey = iterator.next();
                //根据类型处理
                if (selectionKey.isAcceptable()) {
                    ServerHandler.acceptHandler(serverSocketChannel, selector);
                }

                if (selectionKey.isReadable()) {
                    ServerHandler.readHandler(selectionKey, selector);
                }
                //处理完成移除
                iterator.remove();
            }
        }

    }
}
