package br.pucpr.banco.autenticacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** O Spring Data gera a implementação em tempo de execução, a partir dos nomes dos métodos. */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}
