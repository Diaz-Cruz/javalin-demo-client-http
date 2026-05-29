package edu.pucmm.eict.controladores;

import edu.pucmm.eict.util.BaseControlador;
import io.javalin.config.JavalinConfig;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;

/**
 * Demuestra el manejo de excepciones y códigos de error HTTP en Javalin.
 * Referencia de códigos HTTP: https://developer.mozilla.org/es/docs/Web/HTTP/Status
 */
public class ExcepcionesControlador extends BaseControlador {

    public ExcepcionesControlador(JavalinConfig config) {
        super(config);
    }

    @Override
    public void aplicarRutas() {

        // Lanza una excepción 404 Not Found
        // http://localhost:7000/excepciones/ruta-no-encontrada
        config.routes.get("/excepciones/ruta-no-encontrada", ctx -> {
            throw new NotFoundResponse();
        });

        // Lanza una excepción 401 Unauthorized
        // http://localhost:7000/excepciones/ruta-sin-permisos
        config.routes.get("/excepciones/ruta-sin-permisos", ctx -> {
            throw new UnauthorizedResponse();
        });

        // Provoca un NumberFormatException que es capturado por el handler de excepciones
        // http://localhost:7000/excepciones/provocando-error
        config.routes.get("/excepciones/provocando-error", ctx ->
            ctx.result("Error: " + Integer.parseInt("texto-invalido"))
        );

        /**
         * Manejador global de NumberFormatException.
         * Intercepta cualquier NumberFormatException lanzada en cualquier endpoint.
         */
        config.routes.exception(NumberFormatException.class, (exception, ctx) ->
            ctx.html("Ocurrió un error en la conversión numérica: " + exception.getLocalizedMessage())
        );

        /**
         * Manejador de error 404 para respuestas HTML.
         * Solo se activa cuando el cliente acepta text/html.
         */
        config.routes.error(404, "text/html", ctx ->
            ctx.html("<h1>El recurso consultado no existe. Favor verificar la URL.</h1>")
        );
    }
}
