package co.ecofactory.ecotravel.aspectos;

import co.ecofactory.ecotravel.dao.LogDAO;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public aspect AspectoAJ {

    LogDAO dao = new LogDAO();

    pointcut logger(): call(* calificarServicio(..)) || call(* listarProductosBusqueda(..)) ||
        call(* listarProductosDetalle(..)) || call(* agregarPregunta(..)) || call(* pagarOrden(..));

    after() returning(Object resp): logger() {

        String signature = thisJoinPointStaticPart.getSignature().toString();
        signature = signature.replace(".", "-");
        String[] metodo = signature.split("-");
        Object[] argumentos = ((Object[])thisJoinPoint.getArgs());
        String funcion = metodo[metodo.length-1];

        //Escritura en archivo de log
        if(funcion.contains("agregarPregunta")){
            dao.writeLog((Integer) argumentos[1], "PREGUNTA", 0, 0);
        } else if(funcion.contains("calificarServicio")){
            dao.writeLog((Integer) argumentos[0], "CALIFICACION", (Integer) argumentos[2], 0);
        } else if(funcion.contains("listarProductosBusqueda")){
            //Busqueda
            ((CompletableFuture<List<JsonObject>>)resp).whenComplete((ok, error) -> {
                if (ok != null) {
                    ok.forEach(o -> dao.writeLog(((JsonObject)o).getInteger("id"), "BUSQUEDA", 0, 0));
                }
            });
        } else if(funcion.contains("listarProductosDetalle")){
            dao.writeLog(Integer.parseInt((String) argumentos[0]), "VISUALIZACION", 0, 0);
        } else if(funcion.contains("pagarOrden")){
            //Pagar
            Map<Integer, Integer> productos = dao.getProductosOrden((Integer) argumentos[0]);

            Iterator it = productos.keySet().iterator();
            Integer id_producto, cantidad;
            while(it.hasNext()){
                id_producto = (Integer) it.next();
                cantidad = productos.get(id_producto);
                dao.writeLog(id_producto, "VENTA", 0, cantidad);
            }
        }

    }
}