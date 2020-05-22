import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProtoFileSender {
    private static final byte SIGNAL_BYTE_FILE = 25;
    public static void sendFile(Path path, Channel channel, ChannelFutureListener finishListener) throws IOException {
        FileRegion region = new DefaultFileRegion(path.toFile(), 0, Files.size(path)); // показываем на весь файл (путь, позиция внутри файла, количество байт)

        // пересылаем сигнальный байт
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1);
        buf.writeByte(SIGNAL_BYTE_FILE);
        channel.writeAndFlush(buf);

        // пересылаем длину имени файла
        byte[] filenameBytes = path.getFileName().toString().getBytes(StandardCharsets.UTF_8);
        buf = ByteBufAllocator.DEFAULT.directBuffer(4);
        buf.writeInt(filenameBytes.length);
        channel.writeAndFlush(buf);

        buf = ByteBufAllocator.DEFAULT.directBuffer(filenameBytes.length);
        buf.writeBytes(filenameBytes);
        channel.writeAndFlush(buf);

        // пересылаем  размер файла
        buf = ByteBufAllocator.DEFAULT.directBuffer(8);
        buf.writeLong(Files.size(path));
        channel.writeAndFlush(buf);

        ChannelFuture transferOperationFuture = channel.writeAndFlush(region); // пересылаем само содержимое файла
        if (finishListener != null) {
            transferOperationFuture.addListener(finishListener);
        }
    }
}