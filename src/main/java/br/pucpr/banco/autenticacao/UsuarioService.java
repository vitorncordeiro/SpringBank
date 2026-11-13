package br.pucpr.banco.autenticacao;

import br.pucpr.banco.compartilhado.RecursoNaoEncontradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrar(String nome, String cpf, String senha) {
        String cpfNumerico = cpf == null ? "" : cpf.replaceAll("\\D", "");
        if (cpfNumerico.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        }
        if (repository.existsByCpf(cpfNumerico)) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        return repository.save(new Usuario(nome, cpfNumerico, passwordEncoder.encode(senha)));
    }

    public Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário " + id + " não encontrado"));
    }

    /** Lê o hash persistido e compara com a senha informada (base da validação de credenciais). */
    public boolean credenciaisValidas(String cpf, String senha) {
        return repository.findByCpf(cpf.replaceAll("\\D", ""))
                .filter(Usuario::isAtivo)
                .map(u -> passwordEncoder.matches(senha, u.getSenhaHash()))
                .orElse(false);
    }
}
