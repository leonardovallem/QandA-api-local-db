package com.vallem.Perguntas.API.repository;

import org.springframework.data.repository.Repository;

import java.io.IOException;
import java.util.List;

@org.springframework.stereotype.Repository
public interface LocalDBRepository<T> extends Repository<T, Integer> {
    int create(T obj) throws IOException;

    T read(int id) throws Exception;

    List<T> readAll() throws Exception;

    boolean update(T obj);

    boolean delete(int id);
}
