package view;

import javax.swing.JOptionPane;

import app.Contexto;

public class MenuPrincipal {
    Contexto contexto;

    public MenuPrincipal(Contexto contexto) {
        this.contexto = contexto;
    }

    public void iniciar() {
        int opcao;

        do {
            opcao = Integer.parseInt(JOptionPane.showInputDialog(
                    "========= MENU PRINCIPAL =========\n"
                            + "1 - Menu Cliente\n"
                            + "2 - Menu Produto\n"
                            + "3 - Menu Pedido\n"
                            + "4 - Menu Caixa\n"
                            + "5 - Menu Turma\n"
                            + "0 - Sair\n\n"
                            + "Escolha uma opção:"));

            switch (opcao) {

                case 1:
                    new MenuCliente(contexto).menu();
                    break;

                case 2:
                    new MenuProduto(contexto).menu();
                    break;

                case 3:
                    // new MenuPedido().menu();
                    break;

                case 4:
                    // new MenuCaixa().menu();
                    break;

                case 5:
                    new MenuTurma(contexto).menu();
                    break;

                case 0:
                    JOptionPane.showMessageDialog(null, "Saindo do sistema...");
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida.");
            }

        } while (opcao != 0);
    }
}
