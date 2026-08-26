package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemPedido {
    private Produto produto;
    private int quantidade;
    private BigDecimal subtotal;

    public ItemPedido() {
    }

    public ItemPedido(Produto produto, int quantidade, BigDecimal subtotal) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.subtotal = subtotal;
    }

    public ItemPedido(Produto produto, int quantidade, double subtotal) {
        this(produto, quantidade, BigDecimal.valueOf(subtotal).setScale(2, RoundingMode.HALF_UP));
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
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
        if (produto == null || produto.getPreco() == null) {
            return BigDecimal.ZERO;
        }
        return produto.getPreco().multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
    }
}
