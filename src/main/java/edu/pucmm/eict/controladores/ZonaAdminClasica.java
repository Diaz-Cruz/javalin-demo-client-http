package edu.pucmm.eict.controladores;

import edu.pucmm.eict.encapsulaciones.Usuario;
import edu.pucmm.eict.util.BaseControlador;
import io.javalin.config.JavalinConfig;

/**
 * Demuestra la protección de rutas mediante sesión HTTP (autenticación clásica sin roles).
 * El filtro before verifica que exista un usuario en sesión antes de acceder a /zona-admin-clasica/.
 *
 * Las sesiones pueden ser vulnerables al robo de sesión:
 * https://es.wikipedia.org/wiki/Secuestro_de_sesi%C3%B3n
 * (ver también PruebaRoboSesion.java)
 */
public class ZonaAdminClasica extends BaseControlador {

    public ZonaAdminClasica(JavalinConfig config) {
        super(config);
    }

    @Override
    public void aplicarRutas() {

        /**
         * Filtro before: intercepta todas las llamadas a /zona-admin-clasica/ y sub-rutas.
         * Si no hay sesión activa, redirige a la página de error 401.
         * Autenticarse primero en: http://localhost:7000/login.html
         */
        config.routes.before("/zona-admin-clasica/*", ctx -> {
            Usuario usuario = ctx.sessionAttribute("usuario");
            if (usuario == null) {
                ctx.redirect("/401.html");
            }
        });

        // http://localhost:7000/zona-admin-clasica/
        config.routes.get("/zona-admin-clasica/", ctx -> {
            Usuario usuario = ctx.sessionAttribute("usuario");
            ctx.result("Zona Admin (auth clásica) — Usuario: " + usuario.getUsuario());
        });

        // http://localhost:7000/zona-admin-clasica/otro-zona/otra/
        config.routes.get("/zona-admin-clasica/otro-zona/otra/", ctx ->
            ctx.result("El filtro controla todas las rutas por debajo del prefijo...")
        );
    }
}
