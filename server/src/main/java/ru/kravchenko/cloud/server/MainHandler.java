package ru.kravchenko.cloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.kravchenko.cloud.common.*;
import ru.kravchenko.cloud.server.database.AutoService;
import ru.kravchenko.cloud.server.database.ConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Kravchenko
 */

public class MainHandler extends ChannelInboundHandlerAdapter {


    private AutoService autoService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null) return;

            if (msg instanceof FileRequest) {
                FileRequest fileRequest = (FileRequest) msg;

                if ("CMD_getFiles".equals(fileRequest.getCDM_Command())) {
                    System.out.println("CMD_getFiles message from client");
                    FileListFromServerStorage fileListFromServerStorage = getFilesList(); // custom
                    ctx.writeAndFlush(fileListFromServerStorage);
                }

                if ("CMD_delFiles".equals(fileRequest.getCDM_Command())) {
                    System.out.println("CMD_delFiles message from client");
                    String fileNameMouseClick = fileRequest.getFilename();
                    Files.delete(Paths.get("storage/server_storage/" + fileNameMouseClick));
                    FileListFromServerStorage fileListFromServerStorage = getFilesList(); // обновление данных на сервере
                    ctx.writeAndFlush(fileListFromServerStorage);
                }


                if ("CMD_downloadFiles".equals(fileRequest.getCDM_Command())) {
                    System.out.println("CMD_downloadFiles message from client");
                    String fileNameMouseClick = fileRequest.getFilename();
                    System.out.println(fileNameMouseClick);
                    FileMessage fileMessage = new FileMessage(Paths.get("storage/server_storage/" + fileNameMouseClick)); // исправить на правильную логику имя метода
                    ctx.writeAndFlush(fileMessage);
                }

                if (Files.exists(Paths.get("storage/server_storage/" + fileRequest.getCDM_Command()))) { // исправить на правильную логику имя метода
                    FileMessage fileMessage = new FileMessage(Paths.get("storage/server_storage/" + fileRequest.getCDM_Command())); // исправить на правильную логику имя метода
                    ctx.writeAndFlush(fileMessage);
                }
            }

            if (msg instanceof FileMessage) {
                System.out.println("fly file from client " + msg );
                FileMessage fm = (FileMessage) msg;
                Files.write(Paths.get("storage/server_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                FileListFromServerStorage fileListFromServerStorage = getFilesList(); // обновление данных на сервере
                ctx.writeAndFlush(fileListFromServerStorage);
            }

            if (msg instanceof AutoMessageToServer) {  // Блок авторизации
                autoService  = new ConnectionManager();
                System.out.println("try autoriz send date to BD");
                AutoMessageToServer messageFromClient = (AutoMessageToServer) msg;
                AutoMessageToClient autoMessageToClient = new AutoMessageToClient();

                if (autoService.chekRegistry(messageFromClient.getLogin(), messageFromClient.getPassword()))
                {
                    autoMessageToClient.setChekRegistry(true);
                    ctx.writeAndFlush(autoMessageToClient);
                    System.out.println("welcome to cloud");
                }
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public FileListFromServerStorage getFilesList() throws IOException {
        List<String> filesList = new ArrayList<>();
        Files.list(Paths.get("storage/server_storage/")).map(p -> p.getFileName().toString()).forEach(o -> filesList.add(o));
        FileListFromServerStorage fileListFromServerStorage = new FileListFromServerStorage(filesList);
        return fileListFromServerStorage;
    }

}


//
//    @NotNull
//    private final static String CMD_PING = "ping";
//
//    @NotNull
//    private final static String CMD_LOGIN = "login";

//
//       if (CMD_PING.equals(message)) {
//               clientMessagePingEvent.fireAsync(new ClientPingEvent());
//               clientMessageInputEvent.fire(new ClientMessageInputEvent());
//               return;
//               }
//
//               if (CMD_LOGIN.equals(message)) {
//               clientLoginEvent.fire(new ClientLoginEvent());
//               return;
//               }
