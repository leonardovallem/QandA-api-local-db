package com.vallem.Perguntas.API.repository;

import com.vallem.Perguntas.API.entity.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends LocalDBRepository<Usuario> {
}
