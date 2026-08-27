package desktop.view.Caixa;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
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
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;

import desktop.view.Pedido.FormDetalhePedido;
import model.Caixa;
import model.Pedido;
import service.CaixaService;
import service.RegraNegocioException;
import util.FormatacaoUtil;

/**
 * Tela central de controle do Caixa.
 *
 * Ao contrário do Menu de Pedido (focado em operação de pedidos), esta tela
 * é organizada em torno do estado do caixa: aberto/fechado, movimentação do
 * período e histórico de caixas já encerrados.
 */
public class TelaCaixa extends JDialog {

    private final CaixaService caixaService;
    private JPanel painelConteudo;

    public TelaCaixa(CaixaService caixaService) {
        super((Frame) null, "Caixa", true);
        this.caixaService = caixaService;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JButton btnHistorico = new JButton("Histórico de Caixas");
        btnHistorico.addActionListener(e -> abrirHistorico());
        JButton btnFechar = new JButton("Fechar Janela");
        btnFechar.addActionListener(e -> dispose());

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        rodape.add(btnHistorico);
        rodape.add(btnFechar);

        painelConteudo = new JPanel(new BorderLayout());
        add(painelConteudo, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        atualizarPainel();
    }

    private void atualizarPainel() {
        painelConteudo.removeAll();

        Caixa caixaAberto = caixaService.buscarCaixaAberto();
        if (caixaAberto != null) {
            painelConteudo.add(montarPainelCaixaAberto(caixaAberto), BorderLayout.CENTER);
        } else {
            painelConteudo.add(montarPainelSemCaixaAberto(), BorderLayout.CENTER);
        }

        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private JPanel montarPainelCaixaAberto(Caixa caixaAberto) {
        List<Pedido> pedidosDoCaixa = caixaService.listarPedidosDoCaixa(caixaAberto.getId());

        JLabel lblStatus = new JLabel("● Caixa Aberto");
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD, 20f));
        lblStatus.setForeground(new Color(0, 128, 0));

        JPanel painelInfo = new JPanel(new GridLayout(0, 2, 8, 10));
        painelInfo.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        painelInfo.add(new JLabel("Abertura:"));
        painelInfo.add(new JLabel(FormatacaoUtil.formatarDataHora(caixaAberto.getAbertura())));

        painelInfo.add(new JLabel("Pedidos registrados:"));
        painelInfo.add(new JLabel(String.valueOf(pedidosDoCaixa.size())));

        JLabel lblTotal = new JLabel(FormatacaoUtil.formatarMoeda(caixaAberto.getTotalVendas()));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 16f));
        painelInfo.add(new JLabel("Total movimentado:"));
        painelInfo.add(lblTotal);

        JButton btnFecharCaixa = new JButton("Fechar Caixa");
        btnFecharCaixa.addActionListener(e -> fecharCaixa());

        JButton btnVerPedidos = new JButton("Ver Pedidos do Caixa Atual");
        btnVerPedidos.addActionListener(e -> abrirPedidosDoCaixa(caixaAberto));

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelAcoes.add(btnVerPedidos);
        painelAcoes.add(btnFecharCaixa);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBorder(BorderFactory.createEmptyBorder(25, 20, 0, 20));
        topo.add(lblStatus, BorderLayout.CENTER);

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(topo, BorderLayout.NORTH);
        painel.add(painelInfo, BorderLayout.CENTER);
        painel.add(painelAcoes, BorderLayout.SOUTH);
        return painel;
    }

    private JPanel montarPainelSemCaixaAberto() {
        JLabel lblStatus = new JLabel("○ Nenhum Caixa Aberto");
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD, 20f));
        lblStatus.setForeground(Color.DARK_GRAY);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblInstrucao = new JLabel("Abra o caixa para começar a registrar pagamentos.");
        lblInstrucao.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnAbrirCaixa = new JButton("Abrir Caixa");
        btnAbrirCaixa.setFont(btnAbrirCaixa.getFont().deriveFont(Font.BOLD, 14f));
        btnAbrirCaixa.addActionListener(e -> abrirCaixa());

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.add(btnAbrirCaixa);

        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(60, 20, 60, 20));
        painel.add(lblStatus, BorderLayout.NORTH);
        painel.add(lblInstrucao, BorderLayout.CENTER);
        painel.add(painelBotao, BorderLayout.SOUTH);
        return painel;
    }

    private void abrirCaixa() {
        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja abrir um novo caixa?", "Abrir Caixa",
                JOptionPane.YES_NO_OPTION);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            caixaService.abrirCaixa();
            JOptionPane.showMessageDialog(this, "Caixa aberto com sucesso.");
            atualizarPainel();
        } catch (RegraNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível abrir o caixa",
                    JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro ao acessar o banco de dados.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fecharCaixa() {
        int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja fechar o caixa atual?", "Fechar Caixa",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            caixaService.fecharCaixa();
            JOptionPane.showMessageDialog(this, "Caixa fechado com sucesso.");
            atualizarPainel();
        } catch (RegraNegocioException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Não foi possível fechar o caixa",
                    JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Ocorreu um erro ao acessar o banco de dados.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirPedidosDoCaixa(Caixa caixaAberto) {
        List<Pedido> pedidos = caixaService.listarPedidosDoCaixa(caixaAberto.getId());

        if (pedidos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum pedido foi finalizado neste caixa ainda.", "Informação",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] colunas = { "ID", "Cliente", "Data/Hora", "Pagamento", "Total" };
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
                    p.getFormaPagamento(),
                    FormatacaoUtil.formatarMoeda(p.getPrecoTotal())
            });
        }

        JTable tabela = new JTable(model);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int linha = tabela.getSelectedRow();
                    if (linha != -1) {
                        new FormDetalhePedido(pedidos.get(linha)).abrir();
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(600, 280));
        JOptionPane.showMessageDialog(this, scroll, "Pedidos do Caixa Atual", JOptionPane.PLAIN_MESSAGE);
    }

    private void abrirHistorico() {
        List<Caixa> caixasFechados = caixaService.listarCaixasFechados();

        if (caixasFechados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum caixa foi fechado ainda.", "Histórico de Caixas",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] colunas = { "ID", "Abertura", "Fechamento", "Total de Vendas" };
        DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (Caixa c : caixasFechados) {
            model.addRow(new Object[] {
                    c.getId(),
                    FormatacaoUtil.formatarDataHora(c.getAbertura()),
                    FormatacaoUtil.formatarDataHora(c.getFechamento()),
                    FormatacaoUtil.formatarMoeda(c.getTotalVendas())
            });
        }

        JTable tabela = new JTable(model);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new DimensionUIResource(560, 280));
        JOptionPane.showMessageDialog(this, scroll, "Histórico de Caixas Fechados", JOptionPane.PLAIN_MESSAGE);
    }

    public void abrir() {
        setVisible(true);
    }
}
