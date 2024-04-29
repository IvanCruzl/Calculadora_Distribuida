package com.ivan.nodo2;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;



public class DecoderEncoder {

    public static ProtocolMessages read(DataInputStream dis) throws IOException {
        ProtocolMessages message = new ProtocolMessages();
        message.setOperationType(dis.readShort());
        message.setMessageSize(dis.readInt());
        byte[] data = new byte[message.getMessageSize()];
        dis.readFully(data);
        message.setMessageData(data);
        System.out.println("Mensaje recibido: " + message);
        return message;
    }

    public static void write(DataOutputStream dos, ProtocolMessages message) throws IOException {
        dos.writeShort(message.getOperationType());
        dos.writeInt(message.getMessageSize());
        dos.write(message.getMessageData());
        System.out.println("Mensaje enviado: " + message);
    }
}
