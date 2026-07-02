package Models;

public class ItemPedido {
    // TODO: Implementar a classe ItemPedido com os atributos
    private Produto produto;
    private int quantidade;
    private double subtotal;

    public ItemPedido() {
    }


    public ItemPedido(Produto produto, int quantidade, double subtotal) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.subtotal = subtotal;
    }


    public Produto getProduto() {
        return produto;
    }
    public void setProduto(Produto produto) {
        this.produto = produto;
    }
    public int getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    public double getSubtotal() {
       
        subtotal = calcularSubtotal();
        return subtotal;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }



    public String toString(){
        return "produto" + produto +
        "\nquantidade: " + quantidade +
        "\nsubtotal: " + subtotal;
    }


   public double calcularSubtotal() {
        return produto.getPreco() * quantidade;
    }  

    

}
