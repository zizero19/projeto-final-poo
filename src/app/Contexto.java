package app;

import repository.CaixaRepository;
import repository.ClienteRepository;
import repository.PedidoRepository;
import repository.ProdutoRepository;
import repository.TurmaRepository;

public class Contexto {
    private CaixaRepository caixaRepository;
    private ClienteRepository clienteRepository;
    private PedidoRepository pedidoRepository;
    private ProdutoRepository produtoRepository;
    private TurmaRepository turmaRepository;

    public Contexto() {
        this.caixaRepository = new CaixaRepository();
        this.clienteRepository = new ClienteRepository();
        this.pedidoRepository = new PedidoRepository();
        this.produtoRepository = new ProdutoRepository();
        this.turmaRepository = new TurmaRepository();
    }

    public CaixaRepository getCaixaRepository() {
        return caixaRepository;
    }

    public ClienteRepository getClienteRepository() {
        return clienteRepository;
    }

    public PedidoRepository getPedidoRepository() {
        return pedidoRepository;
    }

    public ProdutoRepository getProdutoRepository() {
        return produtoRepository;
    }

    public TurmaRepository getTurmaRepository() {
        return turmaRepository;
    }

}
