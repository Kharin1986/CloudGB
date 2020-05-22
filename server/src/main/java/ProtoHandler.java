import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class ProtoHandler extends ChannelInboundHandlerAdapter {
    public enum State {
        IDLE, // ничего не делаем
        // Получение файла
        NAME_LENGTH, // Ждем длину имени файла
        NAME, // Ждем имя файла
        FILE_LENGTH, // Ждем  длину файла
        FILE // Ждем файл
        // Получение команды


    }

    private State currentState = State.IDLE;
    private int nextLength;
    private long fileLength;
    private long receivedFileLength; // сколько уже получили байт
    private BufferedOutputStream out;

    private final byte SIGNAL_BYTE_FILE = 25;
    private final byte SIGNAL_BYTE_GET_FILELIST = 30;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = ((ByteBuf) msg);
        while (buf.readableBytes() > 0) {
            if (currentState == State.IDLE) { // Если мы в режиме ожидания
                byte readed = buf.readByte();
                if (readed == SIGNAL_BYTE_FILE) { // И получили сигнальный байт на начало работы (шаг 0)
                    currentState = State.NAME_LENGTH; // переходим в режим ожидания длины имени (шаг 1)
                    receivedFileLength = 0L; // полученных байт - 0
                    System.out.println("STATE: Start file receiving");
                } else if (readed==SIGNAL_BYTE_GET_FILELIST){
                    getFileList();
                    }else {
                        System.out.println("ERROR: Invalid first byte - " + readed);
                        //TODO закрыть соединение
                    }
            }


            if (currentState == State.NAME_LENGTH) {
                if (buf.readableBytes() >= 4) {
                    System.out.println("STATE: Get filename length");
                    nextLength = buf.readInt();
                    currentState = State.NAME;
                }
            }

            if (currentState == State.NAME) {
                if (buf.readableBytes() >= nextLength) {
                    byte[] fileName = new byte[nextLength];
                    buf.readBytes(fileName);
                    System.out.println("STATE: Filename received - _" + new String(fileName, "UTF-8"));// для учета файлов с названием на кириллице
                    out = new BufferedOutputStream(new FileOutputStream("_" + new String(fileName)));
                    //TODO в реальном проекте без "_", здесь для удобства
                    currentState = State.FILE_LENGTH;
                }
            }

            if (currentState == State.FILE_LENGTH) {
                if (buf.readableBytes() >= 8) {
                    fileLength = buf.readLong();
                    System.out.println("STATE: File length received - " + fileLength);
                    currentState = State.FILE;
                }
            }

            if (currentState == State.FILE) {
                while (buf.readableBytes() > 0) {
                    out.write(buf.readByte());
                    receivedFileLength++;
                    if (fileLength == receivedFileLength) {
                        currentState = State.IDLE;
                        System.out.println("File received");
                        out.close();
                        break;
                    }
                }
            }
        }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }

    private void getFileList() {
        System.out.println("Запрошен список файлов");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
