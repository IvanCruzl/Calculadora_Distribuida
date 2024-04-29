package com.ivan.cliente2;

import java.util.Arrays;

public class ProtocolMessages {
    private short operationType;
    private int messageSize;
    private byte[] messageData;

    public short getOperationType() {
        return operationType;
    }

    public void setOperationType(short operationType) {
        this.operationType = operationType;
    }

    public int getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    public byte[] getMessageData() {
        return messageData;
    }

    public void setMessageData(byte[] messageData) {
        this.messageData = messageData;
    }

    @Override
    public String toString() {
        return "ProtocolMessage{" +
                "operationType=" + operationType +
                ", messageSize=" + messageSize +
                ", messageData=" + Arrays.toString(messageData) +
                '}';
    }
}

