package desktop.view.Cliente;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Cliente;
import service.PedidoService;
import util.FormatacaoUtil;

public class FormDetalheCliente extends JDialog {

    public FormDetalheCliente(Cliente cliente, PedidoService pedidoService) {
        super((Frame) null, "Detalhes do Cliente", true);
        configurarTela(cliente, pedidoService);
    }

    private void configurarTela(Cliente cliente, PedidoService pedidoService) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(440, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        painel.add(new JLabel("ID:"));
        painel.add(new JLabel(String.valueOf(cliente.getId())));

        painel.add(new JLabel("Nome:"));
        painel.add(new JLabel(cliente.getNome()));

        painel.add(new JLabel("CPF:"));
        painel.add(new JLabel(cliente.getCpf()));

        painel.add(new JLabel("Email:"));
        painel.add(new JLabel(cliente.getEmail()));

        painel.add(new JLabel("Turma:"));
        painel.add(new JLabel(
                cliente.getTurmaMatriculada() != null ? cliente.getTurmaMatriculada().getNomeTurma() : "-"));

        painel.add(new JLabel("Telefone:"));
        painel.add(new JLabel(cliente.getTelefone()));

        painel.add(new JLabel("Saldo devedor:"));
        painel.add(new JLabel(FormatacaoUtil.formatarMoeda(pedidoService.calcularSaldoDevedor(cliente.getCpf()))));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel botoes = new JPanel();
        botoes.add(btnFechar);

        add(painel, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    public void abrir() {
        setVisible(true);
    }
}