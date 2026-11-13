package br.pucpr.banco.pagamento.controller;

import br.pucpr.banco.pagamento.dto.BoletoRequest;
import br.pucpr.banco.pagamento.dto.PagamentoResponse;
import br.pucpr.banco.pagamento.dto.PixRequest;
import br.pucpr.banco.pagamento.dto.SaldoRequest;
import br.pucpr.banco.pagamento.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping("/pix")
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponse pix(@Valid @RequestBody PixRequest r) {
        return PagamentoResponse.de(service.pagarPix(r.contaOrigemId(), r.valor(), r.codigoQr()));
    }

    @PostMapping("/boleto")
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponse boleto(@Valid @RequestBody BoletoRequest r) {
        return PagamentoResponse.de(service.pagarBoleto(r.contaOrigemId(), r.codigoBarras()));
    }

    @PostMapping("/saldo")
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponse saldo(@Valid @RequestBody SaldoRequest r) {
        return PagamentoResponse.de(service.pagarComSaldo(r.contaOrigemId(), r.valor(), r.descricao()));
    }

    @GetMapping("/{id}")
    public PagamentoResponse buscar(@PathVariable Long id) {
        return PagamentoResponse.de(service.buscar(id));
    }

    @GetMapping("/conta/{contaId}")
    public List<PagamentoResponse> porConta(@PathVariable Long contaId) {
        return service.listarPorConta(contaId).stream().map(PagamentoResponse::de).toList();
    }
}
