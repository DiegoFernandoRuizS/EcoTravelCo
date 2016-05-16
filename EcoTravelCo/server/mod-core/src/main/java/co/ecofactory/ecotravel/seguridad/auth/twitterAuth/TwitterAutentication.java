package co.ecofactory.ecotravel.seguridad.auth.twitterAuth;

import co.ecofactory.ecotravel.seguridad.auth.basic.Basic;
import co.ecofactory.ecotravel.seguridad.service.SeguridadService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTOptions;

import io.vertx.ext.auth.jwt.JWTAuth;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class TwitterAutentication extends Basic {

    private String ConsumerKey ="0skforJRGms6udw994QIoCyYQ";
    private String ConsumerSecret ="ZO3357tUgjE98OPgNBknCsu38iOwWSjomTAWpNLzltHpA61vpe";
    private HashMap<String, RequestToken> requestTokenMap = new HashMap<String, RequestToken>();
    private HashMap<String, Twitter> requestTwitterMap = new HashMap<String, Twitter>();
    private HashMap<String, AccessToken> accessTokenMap = new HashMap<String, AccessToken>();

    public TwitterAutentication(Vertx ver,DeploymentOptions options) {
        super(ver,options);
        vertx.deployVerticle(this, options);
    }

    public void start() throws Exception {
        this.getVertx().eventBus().consumer("autenticarTwitter", this::autenticar);
        this.getVertx().eventBus().consumer("autenticar", super::autenticar);
        this.getVertx().eventBus().consumer("getPerfil", this::getPerfil);
        this.getVertx().eventBus().consumer("tweet", this::tweet);
        JsonObject config = new JsonObject().put("keyStore", new JsonObject()
                .put("path", System.getenv("KEY_STORE") + "/keystore.jceks")
                .put("type", "jceks")
                .put("password", "secret"));
        provider = JWTAuth.create(this.getVertx(), config);
        provider = generateJWTAuthProvider(this.getVertx());

    }

    public void autenticar(Message<JsonObject> message) {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(ConsumerKey)
                .setOAuthConsumerSecret(ConsumerSecret);

        try {
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();

            try {
                RequestToken requestToken = twitter.getOAuthRequestToken();
                AccessToken accessToken = null;
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String uriTwit = requestToken.getAuthorizationURL();
                requestTokenMap.put(requestToken.getToken(),requestToken);
                requestTwitterMap.put(requestToken.getToken(),twitter);
                JsonObject response = new JsonObject();
                response.put("url", uriTwit);
                message.reply(response);

            } catch (IllegalStateException ie) {
                // access token is already available, or consumer key/secret is not set.
                if (!twitter.getAuthorization().isEnabled()) {
                    System.out.println("OAuth consumer key/secret is not set.");
                    System.exit(-1);
                }
            }
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            message.fail(0, "ERROR al autenticar");
        }
    }

    public void getPerfil(Message<JsonObject> message)  {
        try{
            JsonObject entrada = message.body();

            String oauth_token =entrada.getString("oauth_token");
            String oauth_verifier =entrada.getString("oauth_verifier");
            Twitter twitter = requestTwitterMap.get(oauth_token);
            AccessToken accessToken = null;
            accessToken = twitter.getOAuthAccessToken(requestTokenMap.get(oauth_token), oauth_verifier);
            accessTokenMap.put(oauth_token,accessToken);
            String user =twitter.getAccountSettings().getScreenName();
            User userAu = twitter.showUser(twitter.getId());

            try {
                this.getVertx().eventBus().send("consultarUsuarioPorLogin", user,
                        res -> {
                            if (res.succeeded()) {
                                JsonObject persona = (JsonObject) res.result().body();
                                if (persona != null) {
                                        String token = provider.generateToken(new JsonObject().put("tipo", persona.getString("tipo"))
                                                        .put("id", persona.getInteger("id"))
                                                , new JWTOptions());
                                        JsonObject response = new JsonObject();
                                        response.put("nombre", persona.getString("nombre"));
                                        response.put("apellido", persona.getString("apellido"));
                                        response.put("correo_electronico", persona.getString("correo_electronico"));
                                        response.put("tipo", persona.getString("tipo"));
                                        response.put("token", token);
                                        response.put("user", user);
                                        response.put("foto", userAu.getProfileImageURL());
                                        response.put("oauth_token", oauth_token);
                                        response.put("oauth_verifier", oauth_verifier);
                                        message.reply(response);

                                } else {
                                    message.fail(401, "Error al autenticar el usuario");
                                }

                            } else {
                                if (res.cause() != null) {
                                    res.cause().printStackTrace();
                                }

                                message.fail(401, "Error al autenticar el usuario");
                            }
                        }
                );

            } catch (Exception e) {
                e.printStackTrace();
                message.fail(0, "Obtener Perfil Twitter");

            }


        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            message.fail(0, "ERROR al Procesar");
        }
    }


    public void tweet(Message<JsonObject> message)  {
        try{
            JsonObject entrada = message.body();

            String oauth_token =entrada.getString("oauth_token");
            Twitter twitter = requestTwitterMap.get(oauth_token);
            String tweet=entrada.getString("name")+" Vendedor :"+ entrada.getString("ven")+
                    "\nhttp://ecotravel-co/#/home";
            Status status = twitter.updateStatus(tweet);
            JsonObject response = new JsonObject();
            message.reply(response);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
            message.fail(0, "ERROR al Procesar Tweet");
        }
    }


}
