package co.ecofactory.ecotravel.reporte.service;

import co.ecofactory.ecotravel.conexion.ConnectionUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.apache.commons.lang.ObjectUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by samuel on 2/15/16.
 */
public class ReporteService extends AbstractVerticle {
    //private ReporteDAO dao;

    @Override
    public void start() throws Exception {
        // registro los metodos en el bus
        this.getVertx().eventBus().consumer("generarReporte", this::generarReporte);
    }


    public void generarReporte(Message<JsonObject> message) {
        boolean error = false;
        //Parametros de entrada
        int idUsuario = message.body().getInteger("idUsuario");
        String reporte = message.body().getString("reporte");
        Date fechaInicial = java.sql.Date.valueOf(message.body().getString("fechaInicial"));
        Date fechaFinal = java.sql.Date.valueOf(message.body().getString("fechaFinal"));

        //Nombre de reporte y archivo
        String nomPdf="", nomReporte="";
        switch(reporte) {
            case "ADMIN":
                /*if(){

                }else{

                }*/
                nomPdf = "AdminInformeVentas";
                nomReporte = "Administrador_Ventas";
                break;

            case "CONSULTAS":
                nomPdf = "ProductorConsultas"+idUsuario;
                nomReporte = "Productor_Consultas";
                break;

            case "VENTAS":
                nomPdf = "ProductorVentas"+idUsuario;
                nomReporte = "Productor_Ventas";
                break;

            case "CLIENTES":
                nomPdf = "ProductorClientes"+idUsuario;
                nomReporte = "Productor_Cliente";
                break;

            default:
                error = true;
        }

        //Generar reporte
        if(!error){
            // Parameters for report
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("Fecha_Inicial", fechaInicial);
            parameters.put("Fecha_Final", fechaFinal);
            parameters.put("id_usuario", idUsuario);

            String ruta = "mod-reporte/src/main/java/co/ecofactory/ecotravel/reporte/service/template/"+ nomReporte +".jrxml";
            String reportSrcFile = System.getenv("KEY_STORE");
            reportSrcFile = reportSrcFile.replace("mod-core\\src\\main\\java", ruta);

            String rutaArchivo = System.getenv("KEY_STORE");
            rutaArchivo = rutaArchivo.replace("server\\mod-core\\src\\main\\java", "front\\reports");

            try {
                // First, compile jrxml file.
                JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);
                Connection conn = ConnectionUtils.getConnection();

                JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);


                // Make sure the output directory exists.
                File outDir = new File(rutaArchivo);
                outDir.mkdirs();

                // PDF Exportor.
                JRPdfExporter exporter = new JRPdfExporter();

                ExporterInput exporterInput = new SimpleExporterInput(print);
                // ExporterInput
                exporter.setExporterInput(exporterInput);

                // ExporterOutput
                OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(
                        rutaArchivo + "\\"+nomPdf+".pdf");
                // Output
                exporter.setExporterOutput(exporterOutput);

                //
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                exporter.setConfiguration(configuration);
                exporter.exportReport();

            } catch (JRException e) {
                error = true;
                System.out.println("Error Generando Reporte.\n" + e);
            } catch (ClassNotFoundException e) {
                error = true;
                System.out.println("Error Generando Reporte.\n" + e);
            } catch (SQLException e) {
                error = true;
                System.out.println("Error Generando Reporte.\n" + e);
            }
        }

        JsonObject obj = new JsonObject();
        obj.put("ruta", nomPdf+".pdf");
        obj.put("error", error);
        message.reply(obj);
    }
}
