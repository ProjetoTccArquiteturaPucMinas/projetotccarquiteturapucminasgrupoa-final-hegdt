package com.example.marketplace.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.marketplace.model.ItemCarrinho;
import com.example.marketplace.model.Produto;
import com.example.marketplace.model.ResumoCarrinho;
import com.example.marketplace.model.SelecaoCarrinho;
import com.example.marketplace.repository.ProdutoRepository;

@Service
public class ServicoCarrinho {

    private final ProdutoRepository repositorioProdutos;

    public ServicoCarrinho(ProdutoRepository repositorioProdutos) {
        this.repositorioProdutos = repositorioProdutos;
    }

    public ResumoCarrinho construirResumo(List<SelecaoCarrinho> selecoes) {

        List<ItemCarrinho> itens = new ArrayList<>();

        // =========================
        // Monta os itens do carrinho
        // =========================
        for (SelecaoCarrinho selecao : selecoes) {
            Produto produto = repositorioProdutos.buscarPorId(selecao.getProdutoId())
                    .orElseThrow(
                            () -> new IllegalArgumentException("Produto não encontrado: " + selecao.getProdutoId()));

            itens.add(new ItemCarrinho(produto, selecao.getQuantidade()));
        }
        
        /* ## Regras que precisam ser implementadas
            ### 1. Desconto por quantidade total de itens
            - 1 item = 0%
            - 2 itens = 5%
            - 3 itens = 7%
            - 4 ou mais itens = 10%

            ### 2. Desconto adicional por categoria
            - CAPINHA = 3%
            - CARREGADOR = 5%
            - FONE = 3%
            - PELICULA = 2%
            - SUPORTE = 2%

            **Importante:** O desconto de categoria é aplicado **por item**, não por categoria única. 
            Se o carrinho tiver 3 capinhas, o desconto de categoria será 3% + 3% + 3% = 9%.
        */

        // =========================
        // Calcula descontos por quantidade de itens
        // =========================

        int quantidadeTotal = itens.stream()
                .mapToInt(ItemCarrinho::getQuantidade)
                .sum();
        if(quantidadeTotal ==1){
            percentualDesconto = BigDecimal.ZERO;
        } else if(quantidadeTotal == 2){
            percentualDesconto = new BigDecimal("0.05");
        } else if(quantidadeTotal == 3){
            percentualDesconto = new BigDecimal("0.07");
        } else {
            percentualDesconto = new BigDecimal("0.10");
        }

        // =========================
        // Calcula valor com os descontos por categoria
        // =========================
        
        BigDecimal valorComDesconto = itens.stream()
                .map(ItemCarrinho::calcularDescontoCategoria)
                .reduce(BigDecimal.ZERO, BigDecimal::add);



        // =========================
        // Calcula subtotal
        // =========================
        BigDecimal subtotal = itens.stream()
                .map(ItemCarrinho::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

                
        // =========================
        //Calcula total de desconto aplicado
        // =========================
        BigDecimal valorDesconto = subtotal.multiply(percentualDesconto).add(subtotal.subtract(valorComDesconto));

        return new ResumoCarrinho(itens, subtotal, percentualDesconto, valorDesconto, total);
    }
}
