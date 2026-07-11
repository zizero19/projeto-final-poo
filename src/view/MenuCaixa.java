package view;

import javax.swing.JOptionPane;

import model.Caixa;
import repository.CaixaRepository;

public class MenuCaixa {
    CaixaRepository caixaRepository = new CaixaRepository();

    public void menu() {
        int opcao = -1;

        do {
            try {
                String entrada = javax.swing.JOptionPane.showInputDialog(
                        "========= MENU CAIXA =========\n"
                                + "1 - Abrir\n"
                                + "2 - Salvar\n"
                                + "3 - Listar\n"
                                + "4 - Buscar por ID\n"
                                + "0 - Voltar\n\n"
                                + "Escolha uma opção:");

                // Cancelou ou fechou a janela
                if (entrada == null) {
                    break;
                }

                // Não digitou nada
                if (entrada.trim().isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(
                            null,
                            "Digite uma opção!",
                            "Aviso",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                    continue;
                }

                opcao = Integer.parseInt(entrada);

                switch (opcao) {

                    case 1:

                        int id = Integer.parseInt(
                                JOptionPane.showInputDialog("Digite o ID do caixa:"));

                        int qtdMaxClientes = Integer.parseInt(
                                JOptionPane.showInputDialog("Quantidade máxima de clientes:"));

                        Caixa caixa = new Caixa(qtdMaxClientes);
                        caixa.setId(id);

                        caixaRepository.abrirCaixa(caixa);

                        break;

                
                    case 2:

                        int idFechar = Integer.parseInt(
                                JOptionPane.showInputDialog("Digite o ID do caixa:"));

                        caixaRepository.fecharCaixa(idFechar);

                        break;

                    case 3:
                        caixaRepository.listar();
                        break;

                    case 4:
                        int idd = Integer
                                .parseInt(JOptionPane.showInputDialog("Digite o ID do caixa que deseja buscar"));
                        caixaRepository.buscarPorId(idd);
                        break;

                    case 0:
                        javax.swing.JOptionPane.showMessageDialog(
                                null,
                                "Voltando ao menu principal.",
                                "Informação",
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        break;

                    default:
                        javax.swing.JOptionPane.showMessageDialog(
                                null,
                                "Opção inválida! Tente novamente.",
                                "Erro",
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(
                        null,
                        "Digite um número válido!",
                        "Erro",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } while (opcao != 0);

    }

}
