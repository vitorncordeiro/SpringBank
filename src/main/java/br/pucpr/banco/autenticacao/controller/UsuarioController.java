package br.pucpr.banco.autenticacao.controller;

import br.pucpr.banco.autenticacao.dto.NovoUsuarioRequest;
import br.pucpr.banco.autenticacao.dto.UsuarioResponse;
import br.pucpr.banco.autenticacao.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse cadastrar(@Valid @RequestBody NovoUsuarioRequest req) {
        return UsuarioResponse.de(service.cadastrar(req.nome(), req.cpf(), req.senha()));
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return UsuarioResponse.de(service.buscar(id));
    }
}
