package chat.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * 客户端
 *
 * @Author lixiang
 * @Date 2020/12/25
 */
public class ChatClient {


    public void clientStart(String clientId) throws IOException {
        //连接到服务器
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 1216));

        /**
         * 从服务器读取消息
         */
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        //读就绪事件 表示通道中已经有了可读的数据，可以执行读操作了
        socketChannel.register(selector, SelectionKey.OP_READ);
        //对selector进行论询
        new Thread(new ClientHandler(selector)).start();

        /**
         * 向服务器发送消息
         */
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String request = scanner.nextLine();
            if (request != null && request.length() > 0) {
                //发送的是byte[]数组，仍然需要编解码
                socketChannel.write(Charset.forName("UTF-8").encode(clientId + "    " + df.format(now) + "  " + request));
            }
        }
    }
}
