package co.ecofactory.ecotravel.init;

import co.ecofactory.ecotravel.module.contract.Modulo;

/**
 * Created by samuel on 2/15/16.
 */
public class Modulos {
    public static void init() {
        String[] modulos = new String[]{"co.ecofactory.ecotravel.producto.ModuloProducto"};

        for (String nombreModulo : modulos) {
            try {

                Modulo modulo = (Modulo) Class.forName(nombreModulo).newInstance();
                modulo.inicializar(VertxFactory.getVertxInstance());
                Services.getMainRouter().mountSubRouter(modulo.getNombre(),modulo.getRutas(VertxFactory.getVertxInstance()));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
