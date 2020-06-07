import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ProtoHandler extends ChannelInboundHandlerAdapter {
    private enum Status {
        IDLE, FILE, COMMAND
    }

    private FileReceiver fileReceiver;
    private CommandReceiver commandReceiver;
    private Status currentStatus;
    private Runnable finishOperation = () -> {
        System.out.println("Операция завершена");
        currentStatus = Status.IDLE;
    };

    public ProtoHandler(String rootDir, CommandReceiver commandReceiver) {
        this.currentStatus = Status.IDLE;
        this.fileReceiver = new FileReceiver(rootDir);
        this.commandReceiver = commandReceiver;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ctx - вся информация о соединении, через него можно поработать с конвейером, байтбуфером и т.д.
        //msg - то, что вам пришло
        ByteBuf buf = ((ByteBuf) msg);
        while (buf.readableBytes() > 0) { // есть ли что-то, что мы не вычитали из буфера?
            if (currentStatus == Status.IDLE) {
                byte controlByte = buf.readByte();
                if (controlByte == CommandList.FILE_SIGNAL_BYTE) {
                    currentStatus = Status.FILE;
                    fileReceiver.startReceive();
                } else if (controlByte == CommandList.CMD_SIGNAL_BYTE) {
                    currentStatus = Status.COMMAND;
                    commandReceiver.startReceive();
                }
            }
            if (currentStatus == Status.FILE) {
                fileReceiver.receive(ctx, buf, finishOperation);
            }
            if (currentStatus == Status.COMMAND) {
                commandReceiver.receive(ctx, buf, finishOperation);
            }
        }
        if (buf.readableBytes() == 0) {
            buf.release(); // после завершения работ освобождаем байтбуфер
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // обязательно в хэндлерах обрабатывать исключения, иначе произойдет ошибка, а мы о ней не будем знать
        cause.printStackTrace();
        ctx.close();
    }
}
