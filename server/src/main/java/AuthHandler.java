import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.exists;

public class AuthHandler extends ChannelInboundHandlerAdapter {

    private AuthService authservice;

    private boolean autorized;
    private final AuthService auth;


    public AuthHandler() {
        autorized = false;
        auth = new AuthService();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (autorized) {
                ctx.fireChannelRead(msg);
                return;
            }

            try {
//                if (msg instanceof AuthCommand) {
//                    AuthCommand com = (AuthCommand) msg;
//
//                    autorized = auth.checkPass(com.login, com.password);
//
//                    if (autorized) {
//                        Path dir = Paths.get(STORAGE_DIR, com.login);
//                        if (!exists(dir))
//                            createDirectory(dir);
//
//                        MainHandler handler = ctx.pipeline().get(MainHandler.class);
//                        handler.setUserDir(dir);
//
//                        ctx.writeAndFlush(AuthResult.ok());
//                    } else {
//                        ctx.writeAndFlush(AuthResult.fail());
//                    }
//                }
            } finally {
                ReferenceCountUtil.release(msg);
            }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    // РАБОТАЕТ, для теста
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        System.out.println(new AuthService().checkPass("Client2","2"));
    }

}
