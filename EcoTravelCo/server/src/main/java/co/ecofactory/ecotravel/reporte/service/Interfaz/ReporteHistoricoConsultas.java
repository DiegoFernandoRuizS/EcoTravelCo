package co.ecofactory.ecotravel.reporte.service.dao;


public class ReporteHistoricoConsultas implements ReporteInterfaz{
    private JDBCClient dataAccess;

    public void setDataAcces(JDBCClient dataAccess){
        this.dataAccess = dataAccess;
    }

    public CompletableFuture<List<JsonObject>> getInfoReporte(JsonObject data){
        final CompletableFuture<List<JsonObject>> res = new CompletableFuture<List<JsonObject>>();
        String query = "SELECT mp_orden.*, sum(mp_orden_item.cantidad) as cantidad " +
                "FROM mp_orden INNER JOIN mp_orden_item ON mp_orden.id = mp_orden_item.id_orden_id " +
                "WHERE mp_orden.id_cliente_id = ? and mp_orden.estado <> " +
                "(SELECT mp_lista_valores.valor FROM mp_lista_valores WHERE mp_lista_valores.codigo = 'CARRO') " +
                "GROUP BY mp_orden.id, mp_orden.estado, mp_orden.preciototal";

        JsonArray params = new JsonArray();
        //JsonUtils.add(params, );

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().queryWithParams(query, params, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().getRows());
                    } else {
                        data.cause().printStackTrace();
                        res.completeExceptionally(data.cause());
                    }
                });
            } else {
                conn.cause().printStackTrace();
            }
            try{
                conn.result().close();
            }catch(Exception e){

            }
        });
        return res;
    }
}
