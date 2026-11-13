package br.pucpr.banco.integracaobc;

import br.pucpr.banco.compartilhado.FalhaIntegracaoException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IntegracaoBancoCentralService {

    private final MensagemBancoCentralRepository repository;
    private final Map<OperacaoBc, AdaptadorBancoCentral> adaptadores;

    public IntegracaoBancoCentralService(MensagemBancoCentralRepository repository,
                                         List<AdaptadorBancoCentral> adaptadores) {
        this.repository = repository;
        this.adaptadores = adaptadores.stream()
                .collect(Collectors.toMap(AdaptadorBancoCentral::operacao, Function.identity()));
    }

    public String comunicar(OperacaoBc operacao, String chaveIdempotencia, String requisicao) {
        Optional<MensagemBancoCentral> existente = repository.findByChaveIdempotencia(chaveIdempotencia);

        if (existente.isPresent() && existente.get().getStatus() == StatusMensagemBc.CONFIRMADA) {
            return existente.get().getResposta();
        }

        MensagemBancoCentral mensagem = existente
                .orElseGet(() -> repository.save(new MensagemBancoCentral(chaveIdempotencia, operacao, requisicao)));

        try {
            String resposta = adaptadores.get(operacao).enviar(requisicao);
            mensagem.confirmar(resposta);
            repository.save(mensagem);
            return resposta;
        } catch (RuntimeException e) {
            mensagem.falhar(e.getMessage());
            repository.save(mensagem);
            throw new FalhaIntegracaoException("Falha ao comunicar " + operacao + ": " + e.getMessage(), e);
        }
    }

    public TituloBoleto consultarBoleto(String codigoBarras) {
        String chave = "BOLETO-CONSULTA-" + UUID.randomUUID();
        String resposta = comunicar(OperacaoBc.BOLETO, chave, "CONSULTA|" + codigoBarras);
        String[] partes = resposta.split(";");
        return new TituloBoleto(
                new BigDecimal(partes[0].substring("valor=".length())),
                LocalDate.parse(partes[1].substring("vencimento=".length())));
    }

    public List<MensagemBancoCentral> listar() {
        return repository.findAllByOrderByIdDesc();
    }
}
