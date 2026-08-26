package desktop.view;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.ListSelectionModel;

import model.Pedido;
import util.FormatacaoUtil;

import service.CaixaService;
import model.Caixa;

public class MenuCaixa {
    private final CaixaService caixaService;

    public MenuCaixa(CaixaService caixaService) {
        this.caixaService = caixaService;
    }

    public void menu() {
        int opcao;

        do {
            String entrada = JOptionPane.showInputDialog(
                    "========= MENU CAIXA =========\n"
                            + "1 - Abrir Caixa\n"
                            + "2 - Fechar Caixa\n"
                            + "3 - Listar Caixas Fechados\n"
                            + "4 - Buscar por ID\n"
                            + "5 - Listar Pedidos do Caixa Atual\n"
                            + "0 - Voltar\n\n"
                            + "Escolha uma opção:");

            if (entrada == null) {
                return;
            }

            opcao = Integer.parseInt(entrada);

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
                    JOptionPane.showMessageDialog(null, "Voltando ao menu principal.", "Informação",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida! Tente novamente.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
            }
        } while (opcao != 0);
    }

    public void abrirCaixa() {
        if (caixaService.buscarCaixaAberto() != null) {
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
            caixaService.salvarCaixa(caixaAberto);
            JOptionPane.showMessageDialog(null, "Caixa aberto com sucesso.");
        } else if (confirmacao == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(null, "Retornando para o menu.");
        }
    }

    public void fecharCaixa() {
        if (caixaService.buscarCaixaAberto() == null) {
            JOptionPane.showMessageDialog(null, "Não há caixa aberto no momento.");
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                null,
                "Deseja fechar o caixa atual?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            caixaService.buscarCaixaAberto().fechar();
            JOptionPane.showMessageDialog(null, "Caixa fechado com sucesso.");
        } else if (confirmacao == JOptionPane.NO_OPTION) {
            JOptionPane.showMessageDialog(null, "Retornando para o menu.");
        }
    }

    public void listarCaixasFechados() {
        List<Caixa> caixas = caixaService.listarCaixas();

        if (caixas == null || caixas.isEmpty() || caixas.stream().noneMatch(caixa -> !caixa.isAberto())) {
            JOptionPane.showMessageDialog(null, "Nenhum caixa foi registrado");
            return;
        }

        String[] colunas = { "ID", "Total de Vendas", "Data Abertura", "Data Fechamento" };
        DefaultTableModel model = new DefaultTableModel(colunas, 0);

        for (Caixa caixa : caixas) {
            if (!caixa.isAberto()) {
                model.addRow(new Object[] {
                        caixa.getId(),
                        FormatacaoUtil.formatarMoeda(caixa.getTotalVendas()),
                        FormatacaoUtil.formatarDataHora(caixa.getAbertura()),
                        FormatacaoUtil.formatarDataHora(caixa.getFechamento())
                });
            }
        }

        JTable tabela = new JTable(model);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(600, 300));
        JOptionPane.showMessageDialog(null, scroll);
    }

    public void buscarCaixaPorId() {
        Long id = Long.parseLong(JOptionPane.showInputDialog(null, "Digite o ID do caixa:"));
        Caixa caixaBuscado = caixaService.buscarPorId(id);

        if (caixaBuscado != null) {
            JOptionPane.showMessageDialog(null,
                    "Caixa encontrado:\n"
                            + "ID: " + caixaBuscado.getId() + "\n"
                            + "Total de Vendas: " + FormatacaoUtil.formatarMoeda(caixaBuscado.getTotalVendas()) + "\n"
                            + "Data e Hora Abertura: " + FormatacaoUtil.formatarDataHora(caixaBuscado.getAbertura())
                            + "\n"
                            + "Data e Hora Fechamento: " + FormatacaoUtil.formatarDataHora(caixaBuscado.getFechamento())
                            + "\n");
        } else {
            JOptionPane.showMessageDialog(null, "Caixa não encontrado");
        }
    }

    public void listarPedidosCaixaAtual() {
        Caixa caixaAtual = caixaService.buscarCaixaAberto();

        if (caixaAtual == null) {
            JOptionPane.showMessageDialog(null, "Não há caixa aberto no momento.", "Informação",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        List<Pedido> pedidos = caixaAtual.getPedidos();

        if (pedidos == null || pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nenhum pedido foi registrado no caixa atual.", "Informação",
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
                    FormatacaoUtil.formatarDataHora(p.getDataHora()),
                    p.getStatus(),
                    p.getFormaPagamento(),
                    FormatacaoUtil.formatarMoeda(p.calcularTotal())
            });
        }

        JTable tabela = new JTable(model);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(700, 300));

        JOptionPane.showMessageDialog(null, scroll, "Pedidos do Caixa Atual",
                JOptionPane.PLAIN_MESSAGE);
    }
}
