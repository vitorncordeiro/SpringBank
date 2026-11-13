package br.pucpr.banco.autenticacao.service;

import br.pucpr.banco.autenticacao.model.Usuario;
import br.pucpr.banco.autenticacao.repository.UsuarioRepository;
import br.pucpr.banco.compartilhado.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario cadastrar(String nome, String cpf, String senha) {
        String cpfNumerico = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (cpfNumerico.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        }
        if (repository.existsByCpf(cpfNumerico)) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        return repository.save(Usuario.builder()
                .nome(nome)
                .cpf(cpfNumerico)
                .senhaHash(passwordEncoder.encode(senha))
                .build());
    }

    public Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário " + id + " não encontrado"));
    }

    public boolean credenciaisValidas(String cpf, String senha) {
        return repository.findByCpf(cpf.replaceAll("\\D", ""))
                .filter(Usuario::isAtivo)
                .map(u -> passwordEncoder.matches(senha, u.getSenhaHash()))
                .orElse(false);
    }
}
