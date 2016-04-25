package co.ecofactory.ecotravel.message.service;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Hashtable;
import java.util.UUID;

/**
 * Created by samuel on 4/24/16.
 */
public class MessageStore {

    //origen->destino:mensajes
    private static Hashtable<String, Hashtable<String, JsonObject>> conversaciones = new Hashtable<>();

    //destino->origen:mensajes
    private static Hashtable<String, Hashtable<String, JsonObject>> conversaciones2 = new Hashtable<>();

    //id conversaciones:mensajes
    private static Hashtable<String, JsonObject> mensajes = new Hashtable<>();

    public static synchronized JsonObject getMensajes(String origen, String destino) {
        if (conversaciones==null){
            conversaciones = new Hashtable<>();
        }

        if (conversaciones2==null){
            conversaciones2 = new Hashtable<>();
        }


        Hashtable<String, JsonObject> _conversaciones = conversaciones.get(origen);
        if (_conversaciones == null) {
            _conversaciones = new Hashtable<>();
            conversaciones.put(origen, _conversaciones);
        }

        JsonObject _mensajes = _conversaciones.get(destino);

        if (_mensajes == null) {

            //verificar si estoy en el destino de alguna conversacion
            Hashtable<String, JsonObject> _conversaciones2 = conversaciones.get(destino);
            if (_conversaciones2 == null) {
                _conversaciones2 = new Hashtable<>();
                conversaciones2.put(destino, _conversaciones2);
            }

            _mensajes = _conversaciones2.get(origen);

            if ( _mensajes == null) {
                //no estoy ni en el origen ni en el destino

                _mensajes = new JsonObject();
                _mensajes.put("id", UUID.randomUUID().toString());
                _mensajes.put("mensajes", new JsonArray());

                _conversaciones.put(destino, _mensajes);
                mensajes.put(_mensajes.getString("id"), _mensajes);
                _conversaciones2.put(origen, _mensajes);
            }


        }

        return _mensajes;

    }

    public static synchronized void agregarMensaje(JsonObject message) {
        JsonObject _mensajes = mensajes.get(message.getString("id"));
        JsonArray lista = _mensajes.getJsonArray("mensajes");
        lista.add(message);
    }


}
