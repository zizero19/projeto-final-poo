package repository;

import java.util.ArrayList;
import java.util.List;

import model.Produto;

public class ProdutoRepository {
    private List<Produto> produtos;

    public ProdutoRepository() {
        produtos = new ArrayList<>();
    }

    public boolean salvarProduto(Produto produto) {
        if (produto == null) {
            return false;
        }

        if (buscarProduto(produto.getId()) != null) {
            return false;
        }

        produtos.add(produto);
        return true;
    }

    public List<Produto> listarProdutos() {
        return produtos;
    }

    public Produto buscarProduto(int id) {
        for (Produto produto : produtos) {
            if (produto.getId() == id) {
                return produto;
            }
        }
        return null;
    }

    public Produto buscarProduto(String nome) {
        for (Produto produto : produtos) {
            if (produto.getNome().equalsIgnoreCase(nome)) {
                return produto;
            }
        }
        return null;
    }

    public boolean excluirProduto(int id) {
        Produto produto = buscarProduto(id);

        if (produto != null) {
            produtos.remove(produto);
            return true;
        }

        return false;
    }

}