package co.ecofactory.derivador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        String rutaPOM = args[0];
        String rutaEjecutable = args[1];
        String rutaConfig = args[2];

        String pom = new String(Files.readAllBytes(Paths.get(rutaPOM)));
        ArrayList<String> modulos = (ArrayList<String>) Files.readAllLines(Paths.get(rutaConfig));

        String modulosPOM = "";
        String modulosEjecutable = "";
        String pluginPOMCore = "";
        String pluginPOMCalificacion = "";
        String variabilidad = "";

        for (int i = 0; i < modulos.size(); i++) {
            String modulo = modulos.get(i);

            if (!modulo.startsWith("CORE_")) {

                if (modulo.equals("EnvioMensajes")) {
                    modulosPOM += "<dependency>\n" +
                            "            <groupId>co.ecofactory</groupId>\n" +
                            "            <artifactId>mod-message</artifactId>\n" +
                            "            <version>3.2.1</version>\n" +
                            "        </dependency>";
                }
                if (modulo.equals("CalificarServicios")) {
                    modulosPOM += "<dependency>\n" +
                            "            <groupId>co.ecofactory</groupId>\n" +
                            "            <artifactId>mod-calificacion</artifactId>\n" +
                            "            <version>3.2.1</version>\n" +
                            "        </dependency>";

                    pluginPOMCore += "<plugin>\n" +
                            "<groupId>org.codehaus.mojo</groupId>\n" +
                            "<artifactId>aspectj-maven-plugin</artifactId>\n" +
                            "<configuration>\n" +
                            "<aspectLibraries>\n" +
                            "<aspectLibrary>\n" +
                            "<groupId>co.ecofactory</groupId>\n" +
                            "<artifactId>mod-aspecto</artifactId>\n" +
                            "</aspectLibrary>\n" +
                            "</aspectLibraries>\n" +
                            "</configuration>\n" +
                            "</plugin>";
                }

                if (modulo.equals("Reportes")) {
                    modulosPOM += "<dependency>\n" +
                            "<groupId>co.ecofactory</groupId>\n" +
                            "<artifactId>mod-reporte</artifactId>\n" +
                            "<version>3.2.1</version>\n" +
                            "</dependency>";


                }

                modulosEjecutable += " -D" + modulo + "=ACTIVO ";
                if (!variabilidad.equals("")) {
                    variabilidad += ",";
                }
                variabilidad = modulo;
            }
        }

        if (modulos.contains("Twitter") && modulos.contains("Facebook")) {
            modulosEjecutable += " -DTipoAutenticacion=ALL ";
        } else if (modulos.contains("Twitter") && !modulos.contains("Facebook")) {
            modulosEjecutable += " -DTipoAutenticacion=TWITTER ";
        } else if (!modulos.contains("Twitter") && modulos.contains("Facebook")) {
            modulosEjecutable += " -DTipoAutenticacion=FACEBOOK ";
        }

        modulosEjecutable += " -DVariabilidad=" + variabilidad;


        if (!modulosPOM.equals("")) {
            pom = pom.replaceAll("<!--modulos-->", modulosPOM);
        }

        if (!pluginPOMCore.equals("")) {
            pom = pom.replaceAll("<!--plugins-->", pluginPOMCore);
        }


        Files.write(Paths.get(rutaPOM), pom.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

        String ejecutable = "java -jar target/mod-core-3.2.1-fat.jar " + modulosEjecutable;


        Files.write(Paths.get(rutaEjecutable), ejecutable.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

    }
}
