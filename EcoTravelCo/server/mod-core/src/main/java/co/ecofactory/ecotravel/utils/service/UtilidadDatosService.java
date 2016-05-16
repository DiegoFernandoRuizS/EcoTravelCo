package co.ecofactory.ecotravel.utils.service;

import co.ecofactory.ecotravel.init.Conexion;
import co.ecofactory.ecotravel.producto.service.dao.ProductoDAO;
import co.ecofactory.ecotravel.utils.service.dao.UtilidadDatosDAO;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UtilidadDatosService extends AbstractVerticle {
    private UtilidadDatosDAO dao;

    @Override
    public void start() throws Exception {
        dao = new UtilidadDatosDAO(this.getVertx(), new Conexion().getConf());

        // registro los metodos en el bus

        this.getVertx().eventBus().consumer("listarPaises", this::listarPaises);
        this.getVertx().eventBus().consumer("listaTipoProducto", this::listaTipoProducto);
        this.getVertx().eventBus().consumer("variabilidad", this::variabilidad);


    }
    public void listarPaises(Message<JsonObject> message) {
        System.out.println("listar Piases");
        try {
            CompletableFuture<List<JsonObject>> data = this.dao.listaPaises();
            data.whenComplete((ok, error) -> {
                System.out.println("Listar Paises");
                if (ok != null) {
                   //   System.out.println("ListarPaises:OK" + ok);
                    JsonArray arr = new JsonArray();
                    ok.forEach(o -> arr.add(o));
                    message.reply(arr);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }

    public void listaTipoProducto(Message<JsonObject> message) {
        System.out.println("listar tipo producto");
        try {
            CompletableFuture<List<JsonObject>> data = this.dao.listaTipoProducto();
            data.whenComplete((ok, error) -> {
                System.out.println("Listar producto");
                if (ok != null) {
                       System.out.println("tipoproducto:OK" + ok);
                    JsonArray arr = new JsonArray();
                    ok.forEach(o -> arr.add(o));
                    message.reply(arr);
                } else {
                    error.printStackTrace();
                    message.fail(0, "ERROR in data");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR inside catch");
        }
    }



    public void variabilidad(Message<JsonObject> message)  {
        try{
            JsonObject entrada = message.body();

            //Reemplazar ya que se llena en despliegue
            System.getProperties().setProperty("Variabilidad"
                    ,"EnvioMensajes," + "CalificarServicios," + "Twitter," + "Facebook," +
                            "Ruta," + "PublicarTransaccion," + "VentasAdministrador," +
                            "VentasProductor," + "ConsultaProductor," + "HistoricoClientesProductor");

            String variantes=System.getProperties().getProperty("Variabilidad");
            String getVariability[] = variantes.split(",");
            JsonObject response = new JsonObject();

            for (int i=0; getVariability.length >i ; i++  ) {
                response.put(String.valueOf(i), getVariability[i]);
            }

            message.reply(response);
        } catch (Exception e) {
            e.printStackTrace();
            message.fail(0, "ERROR al Procesar");
        }
    }

}


