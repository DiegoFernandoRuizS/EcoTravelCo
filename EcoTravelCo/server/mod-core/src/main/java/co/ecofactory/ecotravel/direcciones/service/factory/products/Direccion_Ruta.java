/**
 * Created by Jorge on 24/04/2016.
 */


package co.ecofactory.ecotravel.direcciones.service.factory.products;

import co.ecofactory.ecotravel.utils.JsonUtils;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CompletableFuture;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Document;


public class Direccion_Ruta extends Direccion{


    public Direccion_Ruta(Vertx vertx, JsonObject conf) {

        dataAccess = JDBCClient.createShared(vertx, conf);
    }


    public int getType() {

        return 1;
    }



    public String[] getLatLongPositions(String address) throws Exception
    {
        int responseCode = 0;
        String api = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=true";
        URL url = new URL(api);
        HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
        httpConnection.connect();
        responseCode = httpConnection.getResponseCode();
        if(responseCode == 200)
        {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();;
            Document document = builder.parse(httpConnection.getInputStream());
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("/GeocodeResponse/status");
            String status = (String)expr.evaluate(document, XPathConstants.STRING);
            if(status.equals("OK"))
            {
                expr = xpath.compile("//geometry/location/lat");
                String latitude = (String)expr.evaluate(document, XPathConstants.STRING);
                expr = xpath.compile("//geometry/location/lng");
                String longitude = (String)expr.evaluate(document, XPathConstants.STRING);
                return new String[] {latitude, longitude};
            }
            else
            {
                throw new Exception("Error from the API - response status: "+status);
            }
        }
        return null;
    }

    //Insertar DireccionAsociada al producto
    public CompletableFuture<JsonObject> insertarDireccion(JsonObject nuevoProducto) {
        final CompletableFuture<JsonObject> res = new CompletableFuture<>();
        //Definicion de los datos a guardar del producto

        JsonArray params2 = new JsonArray();

        String direccion = nuevoProducto.getString("nombredireccion", "");
        String latitud = nuevoProducto.getString("latitud", "");
        String longitud = nuevoProducto.getString("longitud", "");
        String pais = nuevoProducto.getString("pais", "");
        String departamento = nuevoProducto.getString("departamento", "");
        String ciudad = nuevoProducto.getString("ciudad", "");

        JsonUtils.add(params2, direccion);
        JsonUtils.add(params2, Double.parseDouble(latitud));
        JsonUtils.add(params2, Double.parseDouble(longitud));
        JsonUtils.add(params2, ciudad);
        JsonUtils.add(params2, departamento);
        JsonUtils.add(params2, pais);


        String query2 = "INSERT INTO mp_direccion(\n" +
                "            id, nombre, latitud, longitud, ciudad, departamento, pais)\n" +
                "    VALUES (nextval('mp_direccion_id_seq'),\n" +
                "     ?,\n" +
                "     ?, \n" +
                "     ?, \n" +
                "     ?, \n" +
                "     ?, \n" +
                "     ?);\n";

        dataAccess.getConnection(conn -> {
            if (conn.succeeded()) {
                conn.result().updateWithParams(query2, params2, data -> {
                    if (data.succeeded()) {
                        res.complete(data.result().toJson());
                    } else {
                        data.cause().printStackTrace();
                        System.out.println("Error insertar direccion en DAO producto");
                        res.completeExceptionally(data.cause());
                    }
                });
            } else {
                conn.cause().printStackTrace();
            }
            try {
                conn.result().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return res;
    }


    public String[] getLatLongPositionsIp(String address) throws Exception
    {
        try {
            LookupService lookup = new LookupService( new File( System.getenv("KEY_STORE") +"\\GeoLiteCity.dat"),LookupService.GEOIP_MEMORY_CACHE);
            Location locationServices = lookup.getLocation(address);
            return new String[] {String.valueOf(locationServices.latitude), String.valueOf(locationServices.longitude)};

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
