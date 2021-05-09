package com.vallem.Perguntas.API.service;

import com.vallem.Perguntas.API.entity.Usuario;
import com.vallem.Perguntas.API.repository.LocalDBRepository;
import com.vallem.Perguntas.API.repository.impl.LocalDBRepositoryImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UsuarioService {
    private final LocalDBRepository<Usuario> repository;

    public UsuarioService() throws Exception {
        this.repository = new LocalDBRepositoryImpl<>(Usuario.class.getConstructor(), "usuarios.db");
    }

    public int create(Usuario usuario) throws IOException {
        return repository.create(usuario);
    }

    public Usuario retrieve(int id) throws Exception {
        return repository.read(id);
    }

    public List<Usuario> list() throws Exception {
        return repository.readAll();
    }

    public boolean update(Usuario usuario) {
        return repository.update(usuario);
    }

    public boolean delete(int id) {
        return repository.delete(id);
    }
}
