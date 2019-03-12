package ru.kravchenko.cloud.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.kravchenko.cloud.common.AbstractMessage;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Roman Kravchenko
 */

public class Network {

    private static Socket socket;

    private static ObjectEncoderOutputStream out;

    private static ObjectDecoderInputStream in;

    public static  void start() {
        try {
            socket = new Socket("localhost", 8189);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean sendMessage(AbstractMessage message) {
        try {
            out.writeObject(message);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static AbstractMessage readObject() throws ClassNotFoundException, IOException {
        Object object = in.readObject();
        return (AbstractMessage) object;
    }

}
