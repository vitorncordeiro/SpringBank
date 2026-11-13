package br.pucpr.banco.conta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    boolean existsByAgenciaAndNumero(String agencia, String numero);

    List<Conta> findByUsuarioId(Long usuarioId);
}
