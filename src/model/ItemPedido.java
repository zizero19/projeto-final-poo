package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemPedido {
    private Produto produto;
    private int quantidade;
    private BigDecimal subtotal;
    private String nomeProduto;
    private BigDecimal precoUnitario;

    public ItemPedido() {
    }

    public ItemPedido(Produto produto, int quantidade, BigDecimal subtotal) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.subtotal = subtotal;
        if (produto != null) {
            this.nomeProduto = produto.getNome();
            this.precoUnitario = produto.getPreco();
        }
    }

    public ItemPedido(Produto produto, int quantidade, double subtotal) {
        this(produto, quantidade, BigDecimal.valueOf(subtotal).setScale(2, RoundingMode.HALF_UP));
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
        if (produto != null) {
            this.nomeProduto = produto.getNome();
            if (this.precoUnitario == null) {
                this.precoUnitario = produto.getPreco();
            }
        }
    }

    public String getNomeProduto() {
        return nomeProduto != null ? nomeProduto : (produto != null ? produto.getNome() : "Produto removido");
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public BigDecimal getPrecoUnitario() {
        if (precoUnitario != null) {
            return precoUnitario.setScale(2, RoundingMode.HALF_UP);
        }
        return produto != null ? produto.getPreco() : BigDecimal.ZERO;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario == null ? null : precoUnitario.setScale(2, RoundingMode.HALF_UP);
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getSubtotal() {
        if (subtotal == null) {
            subtotal = calcularSubtotal();
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal == null ? BigDecimal.ZERO : subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    public void setSubtotal(double subtotal) {
        setSubtotal(BigDecimal.valueOf(subtotal));
    }

    public BigDecimal calcularSubtotal() {
        BigDecimal preco = getPrecoUnitario();
        return preco.multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
    }
}
