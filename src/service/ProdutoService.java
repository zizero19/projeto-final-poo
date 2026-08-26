package service;

import java.util.List;

import model.Produto;
import repository.ProdutoRepository;

public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto salvarProduto(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }
        return produtoRepository.salvarProduto(produto) ? produto : null;
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.listarProdutos();
    }

    public Produto buscarProduto(Long id) {
        return produtoRepository.buscarProduto(id);
    }

    public List<Produto> buscarProdutosPorNome(String texto) {
        if (texto == null || texto.isBlank()) {
            return listarProdutos();
        }
        return produtoRepository.buscarProdutosPorNome(texto.trim());
    }

    public boolean excluirProduto(Long id) {
        return produtoRepository.excluirProduto(id);
    }

    public boolean atualizarProduto(Produto produto) {
        return produtoRepository.atualizarProduto(produto);
    }
}
