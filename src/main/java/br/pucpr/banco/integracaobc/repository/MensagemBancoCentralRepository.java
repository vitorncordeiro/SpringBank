package br.pucpr.banco.integracaobc.repository;

import br.pucpr.banco.integracaobc.model.MensagemBancoCentral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MensagemBancoCentralRepository extends JpaRepository<MensagemBancoCentral, Long> {
    Optional<MensagemBancoCentral> findByChaveIdempotencia(String chaveIdempotencia);

    List<MensagemBancoCentral> findAllByOrderByIdDesc();
}
