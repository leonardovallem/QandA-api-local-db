package com.vallem.Perguntas.API.controller;

import com.vallem.Perguntas.API.entity.Usuario;
import com.vallem.Perguntas.API.service.UsuarioService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@AllArgsConstructor
public class UsuarioController {
    private final UsuarioService service;

    @GetMapping
    public List<Usuario> list() throws Exception {
        return service.list();
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody Usuario usuario, UriComponentsBuilder uriBuilder) {
        try {
            int id = service.create(usuario);
            URI uri = uriBuilder.path("/usuarios/".concat(id+"")).buildAndExpand().toUri();
            return ResponseEntity.created(uri).body(usuario);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return service.delete(id) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
