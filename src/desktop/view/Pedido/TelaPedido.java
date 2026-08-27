package desktop.view.Pedido;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import model.Pedido;
import service.PedidoService;
import service.ProdutoService;
import service.RegraNegocioException;
import util.FormatacaoUtil;

/**
 * Tela operacional de pedidos da cantina.
 *
 * Diferente das telas de cadastro (Produto/Turma), aqui o foco é o fluxo de
 * trabalho: iniciar um pedido novo, acompanhar o que está aguardando
 * pagamento e consultar o que já foi finalizado.
 */
public class TelaPedido extends JDialog {

    private final PedidoService pedidoService;
    private final ProdutoService produtoService;

    private final DefaultTableModel modelAguardando;
    private final JTable tabelaAguardando;
    private List<Pedido> pedidosAguardando;

    private final DefaultTableModel modelFinalizados;
    private final JTable tabelaFinalizados;
    private List<Pedido> pedidosFinalizados;

    public TelaPedido(PedidoService pedidoService, ProdutoService produtoService) {
        super((Frame) null, "Pedidos", true);
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;

        String[] colunas = { "ID", "Cliente", "Data/Hora", "Pagamento", "Total" };

        modelAguardando = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaAguardando = new JTable(modelAguardando);
        tabelaAguardando.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        modelFinalizados = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaFinalizados = new JTable(modelFinalizados);
        tabelaFinalizados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configurarTela();
        atualizarListas();
    }

    private void configurarTela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(780, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Operação de Pedidos");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));

        JButton btnNovoPedido = new JButton("+ Novo Pedido");
        btnNovoPedido.setFont(btnNovoPedido.getFont().deriveFont(Font.BOLD, 13f));
        btnNovoPedido.addActionListener(e -> new FormNovoPedido(pedidoService, produtoService,
                this::atualizarListas).abrir());

        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        painelTopo.add(titulo, BorderLayout.WEST);
        painelTopo.add(btnNovoPedido, BorderLayout.EAST);

        // ---- Aba: aguardando pagamento ----
        tabelaAguardando.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirDetalheSelecionado(tabelaAguardando, pedidosAguardando);
                }
            }
        });

        JButton btnConfirmarPagamento = new JButton("Confirmar Pagamento");
        btnConfirmarPagamento.addActionListener(e -> confirmarPagamento());
        JButton btnCancelarPedido = new JButton("Cancelar Pedido");
        btnCancelarPedido.addActionListener(e -> cancelarPedido());
        JButton btnDetalhesAguardando = new JButton("Ver Detalhes");
        btnDetalhesAguardando.addActionListener(e -> abrirDetalheSelecionado(tabelaAguardando, pedidosAguardando));

        JPanel painelAcoesAguardando = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelAcoesAguardando.add(btnConfirmarPagamento);
        painelAcoesAguardando.add(btnCancelarPedido);
        painelAcoesAguardando.add(btnDetalhesAguardando);

        JPanel painelAguardando = new JPanel(new BorderLayout(5, 5));
        painelAguardando.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelAguardando.add(new JScrollPane(tabelaAguardando), BorderLayout.CENTER);
        painelAguardando.add(painelAcoesAguardando, BorderLayout.SOUTH);

        // ---- Aba: finalizados ----
        tabelaFinalizados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    abrirDetalheSelecionado(tabelaFinalizados, pedidosFinalizados);
                }
            }
        });

        JButton btnDetalhesFinalizados = new JButton("Ver Detalhes");
        btnDetalhesFinalizados.addActionListener(e -> abrirDetalheSelecionado(tabelaFinalizados, pedidosFinalizados));
        JPanel painelAcoesFinalizados = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelAcoesFinalizados.add(btnDetalhesFinalizados);

        JPanel painelFinalizados = new JPanel(new BorderLayout(5, 5));
        painelFinalizados.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelFinalizados.add(new JScrollPane(tabelaFinalizados), BorderLayout.CENTER);
        painelFinalizados.add(painelAcoesFinalizados, BorderLayout.SOUTH);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Aguardando Pagamento", painelAguardando);
        abas.addTab("Finalizados", painelFinalizados);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        rodape.add(btnFechar);

        add(painelTopo, BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void atualizarListas() {
        pedidosAguardando = pedidoService.listarPedidosAguardandoPagamento();
        preencherTabela(modelAguardando, pedidosAguardando);

        pedidosFinalizados = pedidoService.listarPedidosFinalizados();
        preencherTabela(modelFinalizados, pedidosFinalizados);
    }

    private void preencherTabela(DefaultTableModel model, List<Pedido> pedidos) {
        model.setRowCount(0);
        for (Pedido p : pedidos) {
            model.addRow(new Object[] {
                    p.getId(),
                    p.getCliente() != null ? p.getCliente().getNome() : "-",
                    FormatacaoUtil.formatarDataHora(p.getDataHora()),
                    p.getFormaPagamento(),
                    FormatacaoUtil.formatarMoeda(p.getPrecoTotal())
            });
        }
    }

    private Pedido obterSelecionado(JTable tabela, List<Pedido> pedidos) {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            return null;
        }
        return pedidos.get(linha);
    }

    private void abrirDetalheSelecionado(JTable tabela, List<Pedido> pedidos) {
        Pedido selecionado = obterSelecionado(tabela, pedidos);
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela.", "Nenhum pedido selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        new FormDetalhePedido(selecionado).abrir();
    }

    private void confirmarPagamento() {
        Pedido selecionado = obterSelecionado(tabelaAguardando, pedidosAguardando);
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela.", "Nenhum pedido selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Confirmar o pagamento do pedido #" + selecionado.getId() + "?", "Confirmar Pagamento",
                JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            pedidoService.confirmarPagamento(selecionado.getId());
            JOptionPane.showMessageDialog(this, "Pagamento confirmado com sucesso.");
            atualizarListas();
        } catch (RegraNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível confirmar o pagamento",
                    JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro ao acessar o banco de dados.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarPedido() {
        Pedido selecionado = obterSelecionado(tabelaAguardando, pedidosAguardando);
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na tabela.", "Nenhum pedido selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Cancelar o pedido #" + selecionado.getId() + "? Os produtos voltarão ao estoque.",
                "Cancelar Pedido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            pedidoService.cancelarPedido(selecionado.getId());
            JOptionPane.showMessageDialog(this, "Pedido cancelado com sucesso.");
            atualizarListas();
        } catch (RegraNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível cancelar o pedido",
                    JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro ao acessar o banco de dados.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void abrir() {
        setVisible(true);
    }
}
