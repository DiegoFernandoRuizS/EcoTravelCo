package co.ecofactory.ecotravel.reporte.service.Interfaz;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ReporteInterfaz{

    public void setDataAcces(JDBCClient dataAccess);

    public CompletableFuture<List<JsonObject>> getInfoReporte(JsonObject data);
}
