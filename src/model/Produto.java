package model;

import model.enums.CategoriaProduto;

public class Produto {
    private int id;
    private String nome;
    private CategoriaProduto categoria;
    private double preco;
    private int quantidadeEstoque;

    public Produto() {

    }

    public Produto(int id, String nome, CategoriaProduto categoria, double preco, int quantidadeEstoque) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        if (preco > 0) {
            this.preco = preco;
        } else {
            System.out.println("Preço inválido.");
        }
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        if (quantidadeEstoque >= 0) {
            this.quantidadeEstoque = quantidadeEstoque;
        } else {
            System.out.println("Quantidade de estoque inválida.");
        }
    }

    public void diminuirEstoque(int quantidade) {
        if (quantidade > 0 && quantidade <= quantidadeEstoque) {
            quantidadeEstoque -= quantidade;
        } else {
            System.out.println("Quantidade insuficiente em estoque.");
        }
    }

    public void aumentarEstoque(int quantidade) {
        if (quantidade > 0) {
            quantidadeEstoque += quantidade;
        } else {
            System.out.println("Quantidade inválida.");
        }
    }

    public boolean possuiEstoque() {
        return quantidadeEstoque > 0;
    }

    public void atualizarPreco(double novoPreco) {
        if (novoPreco > 0) {
            preco = novoPreco;
        } else {
            System.out.println("Novo preço inválido.");
        }
    }

    @Override
    public String toString() {
        return "ID: " + id +
                "\nNome: " + nome +
                "\nCategoria: " + categoria +
                "\nPreço: R$ " + String.format("%.2f", preco) +
                "\nEstoque: " + quantidadeEstoque;
    }

}