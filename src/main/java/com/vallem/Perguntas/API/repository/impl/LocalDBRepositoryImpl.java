package com.vallem.Perguntas.API.repository.impl;

import com.vallem.Perguntas.API.config.DataConfiguration;
import com.vallem.Perguntas.API.entity.KeyValuePairs.RegistroKeyValuePair;
import com.vallem.Perguntas.API.index.impl.HashExtensivel;
import com.vallem.Perguntas.API.repository.LocalDBRepository;
import com.vallem.Perguntas.API.repository.serialize.Registro;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class LocalDBRepositoryImpl<T extends Registro> implements LocalDBRepository<T> {
    private final Constructor<T> constructor;
    private final RandomAccessFile raf;
    private final HashExtensivel<RegistroKeyValuePair> he;
    public final String file;

    public LocalDBRepositoryImpl(Constructor<T> constructor, String file) throws Exception {
        DataConfiguration.provideFolders();

        this.constructor = constructor;
        this.file = "data/".concat(file);

        this.raf = new RandomAccessFile(this.file, "rw");

        he = new HashExtensivel<>(RegistroKeyValuePair.class.getConstructor(), 4,
                this.file.replace(".db", ".hash_d.db"),
                this.file.replace(".db", ".hash_c.db"));
    }

    @Override
    public int create(T obj) throws IOException {
        int objetoID = -1;

        raf.seek(0);
        if (raf.length() == 0)
            raf.writeInt(++objetoID);
        else
            objetoID = raf.readInt() + 1;

        obj.setId(objetoID);
        byte[] objetoBytes = obj.toByteArray();

        raf.seek(0);
        raf.writeInt(objetoID);

        long lastPos = raf.length();
        raf.seek(lastPos);
        raf.writeInt(objetoBytes.length);
        raf.write(objetoBytes);

        try {
            he.create(new RegistroKeyValuePair(objetoID, lastPos));
        } catch (Exception e) {
            System.out.println("ID #" + objetoID + ": " + e.getMessage());
        }

        return objetoID;
    }

    @Override
    public T read(int id) throws Exception {
        T objeto = null;

        raf.seek(4);
        RegistroKeyValuePair pcv = he.read(id);
        long objPosition = pcv == null ? -1 : pcv.getPosicao();

        if (objPosition > -1) {
            raf.seek(objPosition);

            int size = raf.readInt();
            byte[] ba = new byte[size]; // instancia array de bytes do tamanho do registro

            long initialPos = raf.getFilePointer(); // guarda a posição inicial do registro
            boolean deleted = raf.readBoolean();

            raf.seek(initialPos); // retorno à posição inicial do registro após ler ID e lápide
            if (!deleted) {
                raf.read(ba);

                objeto = constructor.newInstance();
                objeto.fromByteArray(ba); // constroi o objeto

                raf.seek(raf.length()); // invalida a condição do while
            } else
                System.out.println("Registro deletado");
        }

        return objeto;
    }

    @Override
    public List<T> readAll() throws Exception {
        List<T> lista = new ArrayList<T>();
        int intSize = Integer.SIZE / 8;
        if (raf.length() > intSize) {
            raf.seek(intSize);

            int size;
            byte[] ba;
            long initialPos;
            boolean deleted;
            T objeto = null;
            while (raf.getFilePointer() < raf.length()) {
                size = raf.readInt();
                ba = new byte[size];
                initialPos = raf.getFilePointer();
                deleted = raf.readBoolean();
                raf.seek(initialPos);
                if (!deleted) {
                    raf.read(ba);
                    objeto = constructor.newInstance();
                    objeto.fromByteArray(ba);
                    lista.add(objeto);
                } else {
                    raf.skipBytes(size);
                }
            }
        }

        return lista;
    }

    @Override
    public boolean update(T obj) {
        boolean status = true;

        try {
            raf.seek(4);
            long objPosition = he.read(obj.getId()).getPosicao();

            if (objPosition > -1) {
                raf.seek(objPosition);

                int size = raf.readInt();
                byte[] ba = new byte[size]; // instancia array de bytes do tamanho do registro

                long initialPos = raf.getFilePointer(); // guarda a posição inicial do registro
                raf.skipBytes(4); // pula o id do registro
                long lapidePos = raf.getFilePointer(); // guarda a posição da lápide do registro
                boolean deleted = raf.readBoolean();

                if (!deleted) {
                    byte[] updatedBa = obj.toByteArray();

                    // caso o novo registro seja maior do que o original
                    if (updatedBa.length > ba.length) {
                        raf.seek(lapidePos);
                        raf.writeBoolean(true); // marca o antigo registro como excluído

                        long novoRegistroPos = raf.length();

                        raf.seek(novoRegistroPos); // vai para o final do arquivo
                        raf.writeInt(updatedBa.length); // armazena o tamanho do novo registro

                        he.update(new RegistroKeyValuePair(obj.getId(), novoRegistroPos));
                    } else
                        raf.seek(initialPos); // volta ao inicio do registro caso o novo seja menor ou igual

                    raf.write(updatedBa); // escreve o novo registro
                }
            }
        } catch (Exception e) {
            status = false;
            System.out.println("Erro durante a atualização do registro.");
            e.printStackTrace();
        }

        return status;
    }

    @Override
    public boolean delete(int id) {
        boolean status = true;

        try {
            raf.seek(4);
            long objPosition = he.read(id).getPosicao();

            if (objPosition > -1) {
                raf.seek(objPosition);
                raf.skipBytes(4); // pula o id do registro
                long lapidePos = raf.getFilePointer(); // guarda a posição da lápide do registro
                boolean deleted = raf.readBoolean();

                // invalida a condição do while
                if (!deleted) {
                    raf.seek(lapidePos); // volta à posição da lápide e marca como true
                    raf.writeBoolean(true);

                    he.delete(id);
                }
            }
        } catch (Exception e) {
            status = false;
            System.out.println("Erro durante a remoção do registro.");
            e.printStackTrace();
        }

        return status;
    }
}
