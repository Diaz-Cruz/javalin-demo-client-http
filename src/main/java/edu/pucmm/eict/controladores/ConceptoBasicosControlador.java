package edu.pucmm.eict.controladores;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;

/**
 * Demuestra los conceptos básicos del protocolo HTTP:
 * manejadores before/after, los distintos verbos HTTP y cómo leer
 * y escribir cabeceras en la trama de petición/respuesta.
 */
public class ConceptoBasicosControlador {

    private final JavalinConfig config;

    public ConceptoBasicosControlador(JavalinConfig config) {
        this.config = config;
    }

    public void aplicarRutas() {

        /**
         * Manejador before que se aplica a TODAS las llamadas.
         * Se ejecuta antes del handler del endpoint.
         */
        config.routes.before(ctx -> {
            String mensaje = String.format(
                "Manejador before (global): host=%s, path=%s, método=%s",
                ctx.req().getRemoteHost(),
                ctx.path(),
                ctx.req().getMethod()
            );
            System.out.println(mensaje);
        });

        /**
         * Manejador before acotado al path /isc415.
         * Permite establecer variables de contexto para el endpoint.
         */
        config.routes.before("/isc415", ctx -> {
            String mensaje = String.format(
                "Manejador before (/isc415): uri=%s, método=%s",
                ctx.req().getRequestURI(),
                ctx.req().getMethod()
            );
            ctx.attribute("mi-variable", "Hola Mundo"); // variable disponible en el handler
            System.out.println(mensaje);
        });

        /**
         * Handler GET para /isc415.
         * Demuestra cómo leer/escribir cabeceras HTTP y el método del request.
         * http://localhost:7000/isc415
         */
        config.routes.get("/isc415", ctx -> {
            String metodo = ctx.method().name();
            ctx.res().setHeader("asignatura", "ISC-415");
            ctx.header("otro-header", "Mi header enviado");
            ctx.result("Endpoint " + ctx.req().getRequestURI()
                + " - Método: " + metodo
                + " - Variable: " + ctx.attribute("mi-variable"));
        });

        /**
         * Manejador after para TODAS las llamadas.
         * Se ejecuta después del handler del endpoint.
         */
        config.routes.after(ctx -> {
            String mensaje = String.format(
                "Handler after (global): host=%s, contextPath=%s",
                ctx.req().getRemoteHost(),
                ctx.contextPath()
            );
            System.out.println(mensaje);
        });

        /**
         * Manejador after acotado al path /isc415.
         */
        config.routes.after("/isc415", ctx -> {
            String mensaje = String.format(
                "Manejador after (/isc415): uri=%s, método=%s",
                ctx.req().getRequestURI(),
                ctx.req().getMethod()
            );
            ctx.header("incluido-after", "fue ejecutando en bloque after");
            System.out.println(mensaje);
        });

        /**
         * Los diferentes verbos HTTP pueden compartir el mismo path.
         * Prueba con: POST, PUT, DELETE, OPTIONS, PATCH, HEAD en /isc415
         */
        config.routes.post("/isc415", this::procesamiento);
        config.routes.put("/isc415", this::procesamiento);
        config.routes.delete("/isc415", this::procesamiento);
        config.routes.options("/isc415", this::procesamiento);
        config.routes.patch("/isc415", this::procesamiento);
        config.routes.head("/isc415", this::procesamiento);

        /**
         * Devuelve el MIME type correcto para el manifiesto de caché del ejemplo de Service Worker.
         */
        config.routes.after("/html5/sinconexion.appcache", ctx -> {
            System.out.println("Enviando cabecera del manifiesto de caché...");
            ctx.contentType("text/cache-manifest");
        });
    }

    private void procesamiento(Context ctx) {
        ctx.result("Trabajando con el método: " + ctx.method()
            + " - Header[profesor] = " + ctx.header("profesor"));
    }
}
