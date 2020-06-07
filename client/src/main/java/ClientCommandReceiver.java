import io.netty.channel.ChannelHandlerContext;

import javax.naming.OperationNotSupportedException;

public class ClientCommandReceiver extends CommandReceiver {
    @Override
    public void parseCommand(ChannelHandlerContext ctx, String cmd) throws Exception {
        throw new OperationNotSupportedException("Мы не должны сюда попадать на клиенте");
    }
}