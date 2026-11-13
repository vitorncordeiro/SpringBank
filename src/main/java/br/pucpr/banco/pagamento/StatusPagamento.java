package br.pucpr.banco.pagamento;

public enum StatusPagamento {
    INICIADO,
    VALOR_RESERVADO,
    CONFIRMADO_BC,
    PAGO,
    RECUSADO_SALDO_INSUFICIENTE,
    FALHA
}
