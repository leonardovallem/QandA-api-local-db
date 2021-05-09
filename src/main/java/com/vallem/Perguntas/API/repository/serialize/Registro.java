package com.vallem.Perguntas.API.repository.serialize;

import java.io.IOException;

public interface Registro {
    int getId();
    void setId(int n);
    byte[] toByteArray() throws IOException;
    void fromByteArray(byte[] ba) throws IOException;
}
