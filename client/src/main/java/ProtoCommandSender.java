import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ProtoCommandSender {
    private static final byte SIGNAL_BYTE_GET_FILELIST = 30;
    private static final byte SIGNAL_BYTE_GET_FILE = 35;
// TODO встроить в метод тип команды

    public static void sendCommand(Channel channel, ChannelFutureListener finishListener){
        // пересылаем сигнальный байт
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer(1);
        buf.writeByte(SIGNAL_BYTE_GET_FILELIST);
        ChannelFuture transferOperationFuture = channel.writeAndFlush(buf); // пересылаем команду
        if (finishListener != null) {
            transferOperationFuture.addListener(finishListener); // получаем ответ
        }
    }
}
