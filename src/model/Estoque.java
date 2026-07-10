package model;

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
        if (produto == null) {
            System.out.println("O produto não pode ser nulo.");
            return;
        }

        produtos.add(produto);
    }

    
    public void removerProduto(int id) {
        Produto produto = localizarProduto(id);

        if (produto == null) {
            System.out.println("Produto não encontrado.");
            return;
        }

        produtos.remove(produto);
    }

    
    public String buscarProduto(int id) {
        Produto produto = localizarProduto(id);

        if (produto == null) {
            return "Produto não encontrado.";
        }

        return produto.toString();
    }

   
    public void verificarDisponibilidade(int id) {
        Produto produto = localizarProduto(id);

        if (produto == null) {
            System.out.println("Produto não encontrado.");
            return;
        }

        if (produto.possuiEstoque()) {
            System.out.println("O produto '" + produto.getNome() + "' possui estoque disponível ("
                    + produto.getQuantidadeEstoque() + " unidades).");
        } else {
            System.out.println("O produto '" + produto.getNome() + "' está sem estoque disponível.");
        }
    }

    
    private Produto localizarProduto(int id) {
        for (Produto produto : produtos) {
            if (produto.getId() == id) {
                return produto;
            }
        }
        return null;
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