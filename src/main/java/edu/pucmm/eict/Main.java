package edu.pucmm.eict;

import edu.pucmm.eict.controladores.*;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

import java.text.SimpleDateFormat;
import java.util.Date;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hola Mundo en Javalin 7 :-D");

        /**
         * En Javalin 7 TODAS las rutas y manejadores deben registrarse
         * dentro del bloque Javalin.create() antes de llamar a start().
         */
        Javalin app = Javalin.create(config -> {

            // Archivos estáticos servidos desde /publico en el classpath
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/publico";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.aliasCheck = null;
            });

            // Motor de plantillas por defecto: Thymeleaf (usado en /crud-simple y /thymeleaf)
            config.fileRenderer(new JavalinThymeleaf());

            // Rutas del API REST (/api/estudiante) y CRUD tradicional (/crud-simple)
            config.routes.apiBuilder(() -> {

                path("/api", () -> {
                    path("/estudiante", () -> {
                        get(ApiControlador::listarEstudiantes);
                        post(ApiControlador::crearEstudiante);
                        put(ApiControlador::actualizarEstudiante);
                        path("/{matricula}", () -> {
                            get(ApiControlador::estudiantePorMatricula);
                            delete(ApiControlador::eliminarEstudiante);
                        });
                    });
                });

                /**
                 * CRUD con plantillas Thymeleaf (flujo petición-respuesta tradicional).
                 * http://localhost:7000/crud-simple/listar
                 */
                path("/crud-simple/", () -> {
                    get(ctx -> ctx.redirect("/crud-simple/listar"));
                    get("/listar", CrudTradicionalControlador::listar);
                    get("/crear", CrudTradicionalControlador::crearEstudianteForm);
                    post("/crear", CrudTradicionalControlador::procesarCreacionEstudiante);
                    get("/visualizar/{matricula}", CrudTradicionalControlador::visualizarEstudiante);
                    get("/editar/{matricula}", CrudTradicionalControlador::editarEstudianteForm);
                    post("/editar", CrudTradicionalControlador::procesarEditarEstudiante);
                    get("/eliminar/{matricula}", CrudTradicionalControlador::eliminarEstudiante);
                });
            });

            // Endpoint raíz
            config.routes.get("/", ctx -> ctx.result("Hola Mundo en Javalin 7 :-D"));

            // Endpoint auxiliar para los ejemplos de HTML5 (lee la hora del servidor)
            config.routes.get("/fecha", ctx ->
                ctx.result(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()))
            );

            // Header requerido por los navegadores para Service Workers servidos desde el classpath
            config.routes.after(ctx -> {
                if (ctx.path().equalsIgnoreCase("/serviceworkers.js")) {
                    ctx.header("Content-Type", "application/javascript");
                    ctx.header("Service-Worker-Allowed", "/");
                }
            });

            // Conceptos básicos del protocolo HTTP (before, after, verbos, cabeceras)
            new ConceptoBasicosControlador(config).aplicarRutas();

            // Recepción de datos: query params, path params, body form-data
            new RecibirDatosControlador(config).aplicarRutas();

            // Cookies y sesiones HTTP
            new CookiesSesionesControlador(config).aplicarRutas();

            // Zona protegida con sesión (autenticación clásica sin roles)
            new ZonaAdminClasica(config).aplicarRutas();

            // Manejo de excepciones y códigos de error HTTP
            new ExcepcionesControlador(config).aplicarRutas();

            // Plantillas: Thymeleaf, FreeMarker y Velocity
            new PlantillasControlador(config).aplicarRutas();

            // Zona protegida con roles (RBAC)
            new ZonaAdminConRoles(config).aplicarRutas();

            // Resumen visual de todas las rutas registradas → http://localhost:7000/routes
            config.bundledPlugins.enableRouteOverview("/routes");

        });

        app.start(getHerokuAssignedPort());
    }

    /**
     * Devuelve el puerto asignado por Heroku o 7000 en desarrollo local.
     */
    static int getHerokuAssignedPort() {
        String port = new ProcessBuilder().environment().get("PORT");
        return port != null ? Integer.parseInt(port) : 7000;
    }
}
