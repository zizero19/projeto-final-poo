package desktop.view.Produto;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Produto;
import util.FormatacaoUtil;

public class FormDetalheProduto extends JDialog {

    public FormDetalheProduto(Produto produto) {
        super((Frame) null, "Detalhes do Produto #" + produto.getId(), true);
        configurarTela(produto);
    }

    private void configurarTela(Produto produto) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painel = new JPanel(new GridLayout(0, 2, 10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        painel.add(new JLabel("ID:"));
        painel.add(new JLabel(String.valueOf(produto.getId())));

        painel.add(new JLabel("Nome:"));
        painel.add(new JLabel(produto.getNome()));

        painel.add(new JLabel("Categoria:"));
        painel.add(new JLabel(produto.getCategoria().toString()));

        painel.add(new JLabel("Preço:"));
        painel.add(new JLabel(FormatacaoUtil.formatarMoeda(produto.getPreco())));

        painel.add(new JLabel("Estoque:"));
        painel.add(new JLabel(String.valueOf(produto.getQtdEstoque())));

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