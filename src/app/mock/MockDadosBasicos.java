package app.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Cliente;
import model.Produto;
import model.Turma;
import model.enums.CategoriaProduto;
import model.enums.DiaSemana;
import model.enums.Turno;
import repository.ClienteRepository;
import repository.ProdutoRepository;
import repository.TurmaRepository;

/**
 * Dados iniciais para testes do sistema sem pedidos ou caixas previamente criados.
 */
public final class MockDadosBasicos {

    private MockDadosBasicos() {
    }

    public static void popular(
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository,
            TurmaRepository turmaRepository) {

        List<Turma> turmas = turmaRepository.listarTurmas();
        if (turmas.isEmpty()) {
            turmas = criarTurmas(turmaRepository);
        }

        List<Produto> produtos = produtoRepository.listarProdutos();
        if (produtos.isEmpty()) {
            produtos = criarProdutos(produtoRepository);
        }

        if (clienteRepository.listarClientes().isEmpty()) {
            criarClientes(clienteRepository, turmas);
        }
    }

    private static List<Turma> criarTurmas(TurmaRepository repository) {
        List<Turma> turmas = new ArrayList<>();
        turmas.add(salvarTurma(repository, "ADS 1A", 32, Turno.NOTURNO, DiaSemana.SEGUNDA, DiaSemana.QUARTA));
        turmas.add(salvarTurma(repository, "ADS 1B", 28, Turno.NOTURNO, DiaSemana.TERCA, DiaSemana.QUINTA));
        turmas.add(salvarTurma(repository, "ADS 2A", 35, Turno.NOTURNO, DiaSemana.SEGUNDA, DiaSemana.QUARTA));
        turmas.add(salvarTurma(repository, "ADS 2B", 30, Turno.NOTURNO, DiaSemana.TERCA, DiaSemana.QUINTA));
        turmas.add(salvarTurma(repository, "ADS 3A", 27, Turno.NOTURNO, DiaSemana.SEGUNDA, DiaSemana.QUARTA));
        turmas.add(salvarTurma(repository, "ADS 3B", 31, Turno.NOTURNO, DiaSemana.TERCA, DiaSemana.QUINTA));
        turmas.add(salvarTurma(repository, "ADM 1A", 40, Turno.NOTURNO, DiaSemana.SEGUNDA, DiaSemana.QUARTA));
        turmas.add(salvarTurma(repository, "ADM 2A", 37, Turno.NOTURNO, DiaSemana.TERCA, DiaSemana.QUINTA));
        turmas.add(salvarTurma(repository, "CONT 1A", 34, Turno.NOTURNO, DiaSemana.SEGUNDA, DiaSemana.QUARTA));
        turmas.add(salvarTurma(repository, "CONT 2A", 29, Turno.NOTURNO, DiaSemana.TERCA, DiaSemana.QUINTA));
        turmas.add(salvarTurma(repository, "ENG 1A", 36, Turno.NOTURNO, DiaSemana.SEGUNDA, DiaSemana.QUARTA));
        turmas.add(salvarTurma(repository, "ENG 2A", 33, Turno.NOTURNO, DiaSemana.TERCA, DiaSemana.QUINTA));
        return turmas;
    }

    private static Turma salvarTurma(
            TurmaRepository repository,
            String nome,
            int quantidade,
            Turno turno,
            DiaSemana... dias) {
        Turma turma = new Turma(nome, quantidade, turno, true, Arrays.asList(dias));
        repository.salvarTurma(turma);
        return turma;
    }

    private static List<Produto> criarProdutos(ProdutoRepository repository) {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(salvarProduto(repository, "Coxinha", CategoriaProduto.SALGADO, 7.50, 40));
        produtos.add(salvarProduto(repository, "Risoles de Presunto", CategoriaProduto.SALGADO, 7.00, 35));
        produtos.add(salvarProduto(repository, "Pastel de Carne", CategoriaProduto.SALGADO, 8.00, 30));
        produtos.add(salvarProduto(repository, "Pastel de Queijo", CategoriaProduto.SALGADO, 8.00, 30));
        produtos.add(salvarProduto(repository, "Enroladinho de Salsicha", CategoriaProduto.SALGADO, 6.50, 35));
        produtos.add(salvarProduto(repository, "Esfiha de Carne", CategoriaProduto.SALGADO, 7.00, 30));
        produtos.add(salvarProduto(repository, "Pao de Queijo", CategoriaProduto.SALGADINHOS, 4.50, 60));
        produtos.add(salvarProduto(repository, "Mini Pizza", CategoriaProduto.SALGADINHOS, 6.00, 40));
        produtos.add(salvarProduto(repository, "Sanduiche Natural", CategoriaProduto.SALGADO, 9.50, 25));
        produtos.add(salvarProduto(repository, "Misto Quente", CategoriaProduto.SALGADO, 8.50, 25));
        produtos.add(salvarProduto(repository, "Hamburguer", CategoriaProduto.SALGADO, 12.00, 30));
        produtos.add(salvarProduto(repository, "X-Salada", CategoriaProduto.SALGADO, 15.00, 25));
        produtos.add(salvarProduto(repository, "Cafe", CategoriaProduto.BEBIDA, 4.00, 80));
        produtos.add(salvarProduto(repository, "Cafe com Leite", CategoriaProduto.BEBIDA, 5.00, 70));
        produtos.add(salvarProduto(repository, "Refrigerante Lata", CategoriaProduto.BEBIDA, 6.00, 70));
        produtos.add(salvarProduto(repository, "Suco Natural", CategoriaProduto.BEBIDA, 7.00, 50));
        produtos.add(salvarProduto(repository, "Agua Mineral", CategoriaProduto.BEBIDA, 3.50, 100));
        produtos.add(salvarProduto(repository, "Cha Gelado", CategoriaProduto.BEBIDA, 5.50, 45));
        produtos.add(salvarProduto(repository, "Sorvete", CategoriaProduto.GELADOS, 8.00, 35));
        produtos.add(salvarProduto(repository, "Picolé", CategoriaProduto.GELADOS, 5.00, 50));
        produtos.add(salvarProduto(repository, "Brigadeiro", CategoriaProduto.DOCE, 4.00, 50));
        produtos.add(salvarProduto(repository, "Brownie", CategoriaProduto.DOCE, 6.50, 30));
        produtos.add(salvarProduto(repository, "Cookie", CategoriaProduto.DOCE, 5.50, 40));
        produtos.add(salvarProduto(repository, "Bolo de Chocolate", CategoriaProduto.DOCE, 7.00, 25));
        produtos.add(salvarProduto(repository, "Bala de Gelatina", CategoriaProduto.DOCE, 2.50, 100));
        return produtos;
    }

    private static Produto salvarProduto(
            ProdutoRepository repository,
            String nome,
            CategoriaProduto categoria,
            double preco,
            int estoque) {
        Produto produto = new Produto(nome, categoria, BigDecimal.valueOf(preco), estoque);
        repository.salvarProduto(produto);
        return produto;
    }

    private static void criarClientes(ClienteRepository repository, List<Turma> turmas) {
        String[] nomes = {
                "Ana Souza", "Bruno Lima", "Carlos Mendes", "Daniela Rocha", "Eduardo Martins",
                "Fernanda Alves", "Gabriel Costa", "Helena Ramos", "Igor Santos", "Julia Oliveira",
                "Lucas Pereira", "Mariana Silva", "Nicolas Freitas", "Patricia Gomes", "Rafael Duarte",
                "Sofia Almeida", "Thiago Barbosa", "Vanessa Moreira"
        };

        for (int i = 0; i < nomes.length; i++) {
            Cliente cliente = new Cliente();
            cliente.setNome(nomes[i]);
            cliente.setCpf(String.format("999.%03d.%03d-00", i + 1, i + 101));
            cliente.setEmail("cliente" + (i + 1) + "@teste.local");
            cliente.setTelefone("(48) 99999-" + String.format("%04d", 1000 + i));
            cliente.setTurmaMatriculada(turmas.get(i % turmas.size()));
            cliente.setSaldoDevedor(BigDecimal.ZERO);
            repository.salvarCliente(cliente);
        }
    }
}
