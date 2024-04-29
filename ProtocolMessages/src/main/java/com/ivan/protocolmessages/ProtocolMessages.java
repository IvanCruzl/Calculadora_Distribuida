package com.ivan.protocolmessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProtocolMessages {
    // 2 bytes
    Short tipoOperacion;
    // longitud de la informacion del mensaje
    // informacion del mensaje
    byte[] datos;

    public Short getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(Short tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public byte[] getDatos() {
        return datos;
    }

    public void setDatos(byte[] datos) {
        this.datos = datos;
    }

    @Override
    public String toString() {
        return "ProtocolMessages{" + "tipoOperacion=" + tipoOperacion + ", datos=" + new String(datos) + '}';
    }
}
