import io.netty.channel.ChannelHandlerContext;

import java.nio.file.Paths;

public class ServerCommandReceiver extends CommandReceiver {
    @Override
    public void parseCommand(ChannelHandlerContext ctx, String cmd) throws Exception {
        if(cmd.startsWith("/request ")) {
            String fileToClientName = cmd.split("\\s")[1];
            FileSender.sendFile(Paths.get("server_repository", fileToClientName), ctx.channel(), null);
        }

        if(cmd.startsWith("/getFileList ")) {
            //TODO отправить список файлов
//            String fileToClientName = cmd.split("\\s")[1];
//
//            FileSender.sendFile(Paths.get("server_repository", fileToClientName), ctx.channel(), null);
        }
    }
}
