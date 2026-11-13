package br.pucpr.banco.integracaobc.controller;

import br.pucpr.banco.integracaobc.dto.MensagemResponse;
import br.pucpr.banco.integracaobc.service.IntegracaoBancoCentralService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/integracao/mensagens")
public class IntegracaoController {

    private final IntegracaoBancoCentralService service;

    public IntegracaoController(IntegracaoBancoCentralService service) {
        this.service = service;
    }

    @GetMapping
    public List<MensagemResponse> listar() {
        return service.listar().stream().map(MensagemResponse::de).toList();
    }
}
