package br.pucpr.banco.transferencia.controller;

import br.pucpr.banco.transferencia.dto.BancariaRequest;
import br.pucpr.banco.transferencia.dto.PixRequest;
import br.pucpr.banco.transferencia.dto.TransferenciaResponse;
import br.pucpr.banco.transferencia.service.TransferenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transferencias")
@RequiredArgsConstructor
public class TransferenciaController {

    private final TransferenciaService service;

    @PostMapping("/pix")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferenciaResponse pix(@Valid @RequestBody PixRequest r) {
        return TransferenciaResponse.de(service.transferirPix(r.contaOrigemId(), r.valor(), r.chavePix()));
    }

    @PostMapping("/ted")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferenciaResponse ted(@Valid @RequestBody BancariaRequest r) {
        return TransferenciaResponse.de(service.transferirTed(r.contaOrigemId(), r.valor(), r.banco(),
                r.agencia(), r.conta(), r.favorecido()));
    }

    @PostMapping("/doc")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferenciaResponse doc(@Valid @RequestBody BancariaRequest r) {
        return TransferenciaResponse.de(service.transferirDoc(r.contaOrigemId(), r.valor(), r.banco(),
                r.agencia(), r.conta(), r.favorecido()));
    }

    @GetMapping("/{id}")
    public TransferenciaResponse buscar(@PathVariable Long id) {
        return TransferenciaResponse.de(service.buscar(id));
    }

    @GetMapping("/conta/{contaId}")
    public List<TransferenciaResponse> porConta(@PathVariable Long contaId) {
        return service.listarPorConta(contaId).stream().map(TransferenciaResponse::de).toList();
    }
}
