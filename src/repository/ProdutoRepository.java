package repository;

import java.util.ArrayList;
import java.util.List;

import model.Produto;

public class ProdutoRepository {

    private List<Produto> produtos;

    public ProdutoRepository() {
        produtos = new ArrayList<>();
    }

    // ==========================
    // CREATE
    // ==========================

    public String adicionarProduto(Produto produto) {

        String validacao = validarProduto(produto, true);

        if (validacao != null) {
            return validacao;
        }

        produtos.add(produto);

        return "Produto cadastrado com sucesso!";
    }

    // ==========================
    // READ
    // ==========================

    public List<Produto> listarProdutos() {
        return new ArrayList<>(produtos);
    }

    // ==========================
    // SEARCH
    // ==========================

    public Produto buscarProdutoPorId(int id) {

        for (Produto produto : produtos) {

            if (produto.getId() == id) {
                return produto;
            }

        }

        return null;
    }

    public Produto buscarProdutoPorNome(String nome) {

        if (nome == null) {
            return null;
        }

        for (Produto produto : produtos) {

            if (produto.getNome().equalsIgnoreCase(nome)) {
                return produto;
            }

        }

        return null;
    }

    // ==========================
    // UPDATE
    // ==========================

    public String atualizarProduto(Produto produtoAtualizado) {

        Produto produto = buscarProdutoPorId(produtoAtualizado.getId());

        if (produto == null) {
            return "Produto não encontrado.";
        }

        String validacao = validarProduto(produtoAtualizado, false);

        if (validacao != null) {
            return validacao;
        }

        produto.setNome(produtoAtualizado.getNome());
        produto.setCategoria(produtoAtualizado.getCategoria());
        produto.setPreco(produtoAtualizado.getPreco());
        produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());

        return "Produto atualizado com sucesso!";
    }

    // ==========================
    // DELETE
    // ==========================

    public String removerProduto(int id) {

        Produto produto = buscarProdutoPorId(id);

        if (produto == null) {
            return "Produto não encontrado.";
        }

        produtos.remove(produto);

        return "Produto removido com sucesso!";
    }

    // ==========================
    // VALIDAÇÕES
    // ==========================

    private String validarProduto(Produto produto, boolean validarIdDuplicado) {

        if (produto == null) {
            return "Produto inválido.";
        }

        if (produto.getId() <= 0) {
            return "ID inválido.";
        }

        if (validarIdDuplicado && existeId(produto.getId())) {
            return "Já existe um produto com este ID.";
        }

        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            return "Nome do produto é obrigatório.";
        }

        if (produto.getCategoria() == null) {
            return "Categoria obrigatória.";
        }

        if (produto.getPreco() <= 0) {
            return "Preço inválido.";
        }

        if (produto.getQuantidadeEstoque() < 0) {
            return "Quantidade em estoque inválida.";
        }

        return null;
    }

    private boolean existeId(int id) {

        return buscarProdutoPorId(id) != null;

    }

}