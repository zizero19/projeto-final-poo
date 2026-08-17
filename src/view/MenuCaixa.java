package view;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;

import model.Pedido;

import app.Contexto;
import model.Caixa;

public class MenuCaixa {
    private Contexto contexto;

    public MenuCaixa(Contexto contexto) {
        this.contexto = contexto;
    }

    public void menu() {
        int opcao;

        do {
            opcao = Integer.parseInt(JOptionPane.showInputDialog(
                    "========= MENU CAIXA =========\n"
                            + "1 - Abrir Caixa\n"
                            + "2 - Fechar Caixa\n"
                            + "3 - Listar Caixas Fechados\n"
                            + "4 - Buscar por ID\n"
                            + "5 - Listar Pedidos do Caixa Atual\n"
                            + "0 - Voltar\n\n"
                            + "Escolha uma opção:"));

            switch (opcao) {

                case 1:
                    abrirCaixa();
                    break;

                case 2:
                    fecharCaixa();
                    break;

                case 3:
                    listarCaixasFechados();
                    break;

                case 4:
                    buscarCaixaPorId();
                    break;

                case 5:
                    listarPedidosCaixaAtual();
                    break;

                case 0:
                    JOptionPane.showMessageDialog(
                            null,
                            "Voltando ao menu principal.",
                            "Informação",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;

                default:
                    JOptionPane.showMessageDialog(
                            null,
                            "Opção inválida! Tente novamente.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
            }
        } while (opcao != 0);

    }

    public void abrirCaixa() {
        if (contexto.getCaixaRepository().buscarCaixaAberto() != null) {
            JOptionPane.showMessageDialog(null, "Um caixa ja esta aberto.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                null,
                "Deseja abrir um novo caixa?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            Caixa caixaAberto = new Caixa();
            contexto.getCaixaRepository().salvarCaixa(caixaAberto);

            JOptionPane.showMessageDialog(null, "Caixa aberto com sucesso.");
        } else if (confirmacao == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(null, "Retornando para o menu.");
            return;
        }
    }

    public void fecharCaixa() {
        if (contexto.getCaixaRepository().buscarCaixaAberto() == null) {
            JOptionPane.showMessageDialog(null, "Não há caixa aberto no momento.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                null,
                "Deseja fechar o caixa atual?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            contexto.getCaixaRepository().buscarCaixaAberto().fechar();

            JOptionPane.showMessageDialog(null, "Caixa fechado com sucesso.");
        } else if (confirmacao == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(null, "Retornando para o menu.");
            return;
        }

    }

    public void listarCaixasFechados() {
        List<Caixa> caixas = contexto.getCaixaRepository().listarCaixas();

        if (caixas.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhuma caixa foi registrado");
            return;
        }

        String[] colunas = { "ID", "Total de Vendas", "Data Abertura", "Data Fechamento" };

        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        for (Caixa caixa : caixas) {
            if (!caixa.isAberto()) {
                model.addRow(new Object[] {
                        caixa.getId(),
                        caixa.getTotalVendas(),
                        caixa.getAbertura(),
                        caixa.getFechamento()
                });
            }
        }

        JTable tabela = new JTable(model);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(600, 300));

        JOptionPane.showMessageDialog(null, scroll);

    }

    public void buscarCaixaPorId() {
        int id = Integer.parseInt(JOptionPane.showInputDialog(null, "Digite o ID do caixa:"));

        Caixa caixaBuscado = contexto.getCaixaRepository().buscarPorId(id);

        if (caixaBuscado != null) {
            JOptionPane.showMessageDialog(null,
                    "Caixa encontrado:\n"
                            + "ID: " + caixaBuscado.getId() + "\n"
                            + "Total de Vendas: " + caixaBuscado.getTotalVendas() + "\n"
                            + "Data e Hora Abertura: " + caixaBuscado.getAbertura() + "\n"
                            + "Data e Hora Fechamento: " + caixaBuscado.getFechamento() + "\n");
        } else {
            JOptionPane.showMessageDialog(null, "Caixa não encontrado");
            return;
        }

    }

    public void listarPedidosCaixaAtual() {
        Caixa caixaAtual = contexto.getCaixaRepository().buscarCaixaAberto();

        if (caixaAtual == null) {
            JOptionPane.showMessageDialog(
                    null,
                    "Não há caixa aberto no momento.",
                    "Informação",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Pedido> pedidos = caixaAtual.getPedidos();

        if (pedidos == null || pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Nenhum pedido foi registrado no caixa atual.",
                    "Informação",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] colunas = { "ID", "Cliente", "Data/Hora", "Status", "Forma de Pagamento", "Total" };

        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

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
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(700, 300));

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int linhaSelecionada = tabela.getSelectedRow();

            if (linhaSelecionada != -1) {
                Pedido pedidoSelecionado = pedidos.get(linhaSelecionada);

                new MenuPedido(contexto).mostrarDetalhesPedido(pedidoSelecionado);

                tabela.clearSelection();
            }
        });

        JOptionPane.showMessageDialog(
                null,
                scroll,
                "Pedidos do Caixa Atual - Selecione um pedido para ver os detalhes",
                JOptionPane.PLAIN_MESSAGE);
    }

}
