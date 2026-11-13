package br.pucpr.banco.conta.repository;

import br.pucpr.banco.conta.model.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findByContaIdOrderByIdAsc(Long contaId);
}
