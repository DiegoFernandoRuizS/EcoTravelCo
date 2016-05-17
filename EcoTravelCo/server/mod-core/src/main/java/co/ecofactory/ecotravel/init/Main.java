package co.ecofactory.ecotravel.init;

public class Main {
    public static void main(String... args) {

        System.out.println( "Inicializando con:" );
        System.out.println(System.getenv());
        System.out.println(System.getProperties());

        Services.init();
        Modulos.init();

    }
}
