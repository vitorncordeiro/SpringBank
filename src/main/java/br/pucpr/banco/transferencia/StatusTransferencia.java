package br.pucpr.banco.transferencia;

public enum StatusTransferencia {
    INICIADA,
    VALOR_RESERVADO,
    CONFIRMADA_BC,
    CONCLUIDA,
    RECUSADA_SALDO_INSUFICIENTE,
    FALHA
}
