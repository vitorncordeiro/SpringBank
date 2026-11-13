package br.pucpr.banco.integracaobc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/integracao/mensagens")
public class IntegracaoController {

    public record MensagemResponse(Long id, String chaveIdempotencia, OperacaoBc operacao, StatusMensagemBc status,
                                   String requisicao, String resposta, LocalDateTime criadaEm) {
        static MensagemResponse de(MensagemBancoCentral m) {
            return new MensagemResponse(m.getId(), m.getChaveIdempotencia(), m.getOperacao(), m.getStatus(),
                    m.getRequisicao(), m.getResposta(), m.getCriadaEm());
        }
    }

    private final IntegracaoBancoCentralService service;

    public IntegracaoController(IntegracaoBancoCentralService service) {
        this.service = service;
    }

    @GetMapping
    public List<MensagemResponse> listar() {
        return service.listar().stream().map(MensagemResponse::de).toList();
    }
}
