package desktop.view.Cliente;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;

import desktop.view.Pedido.FormDetalhePedido;
import model.Cliente;
import model.Pedido;
import service.ClienteService;
import service.PedidoService;
import service.TurmaService;
import util.FormatacaoUtil;

public class TelaCliente extends JDialog {

    private final ClienteService clienteService;
    private final PedidoService pedidoService;
    private final TurmaService turmaService;

    private final JTable tabela;
    private final DefaultTableModel model;
    private List<Cliente> clientesExibidos;

    private final JComboBox<String> cbModoBusca = new JComboBox<>(new String[] { "Nome", "CPF" });
    private final JTextField txtBusca = new JTextField();

    public TelaCliente(ClienteService clienteService, PedidoService pedidoService, TurmaService turmaService) {
        super((Frame) null, "Gerenciamento de Clientes", true);
        this.clienteService = clienteService;
        this.pedidoService = pedidoService;
        this.turmaService = turmaService;

        String[] colunas = { "ID", "Nome", "CPF", "Email", "Turma", "Telefone", "Saldo Devedor" };
        model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(model);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        configurarTela();
        carregarClientes(clienteService.listarClientes());
    }

    private void configurarTela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(850, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topo = new JPanel(new GridLayout(2, 1, 5, 5));
        topo.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnCadastrar = new JButton("Cadastrar Cliente");
        JButton btnEditar = new JButton("Editar Cliente");
        JButton btnExcluir = new JButton("Excluir Cliente");
        JButton btnHistorico = new JButton("Histórico de Pedidos");

        btnCadastrar.addActionListener(e -> cadastrarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        btnHistorico.addActionListener(e -> historicoPedidos());

        painelAcoes.add(btnCadastrar);
        painelAcoes.add(btnEditar);
        painelAcoes.add(btnExcluir);
        painelAcoes.add(btnHistorico);

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpar = new JButton("Limpar");
        txtBusca.setColumns(20);

        btnBuscar.addActionListener(e -> pesquisar());
        btnLimpar.addActionListener(e -> {
            txtBusca.setText("");
            carregarClientes(clienteService.listarClientes());
        });

        painelBusca.add(new JLabel("Buscar por:"));
        painelBusca.add(cbModoBusca);
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelBusca.add(btnLimpar);

        topo.add(painelAcoes);
        topo.add(painelBusca);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Cliente selecionado = obterClienteSelecionado();
                    if (selecionado != null) {
                        new FormDetalheCliente(selecionado, pedidoService).abrir();
                    }
                }
            }
        });

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        rodape.add(btnFechar);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void carregarClientes(List<Cliente> clientes) {
        clientesExibidos = clientes;
        model.setRowCount(0);

        for (Cliente c : clientes) {
            model.addRow(new Object[] {
                    c.getId(),
                    c.getNome(),
                    c.getCpf(),
                    c.getEmail(),
                    c.getTurmaMatriculada() != null ? c.getTurmaMatriculada().getNomeTurma() : "-",
                    c.getTelefone(),
                    FormatacaoUtil.formatarMoeda(pedidoService.calcularSaldoDevedor(c.getCpf()))
            });
        }
    }

    private Cliente obterClienteSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            return null;
        }
        return clientesExibidos.get(linha);
    }

    private void cadastrarCliente() {
        new FormCliente(clienteService, turmaService, null,
                () -> carregarClientes(clienteService.listarClientes())).abrir();
    }

    private void editarCliente() {
        Cliente selecionado = obterClienteSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.", "Nenhum cliente selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        new FormCliente(clienteService, turmaService, selecionado,
                () -> carregarClientes(clienteService.listarClientes())).abrir();
    }

    private void excluirCliente() {
        Cliente selecionado = obterClienteSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.", "Nenhum cliente selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mensagem = "Deseja realmente excluir o cliente abaixo?\n\n"
                + "Nome: " + selecionado.getNome() + "\n"
                + "CPF: " + selecionado.getCpf();

        int confirmacao = JOptionPane.showConfirmDialog(this, mensagem, "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                clienteService.excluirCliente(selecionado.getCpf());
                JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!");
                carregarClientes(clienteService.listarClientes());
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, "Não foi possível excluir o cliente.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void pesquisar() {
        String texto = txtBusca.getText().trim();
        String modo = (String) cbModoBusca.getSelectedItem();

        if (texto.isEmpty()) {
            carregarClientes(clienteService.listarClientes());
            return;
        }

        if ("CPF".equals(modo)) {
            Cliente cliente = clienteService.buscarPorCpf(texto);
            carregarClientes(cliente != null ? List.of(cliente) : List.of());
        } else {
            carregarClientes(clienteService.buscarClientesPorNome(texto));
        }
    }

    private void historicoPedidos() {
        Cliente selecionado = obterClienteSelecionado();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela.", "Nenhum cliente selecionado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Pedido> pedidosDoCliente = pedidoService.buscarPedidosPorCpfDeCliente(selecionado.getCpf());

        if (pedidosDoCliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum pedido associado a esse cliente.");
            return;
        }

        String[] colunas = { "ID", "Data/Hora", "Status", "Pagamento", "Total", "Observações" };
        DefaultTableModel modelPedidos = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Pedido pedido : pedidosDoCliente) {
            modelPedidos.addRow(new Object[] {
                    pedido.getId(),
                    FormatacaoUtil.formatarDataHora(pedido.getDataHora()),
                    pedido.getStatus(),
                    pedido.getFormaPagamento(),
                    FormatacaoUtil.formatarMoeda(pedido.calcularTotal()),
                    pedido.getObservacoes() == null ? "" : pedido.getObservacoes()
            });
        }

        JTable tabelaPedidos = new JTable(modelPedidos);
        JScrollPane scrollPedidos = new JScrollPane(tabelaPedidos);
        scrollPedidos.setPreferredSize(new DimensionUIResource(700, 250));

        tabelaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int linha = tabelaPedidos.getSelectedRow();
            if (linha != -1) {
                new FormDetalhePedido(pedidosDoCliente.get(linha)).abrir();
                tabelaPedidos.clearSelection();
            }
        });

        JOptionPane.showMessageDialog(this, scrollPedidos, "Histórico de Pedidos de " + selecionado.getNome(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void abrir() {
        setVisible(true);
    }
}