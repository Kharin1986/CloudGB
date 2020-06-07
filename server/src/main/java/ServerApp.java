import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerApp {
    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();// пул потоков, ожидающий клиентов
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // пул потоков, сложные задачи
        try {
            ServerBootstrap b = new ServerBootstrap(); //настройки сервера
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception { // когда к нам кто-то подключается, мы инициализаируем его канал
                            ch.pipeline().addLast(
                                    new AuthHandler(),
                                    new ProtoHandler("server_repository", new ServerCommandReceiver()));
                        }
                    });
            ChannelFuture f = b.bind(8189).sync(); // future - информация о работе канала
            //sync запускает подключение и выдает объект типа ChannelFuture, работает типа await - пока сервер работает, мы дальше не идем
            f.channel().closeFuture().sync(); // ждем завершения работы сервера
        } finally {
            workerGroup.shutdownGracefully(); // когда завершилась работа сервера освобождаем пулы потоков
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new ServerApp().run();
    }
}
