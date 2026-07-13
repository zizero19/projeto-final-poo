package repository;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import model.Produto;

public class ProdutoRepository {
    private List<Produto> produtos;

    public ProdutoRepository() {
        produtos = new ArrayList<>();
    }

    public void salvarProduto(Produto produto) {
        if (produto == null) {
            JOptionPane.showMessageDialog(null, "Produto não pode ser nulo.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (buscarPorId(produto.getId()) != null) {
            JOptionPane.showMessageDialog(null, "Produto com ID " + produto.getId() + " já existe.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        produtos.add(produto);
    }

    public List<Produto> listarProdutos() {
        return produtos;
    }

    public Produto buscarPorId(int id) {
        for (Produto produto : produtos) {
            if (produto.getId() == id) {
                return produto;
            } else {
                JOptionPane.showMessageDialog(null, "Produto com ID " + id + " não encontrado.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    public void excluirProduto(int id) {
        Produto produto = buscarPorId(id);

        if (produto != null) {
            produtos.remove(produto);
        } else {
            JOptionPane.showMessageDialog(null, "Produto não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
        }

    }

}