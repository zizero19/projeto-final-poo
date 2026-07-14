package app;

import view.MenuPrincipal;

public class App {
    public static void main(String[] args) throws Exception {
        Contexto contexto = new Contexto();

        MenuPrincipal menuPrincipal = new MenuPrincipal(contexto);

        menuPrincipal.iniciar();
    }

}