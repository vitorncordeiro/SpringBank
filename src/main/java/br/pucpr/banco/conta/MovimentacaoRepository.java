package br.pucpr.banco.conta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    /** "ContaId" navega pela associação: movimentacoes.conta_id. */
    List<Movimentacao> findByContaIdOrderByIdAsc(Long contaId);
}
