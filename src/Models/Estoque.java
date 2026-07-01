package Models;

public class Estoque {

    private Produto produto;
    private int quantidade;

    // Construtor
    public Estoque(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    // Getters
    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    // Setters
    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    // toString()
    @Override
    public String toString() {
        return "Estoque{" +
                "produto=" + produto +
                ", quantidade=" + quantidade +
                '}';
    }
}