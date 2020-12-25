package test;

import chat.client.ChatClient;

import java.io.IOException;

/**
 * 先启动服务器端,再启动客户端
 *
 * @Author lixiang
 * @Date 2020/12/25
 */
public class ClientC {
    public static void main(String[] args) throws IOException {
        new ChatClient().clientStart("Sheep");
    }
}
