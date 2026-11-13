package br.pucpr.banco.transferencia.repository;

import br.pucpr.banco.transferencia.model.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {
    List<Transferencia> findByContaOrigemIdOrderByIdDesc(Long contaOrigemId);
}
