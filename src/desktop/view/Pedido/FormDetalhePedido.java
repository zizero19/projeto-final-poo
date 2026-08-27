package desktop.view.Pedido;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.table.DefaultTableModel;

import model.ItemPedido;
import model.Pedido;
import model.enums.FormaPagamento;
import util.FormatacaoUtil;

/**
 * Tela somente-leitura com os detalhes de um pedido. É usada tanto a partir
 * do Menu de Pedido quanto do Menu de Caixa (e do histórico de cliente),
 * então concentra aqui a montagem da tabela de itens para não duplicar essa
 * lógica em cada tela que precisa mostrar um pedido.
 */
public class FormDetalhePedido extends JDialog {

    public FormDetalhePedido(Pedido pedido) {
        super((Frame) null, "Detalhes do Pedido #" + pedido.getId(), true);
        configurarTela(pedido);
    }

    private void configurarTela(Pedido pedido) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 460);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel painelInfo = new JPanel(new GridLayout(0, 2, 8, 4));
        painelInfo.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        painelInfo.add(new JLabel("ID:"));
        painelInfo.add(new JLabel(String.valueOf(pedido.getId())));

        painelInfo.add(new JLabel("Data/Hora:"));
        painelInfo.add(new JLabel(FormatacaoUtil.formatarDataHora(pedido.getDataHora())));

        painelInfo.add(new JLabel("Status:"));
        painelInfo.add(new JLabel(String.valueOf(pedido.getStatus())));

        painelInfo.add(new JLabel("Forma de Pagamento:"));
        painelInfo.add(new JLabel(pedido.getFormaPagamento() != null ? pedido.getFormaPagamento().toString() : "-"));

        if (pedido.getFormaPagamento() == FormaPagamento.FIADO) {
            painelInfo.add(new JLabel("Cliente:"));
            painelInfo.add(new JLabel(pedido.getCliente() != null ? pedido.getCliente().getNome() : "-"));

            painelInfo.add(new JLabel("CPF do Cliente:"));
            painelInfo.add(new JLabel(pedido.getCliente() != null ? pedido.getCliente().getCpf() : "-"));
        }

        String[] colunas = { "Produto", "Quantidade", "Preço Unitário", "Subtotal" };
        DefaultTableModel modelItens = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                if (item == null) {
                    continue;
                }
                modelItens.addRow(new Object[] {
                        item.getNomeProduto(),
                        item.getQuantidade(),
                        FormatacaoUtil.formatarMoeda(item.getPrecoUnitario()),
                        FormatacaoUtil.formatarMoeda(item.getSubtotal())
                });
            }
        }

        JTable tabelaItens = new JTable(modelItens);
        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setPreferredSize(new DimensionUIResource(520, 200));
        scrollItens.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JLabel lblTotal = new JLabel("Total do Pedido: " + FormatacaoUtil.formatarMoeda(pedido.getPrecoTotal()));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());
        JPanel rodape = new JPanel();
        rodape.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        rodape.add(btnFechar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(scrollItens, BorderLayout.CENTER);
        centro.add(lblTotal, BorderLayout.SOUTH);

        add(painelInfo, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    public void abrir() {
        setVisible(true);
    }
}
