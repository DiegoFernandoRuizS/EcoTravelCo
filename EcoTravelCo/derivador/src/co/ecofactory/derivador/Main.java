package co.ecofactory.derivador;

import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws Exception {
        String rutaBASE = args[0];
        String rutaPOM = rutaBASE + "/pom.xml";
        String rutaEjecutable = args[1];
        String rutaConfig = args[2];
        String rutaMaven = args[3];
        String rutaJKS = args[4];

        String pom = new String(Files.readAllBytes(Paths.get(rutaBASE + "/pom.base")));
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
                            "                <groupId>org.codehaus.mojo</groupId>\n" +
                            "                <artifactId>aspectj-maven-plugin</artifactId>\n" +
                            "                <version>1.8</version>\n" +
                            "                <executions>\n" +
                            "                    <execution>\n" +
                            "                        <goals>\n" +
                            "                            <goal>compile</goal>\n" +
                            "                            <goal>test-compile</goal>\n" +
                            "                        </goals>\n" +
                            "                    </execution>\n" +
                            "\n" +
                            "                </executions>\n" +
                            "                <configuration>\n" +
                            "                    <complianceLevel>1.8</complianceLevel>\n" +
                            "                    <source>1.8</source>\n" +
                            "                    <target>1.8</target>\n" +
                            "                    <aspectLibraries>\n" +
                            "                        <aspectLibrary>\n" +
                            "                            <groupId>co.ecofactory</groupId>\n" +
                            "                            <artifactId>mod-aspecto</artifactId>\n" +
                            "                        </aspectLibrary>\n" +
                            "                    </aspectLibraries>\n" +
                            "                </configuration>\n" +
                            "            </plugin>";
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
                variabilidad += modulo;
            }
        }

        if (modulos.contains("Twitter") && modulos.contains("Facebook")) {
            modulosEjecutable += " -DTipoAutenticacion=ALL ";
        } else if (modulos.contains("Twitter") && !modulos.contains("Facebook")) {
            modulosEjecutable += " -DTipoAutenticacion=TWITTER ";
        } else if (!modulos.contains("Twitter") && modulos.contains("Facebook")) {
            modulosEjecutable += " -DTipoAutenticacion=FACEBOOK ";
        }else{
            modulosEjecutable += " -DTipoAutenticacion=BASIC ";
        }

        modulosEjecutable += " -DVariabilidad=" + variabilidad;


        if (!modulosPOM.equals("")) {
            pom = pom.replaceAll("<!--modulos-->", modulosPOM);
        }

        if (!pluginPOMCore.equals("")) {
            pom = pom.replaceAll("<!--plugins-->", pluginPOMCore);
        }


        Files.write(Paths.get(rutaPOM), pom.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);

        //ejecutar el pom.xml y esperar a que se cree el jar

        InvocationRequest request = new DefaultInvocationRequest();
        //request.setPomFile(new File(rutaPOM));
        request.setBaseDirectory(new File(rutaBASE));

        request.setGoals(Arrays.asList("clean", "package assembly:single"));
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(rutaMaven));
        invoker.setMavenExecutable(new File(rutaMaven + "/bin/mvn"));
        InvocationResult result = invoker.execute(request);

        if (result.getExitCode() != 0) {
            result.getExecutionException().printStackTrace();
        } else {
            //copiando archivos
            Files.copy(Paths.get(rutaBASE + "/target/mod-core-3.2.1-jar-with-dependencies.jar"), Paths.get(rutaEjecutable + "/mod-core-3.2.1-jar-with-dependencies.jar"), StandardCopyOption.REPLACE_EXISTING);
            String ejecutable = "export KEY_STORE="  + rutaJKS + "\n" +
                    "java  " + modulosEjecutable + " -jar mod-core-3.2.1-jar-with-dependencies.jar";


            Files.write(Paths.get(rutaEjecutable + "/ejecutar.sh"), ejecutable.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(Paths.get(rutaEjecutable + "/ejecutar.bat"), ejecutable.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        }


    }
}
