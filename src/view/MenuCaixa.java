package view;

import javax.swing.JOptionPane;

import app.Contexto;
import model.Caixa;
import javax.swing.JOptionPane;

public class MenuCaixa {
    private Contexto contexto;

    public MenuCaixa(Contexto contexto) {
        this.contexto = contexto;
    }

    public void menu() {
        int opcao = -1;

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
                    break;

                case 2:
                    break;

                case 3:
                    break;

                case 4:

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

        Caixa caixaAberto = new Caixa();
        caixaAberto.abrir();

    }

    public void fecharCaixa() {

    }

    public void listarCaixasFechados() {

    }

    public void buscarCaixaPorId() {

    }

    public void listarPedidosCaixaAtual() {

    }

}
