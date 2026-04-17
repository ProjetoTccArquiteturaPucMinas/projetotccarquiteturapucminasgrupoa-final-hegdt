package com.example.marketplace.model;
/*Desconto adicional por categoria
- CAPINHA = 3%
- CARREGADOR = 5%
- FONE = 3%
- PELICULA = 2%
- SUPORTE = 2% */

public enum CategoriaProduto {
    CAPINHA(0.03),
    CARREGADOR(0.05),
    FONE(0.03),
    PELICULA(0.02),
    SUPORTE(0.02)
}

private final double percentualDesconto;

    CategoriaProduto(double percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public double getPercentualDesconto() {
        return percentualDesconto;
    }

