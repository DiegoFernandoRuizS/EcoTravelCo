package co.ecofactory.ecotravel.producto;

import co.ecofactory.ecotravel.module.contract.Modulo;
import co.ecofactory.ecotravel.producto.service.ProductoService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import jdk.nashorn.internal.runtime.JSONFunctions;
import org.bson.json.JsonWriter;

import java.util.List;

public class ModuloProducto implements Modulo {
    @Override
    public void inicializar(Vertx vertx) {
        // Inicializando router
        System.out.println("Inicializando el modulo: Producto");
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        ProductoService productoService = new ProductoService();
        vertx.deployVerticle(productoService, options);
    }

    public String getNombre() {
        return "/producto";
    }

    public Router getRutas(Vertx vertx) {
        Router rutas = Router.router(vertx);


        rutas.get("/").handler(rc -> {
            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
            System.out.println("USUARIO AUTENTICADO ----->"+idUsuario);
            vertx.eventBus().send("listarProductos", new JsonObject(), res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto");
                    rc.response().end(((JsonArray) res.result().body()).encodePrettily());

                } else {
                    rc.response().end("ERROR en el modulo producto");
                }
            });
        });

        //Listar producto
        rutas.get("/:id").handler(rc -> {
            final String id = rc.request().getParam("id");
            final Long idAsLong = Long.valueOf(id);
            JsonObject _params = new JsonObject();
            _params.put("id", idAsLong);

            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
            System.out.println("USUARIO AUTENTICADO ----->"+idUsuario);

            vertx.eventBus().send("listarProducto", _params, res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo producto consultando un producto");
                }
            });
        });


        //Agregar nuevo producto
        rutas.post("/").handler(rc -> {
            JsonObject producto = new JsonObject();
            producto = rc.getBodyAsJson();
            Integer idUsuario = Integer.parseInt(rc.request().params().get("user-id"));
            producto.put("id_usuario", idUsuario);

           System.out.println("USUARIO AUTENTICADO para crear producto ----->"+producto.encodePrettily());
            vertx.eventBus().send("insertarProducto", producto, res -> {
                System.out.println("servidor insertarProducto: " + res.result().body());
                if (res.succeeded()) {
                    System.out.println("servidor correcto insertarProducto -> : " + res.result().body());
                    rc.response().end("Se inserto correctamente "+((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo producto insertar un producto");
                }
            });
        });

        //Editar un producto
        rutas.put("/:id").handler(rc -> {
            final String id = rc.request().getParam("id");
            final Long idAsLong = Long.valueOf(id);
            JsonObject producto = new JsonObject();
            producto = rc.getBodyAsJson();
            producto.put("id", idAsLong);
            vertx.eventBus().send("editarProducto", producto,res -> {
                System.out.println("servidor editarProducto: " + res.result().body());
                if (res.succeeded()) {
                    System.out.println("servidor correcto editarProducto -> : " + res.result().body());
                    if(((JsonObject)res.result().body()).getInteger("updated",0)!=0){
                        rc.response().end("Se editarProducto correctamente "+((JsonObject) res.result().body()).encodePrettily());
                    }else{
                        rc.response().end("El ID "+idAsLong+" no fue encontrado");
                    }

                } else {
                    rc.response().end("ERROR en el modulo producto editar un producto");
                }
            });
        });

        //Borrar un producto
        rutas.delete("/:id").handler(rc -> {
            final String id = rc.request().getParam("id");
            final Long idAsLong = Long.valueOf(id);
            JsonObject _params = new JsonObject();
            _params.put("id", idAsLong);
            vertx.eventBus().send("borrarProducto", _params, res -> {
                System.out.println("servidor: " + res);
                if (res.succeeded()) {
                    System.out.println("servidor correcto -> : " + res.result().body());
                    rc.response().end(((JsonObject) res.result().body()).encodePrettily());
                } else {
                    rc.response().end("ERROR en el modulo producto delete un producto");
                }
            });

        });

        return rutas;
    }

}

