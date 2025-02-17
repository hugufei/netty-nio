// 接受連線請求線程
package com.hugufei.reactor.many;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements Runnable {

    private final ServerSocketChannel ssc;
    private final Selector selector;

    public Acceptor(Selector selector, ServerSocketChannel ssc) {
        this.ssc = ssc;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            SocketChannel sc = ssc.accept(); // 接受client連線請求
            System.out.println(sc.socket().getRemoteSocketAddress().toString() + " is connected.");

            if (sc != null) {
                sc.configureBlocking(false); // 設置為非阻塞
                SelectionKey sk = sc.register(selector, SelectionKey.OP_READ); // SocketChannel向selector註冊一個OP_READ事件，然後返回該通道的key
//                    System.out.println(sk.selector()==selector);
                selector.wakeup(); // 使一個阻塞住的selector操作立即返回
                sk.attach(new TCPHandler(sk, sc)); // 給定key一個附加的TCPHandler對象
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}