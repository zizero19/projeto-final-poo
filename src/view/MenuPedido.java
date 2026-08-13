package view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;

import app.Contexto;
import model.Caixa;
import model.Cliente;
import model.ItemPedido;
import model.Pedido;
import model.Produto;
import model.enums.FormaPagamento;
import model.enums.StatusPedido;

public class MenuPedido {
    private Contexto contexto;

    public MenuPedido(Contexto contexto) {
        this.contexto = contexto;
    }

    public void menu() {
        int opcao;

        do {
            opcao = Integer.parseInt(JOptionPane.showInputDialog(
                    "========= MENU PEDIDO =========\n"
                            + "1 - Realizar Pedido\n"
                            + "2 - Listar Pedidos\n"
                            + "3 - Buscar Pedido por ID\n"
                            + "4 - Confirmar Pagamento / Finalizar Pedido\n"
                            + "5 - Cancelar Pedido\n"
                            + "6 - Remover Pedido\n"
                            + "0 - Voltar\n\n"
                            + "Escolha uma opção:"));

            switch (opcao) {

                case 1:
                    realizarPedido();
                    break;

                case 2:
                    listarPedidos();
                    break;

                case 3:
                    buscarPedidoPorId();
                    break;

                case 4:
                    confirmarPagamento();
                    break;

                case 5:
                    cancelarPedido();
                    break;

                case 6:
                    removerPedido();
                    break;

                case 0:
                    JOptionPane.showMessageDialog(null, "Voltando ao menu principal...");
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida.");
            }

        } while (opcao != 0);
    }

    public void realizarPedido() {
        List<Produto> produtos = contexto.getProdutoRepository().listarProdutos();

        if (produtos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum produto cadastrado.");
            return;
        }

        String[] colunasProdutos = { "ID", "Nome", "Categoria", "Preço", "Estoque" };
        DefaultTableModel modelProdutos = new DefaultTableModel(colunasProdutos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Produto p : produtos) {
            modelProdutos.addRow(new Object[] {
                    p.getId(),
                    p.getNome(),
                    p.getCategoria(),
                    p.getPreco(),
                    p.getQuantidadeEstoque()
            });
        }

        JTable tabelaProdutos = new JTable(modelProdutos);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollProdutos = new JScrollPane(tabelaProdutos);
        scrollProdutos.setPreferredSize(new DimensionUIResource(560, 150));

        JTextField txtQuantidade = new JTextField(5);
        JButton btnAdicionar = new JButton("Adicionar Item");

        JPanel painelAdicionar = new JPanel();
        painelAdicionar.add(new JLabel("Quantidade:"));
        painelAdicionar.add(txtQuantidade);
        painelAdicionar.add(btnAdicionar);

        List<ItemPedido> itensPedido = new ArrayList<>();

        String[] colunasItens = { "Produto", "Quantidade", "Subtotal" };
        DefaultTableModel modelItens = new DefaultTableModel(colunasItens, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabelaItens = new JTable(modelItens);
        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setPreferredSize(new DimensionUIResource(560, 120));

        JLabel lblTotal = new JLabel("Total: R$ 0,00");

        btnAdicionar.addActionListener(e -> {
            int linhaSelecionada = tabelaProdutos.getSelectedRow();

            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(null, "Selecione um produto na tabela.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Produto produtoSelecionado = produtos.get(linhaSelecionada);

            int quantidade;
            try {
                quantidade = Integer.parseInt(txtQuantidade.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Quantidade inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(null, "A quantidade deve ser maior que zero.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (quantidade > produtoSelecionado.getQuantidadeEstoque()) {
                JOptionPane.showMessageDialog(null, "Quantidade indisponível em estoque.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ItemPedido item = new ItemPedido(produtoSelecionado, quantidade, 0);
            itensPedido.add(item);
            produtoSelecionado.diminuirEstoque(quantidade);

            modelItens.addRow(new Object[] {
                    produtoSelecionado.getNome(),
                    item.getQuantidade(),
                    String.format("R$ %.2f", item.getSubtotal())
            });

            modelProdutos.setValueAt(produtoSelecionado.getQuantidadeEstoque(), linhaSelecionada, 4);

            double total = 0.0;
            for (ItemPedido i : itensPedido) {
                total += i.getSubtotal();
            }
            lblTotal.setText(String.format("Total: R$ %.2f", total));

            txtQuantidade.setText("");
        });

        JComboBox<FormaPagamento> comboPagamento = new JComboBox<>(FormaPagamento.values());
        JTextField txtObservacoes = new JTextField();

        JPanel painelFinal = new JPanel(new GridLayout(2, 2, 5, 5));
        painelFinal.add(new JLabel("Forma de Pagamento:"));
        painelFinal.add(comboPagamento);
        painelFinal.add(new JLabel("Observações:"));
        painelFinal.add(txtObservacoes);

        JPanel painelPrincipal = new JPanel(new BorderLayout(5, 5));
        painelPrincipal.add(new JLabel("Produtos cadastrados:"), BorderLayout.NORTH);
        painelPrincipal.add(scrollProdutos, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new BorderLayout(5, 5));
        painelInferior.add(painelAdicionar, BorderLayout.NORTH);
        painelInferior.add(scrollItens, BorderLayout.CENTER);
        painelInferior.add(lblTotal, BorderLayout.SOUTH);

        JPanel painelSul = new JPanel(new BorderLayout(5, 5));
        painelSul.add(painelInferior, BorderLayout.NORTH);
        painelSul.add(painelFinal, BorderLayout.SOUTH);

        painelPrincipal.add(painelSul, BorderLayout.SOUTH);

        int opcao = JOptionPane.showConfirmDialog(
                null,
                painelPrincipal,
                "Realizar Pedido",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (opcao != JOptionPane.OK_OPTION) {
            desfazerReservaDeEstoque(itensPedido);
            return;
        }

        if (itensPedido.isEmpty()) {
            JOptionPane.showMessageDialog(null, "O pedido precisa ter ao menos um item.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            desfazerReservaDeEstoque(itensPedido);
            return;
        }

        FormaPagamento formaPagamento = (FormaPagamento) comboPagamento.getSelectedItem();
        String observacoes = txtObservacoes.getText();

        Cliente cliente = null;

        if (formaPagamento == FormaPagamento.FIADO) {
            String cpf = JOptionPane.showInputDialog(null, "Pedido fiado. Digite o CPF do cliente:");
            cliente = contexto.getClienteRepository().buscarPorCpf(cpf);

            if (cliente == null) {
                JOptionPane.showMessageDialog(null,
                        "Cliente com CPF " + cpf + " não encontrado. Pedido não pôde ser criado.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                desfazerReservaDeEstoque(itensPedido);
                return;
            }
        }

        Pedido novoPedido = new Pedido(0, cliente, observacoes);

        if (novoPedido.getFormaPagamento() == FormaPagamento.FIADO && novoPedido.getCliente() == null) {
            JOptionPane.showMessageDialog(null, "Pedidos fiados precisam de um cliente associado.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (contexto.getPedidoRepository().buscarPorId(novoPedido.getId()) != null) {
            JOptionPane.showMessageDialog(null, "Pedido com ID " + novoPedido.getId() + " já existe.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (ItemPedido item : itensPedido) {
            novoPedido.adicionarItem(item);
        }

        novoPedido.setFormaPagamento(formaPagamento);
        novoPedido.setPrecoTotal(novoPedido.calcularTotal());

        novoPedido.cobrarPedido();

        contexto.getPedidoRepository().salvarPedido(novoPedido);

        JOptionPane.showMessageDialog(null, "Pedido realizado com sucesso!\nTotal: R$ "
                + String.format("%.2f", novoPedido.getPrecoTotal()));
    }

    private void desfazerReservaDeEstoque(List<ItemPedido> itensPedido) {
        for (ItemPedido item : itensPedido) {
            item.getProduto().aumentarEstoque(item.getQuantidade());
        }
    }

    public void listarPedidos() {
        List<Pedido> pedidos = contexto.getPedidoRepository().listarPedidos();

        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pedido cadastrado.");
            return;
        }

        String[] colunas = { "ID", "Cliente", "Data/Hora", "Status", "Forma de Pagamento", "Total" };

        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        for (Pedido p : pedidos) {
            model.addRow(new Object[] {
                    p.getId(),
                    p.getCliente() != null ? p.getCliente().getNome() : "-",
                    p.getDataHora(),
                    p.getStatus(),
                    p.getFormaPagamento(),
                    String.format("R$ %.2f", p.calcularTotal())
            });
        }

        JTable tabela = new JTable(model);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(600, 300));

        JOptionPane.showMessageDialog(null, scroll);
    }

    public void buscarPedidoPorId() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do pedido a ser buscado:"));

        Pedido pedidoBuscado = contexto.getPedidoRepository().buscarPorId(id);

        if (pedidoBuscado != null) {
            JOptionPane.showMessageDialog(null, pedidoBuscado.toString());
        } else {
            JOptionPane.showMessageDialog(null, "Pedido com ID " + id + " não encontrado.");
        }
    }

    public void confirmarPagamento() {
        List<Pedido> pedidos = contexto.getPedidoRepository().listarPedidos();

        List<Pedido> pedidosEmEspera = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getStatus() == StatusPedido.AGUARDANDO_PAGAMENTO) {
                pedidosEmEspera.add(p);
            }
        }

        if (pedidosEmEspera.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pedido aguardando pagamento.");
            return;
        }

        String[] colunas = { "ID", "Cliente", "Forma de Pagamento", "Total" };
        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        for (Pedido p : pedidosEmEspera) {
            model.addRow(new Object[] {
                    p.getId(),
                    p.getCliente() != null ? p.getCliente().getNome() : "-",
                    p.getFormaPagamento(),
                    String.format("R$ %.2f", p.calcularTotal())
            });
        }

        JTable tabela = new JTable(model);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(600, 250));

        JOptionPane.showMessageDialog(null, scroll, "Pedidos Aguardando Pagamento", JOptionPane.PLAIN_MESSAGE);

        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do pedido para confirmar o pagamento:"));

        Pedido pedido = contexto.getPedidoRepository().buscarPorId(id);

        if (pedido == null || pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            JOptionPane.showMessageDialog(null, "Pedido não encontrado ou não está aguardando pagamento.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                null,
                "Confirmar pagamento do pedido ID " + pedido.getId() + " no valor de R$ "
                        + String.format("%.2f", pedido.calcularTotal()) + "?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(null, "Operação cancelada.");
            return;
        }

        pedido.finalizarPedido();

        Caixa caixaAberto = contexto.getCaixaRepository().buscarCaixaAberto();
        if (caixaAberto != null) {
            caixaAberto.registrarPedido(pedido);
        }

        JOptionPane.showMessageDialog(null, "Pagamento confirmado! Pedido finalizado com sucesso.");
    }

    public void cancelarPedido() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do pedido a ser cancelado:"));

        Pedido pedido = contexto.getPedidoRepository().buscarPorId(id);

        if (pedido == null) {
            JOptionPane.showMessageDialog(null, "Pedido com ID " + id + " não encontrado.");
            return;
        }

        if (pedido.getStatus() == StatusPedido.FINALIZADO || pedido.getStatus() == StatusPedido.CANCELADO) {
            JOptionPane.showMessageDialog(null, "Não é possível cancelar um pedido que já foi finalizado ou cancelado.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                null,
                "Deseja realmente cancelar o pedido ID " + id + "?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            pedido.cancelarPedido();
            desfazerReservaDeEstoque(pedido.getItens());
            JOptionPane.showMessageDialog(null, "Pedido cancelado com sucesso.");
        }
    }

    public void removerPedido() {
        int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do pedido a ser removido:"));

        Pedido pedido = contexto.getPedidoRepository().buscarPorId(id);

        if (pedido != null) {
            contexto.getPedidoRepository().removerPedido(id);
            JOptionPane.showMessageDialog(null, "Pedido com ID " + id + " removido com sucesso.");
        } else {
            JOptionPane.showMessageDialog(null, "Pedido com ID " + id + " não encontrado.");
        }
    }
}