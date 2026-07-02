package Models;

import java.util.ArrayList;
import java.util.List;

public class Estoque {

    private List<Produto> produtos;

    public Estoque() {
        this.produtos = new ArrayList<>();
    }

    public Estoque(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
    }

    public void removerProduto(int id) {
        Produto produto = buscarProduto(id);

        if (produto != null) {
            produtos.remove(produto);
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    public Produto buscarProduto(int id) {
        for (Produto produto : produtos) {
            if (produto.getId() == id) {
                return produto;
            }
        }
        return null;
    }

    public boolean verificarDisponibilidade(int id) {
        Produto produto = buscarProduto(id);

        return produto != null && produto.possuiEstoque();
    }

    public void atualizarQuantidade(int id, int novaQuantidade) {
        Produto produto = buscarProduto(id);

        if (produto != null) {
            produto.setQuantidadeEstoque(novaQuantidade);
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    public void atualizarProduto(Produto produtoAtualizado) {
        Produto produto = buscarProduto(produtoAtualizado.getId());

        if (produto != null) {
            produto.setNome(produtoAtualizado.getNome());
            produto.setCategoria(produtoAtualizado.getCategoria());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("=== ESTOQUE ===\n");

        for (Produto produto : produtos) {
            sb.append(produto).append("\n\n");
        }

        return sb.toString();
    }
}