package edu.pucmm.eict.controladores;

import edu.pucmm.eict.util.BaseControlador;
import io.javalin.config.JavalinConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Demuestra las distintas formas de recibir datos en una petición HTTP:
 * query parameters, path parameters y body (form data).
 */
public class RecibirDatosControlador extends BaseControlador {

    public RecibirDatosControlador(JavalinConfig config) {
        super(config);
    }

    @Override
    public void aplicarRutas() {

        /**
         * Parámetros de consulta (query params) en la URL.
         * http://localhost:7000/parametros?matricula=20011126&nombre=Carlos%20Camacho
         */
        config.routes.get("/parametros", ctx -> {
            List<String> salida = new ArrayList<>();
            salida.add("Mostrando todos los parámetros enviados:");
            ctx.queryParamMap().forEach((key, lista) ->
                salida.add(String.format("[%s] = [%s]", key, String.join(",", lista)))
            );
            ctx.result(String.join("\n", salida));
        });

        /**
         * Parámetros como parte de la URL (path params).
         * http://localhost:7000/parametros/20011136/
         */
        config.routes.get("/parametros/{matricula}/", ctx ->
            ctx.result("El estudiante tiene la matrícula: " + ctx.pathParam("matricula"))
        );

        /**
         * Combinación de path params.
         * http://localhost:7000/parametros/20011136/nombre/carloscamacho
         */
        config.routes.get("/parametros/{matricula}/nombre/{nombre}", ctx ->
            ctx.result("Matrícula: " + ctx.pathParam("matricula")
                + " - Nombre: " + ctx.pathParam("nombre"))
        );

        config.routes.get("/parametros/{para1}/{para2}/{para3}", ctx ->
            ctx.result("hhhhh")
        );

        // Ruta ambigua: puede ejecutarse o no dependiendo del orden de registro
        config.routes.get("/parametros/{para4}/{para5}/{para6}", ctx ->
            ctx.result("kkkkkk")
        );

        /**
         * Datos en el cuerpo del mensaje (body / form data).
         * Utilizar el formulario en: http://localhost:7000/formulario.html
         */
        config.routes.post("/parametros", ctx -> {
            System.out.println("Content-Type: " + ctx.header("Content-Type")
                + " - Matrícula (query): " + ctx.queryParam("matricula"));
            List<String> salida = new ArrayList<>();
            salida.add("Mostrando información enviada en el cuerpo:");
            ctx.formParamMap().forEach((key, lista) ->
                salida.add(String.format("[%s] = [%s]", key, String.join(",", lista)))
            );
            ctx.result(String.join("\n", salida));
        });

        /**
         * Leer todos los encabezados HTTP enviados por el cliente.
         * http://localhost:7000/leerheaders
         */
        config.routes.get("leerheaders", ctx -> {
            List<String> salida = new ArrayList<>();
            salida.add("Encabezados enviados en la trama HTTP:");
            ctx.headerMap().forEach((key, valor) ->
                salida.add(String.format("[%s] = [%s]", key, String.join(",", valor)))
            );
            ctx.result(String.join("\n", salida));
        });
    }
}
