/**
 * Created by Jorge on 24/04/2016.
 */

package co.ecofactory.ecotravel.direcciones.service.factory.products;

import io.vertx.ext.jdbc.JDBCClient;


public abstract class Direccion {

    public JDBCClient dataAccess;

    private int tipo;

    public abstract int getType();

}