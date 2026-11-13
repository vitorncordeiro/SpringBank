package br.pucpr.banco.autenticacao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    public record NovoUsuarioRequest(@NotBlank String nome, @NotBlank String cpf, @NotBlank String senha) {
    }

    /** Nunca devolve o hash da senha. */
    public record UsuarioResponse(Long id, String nome, String cpf, boolean ativo) {
        static UsuarioResponse de(Usuario u) {
            return new UsuarioResponse(u.getId(), u.getNome(), u.getCpf(), u.isAtivo());
        }
    }

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

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
