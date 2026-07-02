import Models.*;
public class App {
    public static void main(String[] args) throws Exception {

        Estoque estoque = new Estoque();

Produto p1 = new Produto(1, "Arroz", CategoriaProduto.ALIMENTO, 25.90, 10);
Produto p2 = new Produto(2, "Feijão", CategoriaProduto.ALIMENTO, 8.50, 5);

estoque.adicionarProduto(p1);
estoque.adicionarProduto(p2);

System.out.println(estoque);

System.out.println("Buscar produto:");
System.out.println(estoque.buscarProduto(1));

System.out.println("Disponível? " + estoque.verificarDisponibilidade(1));

estoque.atualizarQuantidade(1, 20);

System.out.println(estoque.buscarProduto(1));

estoque.removerProduto(2);

System.out.println(estoque);


        
    }
}
