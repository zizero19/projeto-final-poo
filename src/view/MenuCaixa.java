// package view;

// import javax.swing.JOptionPane;

// import app.Contexto;
// import model.Caixa;
// import repository.CaixaRepository;

// public class MenuCaixa {
// private Contexto contexto;

// public MenuCaixa(Contexto contexto) {
// this.contexto = contexto;
// }

// public void menu() {
// int opcao = -1;

// do {
// opcao = Integer.parseInt(JOptionPane.showInputDialog(
// "========= MENU CAIXA =========\n"
// + "1 - Abrir Caixa\n"
// + "2 - Fechar Caixa\n"
// + "3 - Listar Caixas Fechados\n"
// + "4 - Buscar por ID\n"
// + "5 - Listar Pedidos do Caixa Atual\n"
// + "0 - Voltar\n\n"
// + "Escolha uma opção:"));

// switch (opcao) {

// case 1:

// int id = Integer.parseInt(
// JOptionPane.showInputDialog("Digite o ID do caixa:"));

// int qtdMaxClientes = Integer.parseInt(
// JOptionPane.showInputDialog("Quantidade máxima de clientes:"));

// Caixa caixa = new Caixa(qtdMaxClientes);
// caixa.setId(id);

// caixaRepository.abrirCaixa(caixa);

// break;

// case 2:

// int idFechar = Integer.parseInt(
// JOptionPane.showInputDialog("Digite o ID do caixa:"));

// caixaRepository.fecharCaixa(idFechar);

// break;

// case 3:
// caixaRepository.listar();
// break;

// case 4:
// int idd = Integer
// .parseInt(JOptionPane.showInputDialog("Digite o ID do caixa que deseja
// buscar"));
// caixaRepository.buscarPorId(idd);
// break;

// case 0:
// javax.swing.JOptionPane.showMessageDialog(
// null,
// "Voltando ao menu principal.",
// "Informação",
// javax.swing.JOptionPane.INFORMATION_MESSAGE);
// break;

// default:
// javax.swing.JOptionPane.showMessageDialog(
// null,
// "Opção inválida! Tente novamente.",
// "Erro",
// javax.swing.JOptionPane.ERROR_MESSAGE);
// }
// } while (opcao != 0);

// }

// }
