package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import model.enums.CategoriaProduto;

public class Produto {
    private Long id;
    private String nome;
    private CategoriaProduto categoria;
    private BigDecimal preco;
    private int qtdEstoque;

    public Produto() {
    }

    public Produto(String nome, CategoriaProduto categoria, BigDecimal preco, int quantidadeEstoque) {
        this.nome = nome;
        this.categoria = categoria;
        setPreco(preco);
        setQtdEstoque(quantidadeEstoque);
    }

    public Produto(String nome, CategoriaProduto categoria, double preco, int quantidadeEstoque) {
        this(nome, categoria, BigDecimal.valueOf(preco).setScale(2, RoundingMode.HALF_UP), quantidadeEstoque);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public CategoriaProduto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProduto categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPreco() {
        return preco == null ? BigDecimal.ZERO : preco;
    }

    public void setPreco(BigDecimal preco) {
        if (preco == null) {
            this.preco = BigDecimal.ZERO;
            return;
        }
        this.preco = preco.setScale(2, RoundingMode.HALF_UP);
    }

    public void setPreco(double preco) {
        setPreco(BigDecimal.valueOf(preco));
    }

    public int getQtdEstoque() {
        return qtdEstoque;
    }

    public void setQtdEstoque(int quantidadeEstoque) {
        if (quantidadeEstoque >= 0) {
            this.qtdEstoque = quantidadeEstoque;
        } else {
            System.out.println("Quantidade de estoque inválida.");
        }
    }

    public void diminuirEstoque(int quantidade) {
        if (quantidade > 0 && quantidade <= qtdEstoque) {
            qtdEstoque -= quantidade;
        } else {
            System.out.println("Quantidade insuficiente em estoque.");
        }
    }

    public void aumentarEstoque(int quantidade) {
        if (quantidade > 0) {
            qtdEstoque += quantidade;
        } else {
            System.out.println("Quantidade inválida.");
        }
    }

    public boolean possuiEstoque() {
        return qtdEstoque > 0;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                "\nNome: " + nome +
                "\nCategoria: " + categoria +
                "\nPreço: R$ " + getPreco().setScale(2, RoundingMode.HALF_UP) +
                "\nEstoque: " + qtdEstoque;
    }
}