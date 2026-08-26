package desktop.view.Produto;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import service.ProdutoService;
import model.Produto;
import model.enums.CategoriaProduto;

public class FormProduto extends JDialog {

    private final ProdutoService produtoService;
    private final Produto produtoEmEdicao;
    private final Runnable aoSalvar;

    private final JTextField txtNome = new JTextField();
    private final JComboBox<CategoriaProduto> cbCategoria = new JComboBox<>(CategoriaProduto.values());
    private final JTextField txtPreco = new JTextField();
    private final JTextField txtEstoque = new JTextField();

    public FormProduto(ProdutoService produtoService) {
        this(produtoService, null, null);
    }

    public FormProduto(ProdutoService produtoService, Produto produtoEditar, Runnable aoSalvar) {
        super((Frame) null, produtoEditar == null ? "Cadastro de Produto" : "Editar Produto", true);
        this.produtoService = produtoService;
        this.produtoEmEdicao = produtoEditar;
        this.aoSalvar = aoSalvar;
        configurarTela();
        if (produtoEditar != null) {
            preencherCampos(produtoEditar);
        }
    }

    private void configurarTela() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        JPanel campos = new JPanel(new GridLayout(4, 2, 10, 10));
        campos.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        campos.add(new JLabel("Nome:"));
        campos.add(txtNome);
        campos.add(new JLabel("Categoria:"));
        campos.add(cbCategoria);
        campos.add(new JLabel("Preço:"));
        campos.add(txtPreco);
        campos.add(new JLabel("Quantidade em estoque:"));
        campos.add(txtEstoque);

        boolean edicao = produtoEmEdicao != null;
        JButton btnSalvar = new JButton(edicao ? "Salvar" : "Cadastrar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel();
        botoes.add(btnCancelar);
        botoes.add(btnSalvar);

        setLayout(new BorderLayout());
        add(campos, BorderLayout.CENTER);
        add(botoes, BorderLayout.SOUTH);
    }

    private void preencherCampos(Produto produto) {
        txtNome.setText(produto.getNome());
        cbCategoria.setSelectedItem(produto.getCategoria());
        txtPreco.setText(produto.getPreco().toString().replace('.', ','));
        txtEstoque.setText(String.valueOf(produto.getQtdEstoque()));
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String precoTexto = txtPreco.getText().trim();
        String estoqueTexto = txtEstoque.getText().trim();
        CategoriaProduto categoria = (CategoriaProduto) cbCategoria.getSelectedItem();

        if (nome.isEmpty()) {
            erro("O nome deve ser preenchido.", txtNome);
            return;
        }

        if (categoria == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria.", "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (precoTexto.isEmpty()) {
            erro("O preço deve ser preenchido.", txtPreco);
            return;
        }

        if (estoqueTexto.isEmpty()) {
            erro("A quantidade em estoque deve ser preenchida.", txtEstoque);
            return;
        }

        BigDecimal preco;
        int estoque;

        try {
            String valorNormalizado = precoTexto.replace("R$", "").replace(".", "").replace(',', '.').trim();
            preco = new BigDecimal(valorNormalizado).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            erro("Digite um preço válido.", txtPreco);
            return;
        }

        try {
            estoque = Integer.parseInt(estoqueTexto);
        } catch (NumberFormatException e) {
            erro("Digite uma quantidade inteira válida.", txtEstoque);
            return;
        }

        if (preco.compareTo(BigDecimal.ZERO) <= 0) {
            erro("O preço deve ser maior que zero.", txtPreco);
            return;
        }

        if (estoque < 0) {
            erro("A quantidade em estoque não pode ser negativa.", txtEstoque);
            return;
        }

        boolean sucesso;

        if (produtoEmEdicao == null) {
            Produto produto = new Produto(nome, categoria, preco, estoque);
            sucesso = produtoService.salvarProduto(produto) != null;
        } else {
            produtoEmEdicao.setNome(nome);
            produtoEmEdicao.setCategoria(categoria);
            produtoEmEdicao.setPreco(preco);
            produtoEmEdicao.setQtdEstoque(estoque);
            sucesso = produtoService.atualizarProduto(produtoEmEdicao);
        }

        if (!sucesso) {
            JOptionPane.showMessageDialog(this, "Não foi possível salvar o produto.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                produtoEmEdicao == null ? "Produto cadastrado com sucesso!" : "Produto atualizado com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        if (aoSalvar != null) {
            aoSalvar.run();
        }

        dispose();
    }

    private void erro(String mensagem, JTextField campo) {
        JOptionPane.showMessageDialog(this, mensagem, "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        campo.requestFocus();
    }

    public void abrir() {
        setVisible(true);
    }
}