package co.ecofactory.ecotravel.init;

import co.ecofactory.ecotravel.module.contract.Modulo;

public class Modulos {
    public static void init() {

        String[] modulos = new String[]{"co.ecofactory.ecotravel.producto.ModuloProducto"
                , "co.ecofactory.ecotravel.producto.ModuloProductoPrincipal"
                , "co.ecofactory.ecotravel.producto.ModuloProductoDetalle",
                "co.ecofactory.ecotravel.usuario.ModuloUsuario",
                "co.ecofactory.ecotravel.cliente.ModuloCliente",
                "co.ecofactory.ecotravel.canasta.ModuloCanasta",
                "co.ecofactory.ecotravel.seguridad.ModuloSeguridad"};



        for (String nombreModulo : modulos) {
            try {

                Modulo modulo = (Modulo) Class.forName(nombreModulo).newInstance();
                modulo.inicializar(VertxFactory.getVertxInstance());
                Services.getMainRouter().mountSubRouter(modulo.getNombre(), modulo.getRutas(VertxFactory.getVertxInstance()));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
