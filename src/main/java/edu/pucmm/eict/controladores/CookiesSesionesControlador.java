package edu.pucmm.eict.controladores;

import edu.pucmm.eict.encapsulaciones.Usuario;
import edu.pucmm.eict.servicios.FakeServices;
import edu.pucmm.eict.util.BaseControlador;
import io.javalin.config.JavalinConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Demuestra el uso de cookies y sesiones HTTP:
 * - Cookies: almacenamiento en el cliente con tiempo de expiración.
 * - Sesiones: almacenamiento en el servidor asociado a un ID de sesión.
 * - Autenticación simple con sesión.
 */
public class CookiesSesionesControlador extends BaseControlador {

    public static List<String> lista = new ArrayList<>();

    public CookiesSesionesControlador(JavalinConfig config) {
        super(config);
    }

    @Override
    public void aplicarRutas() {

        /**
         * Crea una cookie en el navegador del cliente con tiempo de vida de 120 segundos.
         * http://localhost:7000/crearCookie/micookie/valor-de-la-cookie
         */
        config.routes.get("/crearCookie/{nombre}/{valor}", ctx -> {
            ctx.cookie(ctx.pathParam("nombre"), ctx.pathParam("valor"), 120);
            ctx.cookie("usuario", "CarlosCamacho", 120);
            ctx.result("Cookie creada...");
        });

        /**
         * Lista todas las cookies almacenadas en el cliente para este servidor.
         * http://localhost:7000/listarCookies
         */
        config.routes.get("/listarCookies", ctx -> {
            List<String> salida = new ArrayList<>();
            salida.add("Cookies del cliente:");
            ctx.cookieMap().forEach((key, valor) ->
                salida.add(String.format("[%s] = [%s]", key, String.join(",", valor)))
            );
            if (ctx.cookie("usuario") != null) {
                salida.add("Hola " + ctx.cookie("usuario"));
            } else {
                salida.add("No se envió la cookie 'usuario'");
            }
            ctx.result(String.join("\n", salida));
        });

        /**
         * Login usando cookies para mantener la identidad del usuario.
         * Formulario en: http://localhost:7000/formulario_cookie.html
         */
        config.routes.post("/login-cookies", ctx -> {
            String usuario = ctx.formParam("usuario");
            String contrasena = ctx.formParam("contrasena");
            if (usuario == null || contrasena == null) {
                ctx.redirect("/formulario_cookie.html");
                return;
            }
            ctx.cookie("usuario", usuario, 120);
            ctx.cookie("nombre", "Nombre%20de%20Usuario%20" + usuario, 120);
            ctx.redirect("/inicio-cookie");
        });

        /**
         * Página de inicio protegida por cookie.
         * Redirige al formulario si la cookie no existe.
         */
        config.routes.get("/inicio-cookie", ctx -> {
            if (ctx.cookie("nombre") == null || ctx.cookie("usuario") == null) {
                ctx.redirect("/formulario_cookie.html");
                return;
            }
            ctx.result("Hola " + ctx.cookie("nombre") + ", gracias por su visita!");
        });

        /**
         * Contador de visitas usando sesión del servidor.
         * Cada recarga incrementa el contador; el valor persiste en la sesión.
         * http://localhost:7000/contadorSesion
         */
        config.routes.get("/contadorSesion", ctx -> {
            Integer contador = ctx.sessionAttribute("contador");
            if (contador == null) contador = 0;
            contador++;
            ctx.sessionAttribute("contador", contador);
            ctx.result(String.format(
                "Usted ha visitado esta página %d veces. Sesión ID: #%s",
                contador,
                ctx.req().getSession().getId()
            ));
        });

        /**
         * Invalida la sesión del servidor, eliminando todos sus atributos.
         * http://localhost:7000/invalidarSesion
         */
        config.routes.get("/invalidarSesion", ctx -> {
            String id = ctx.req().getSession().getId();
            ctx.req().getSession().invalidate();
            ctx.result("Sesión con ID: " + id + " fue invalidada");
        });

        /**
         * Autenticación con sesión: guarda el usuario en la sesión para
         * que otros manejadores puedan verificar si está autenticado.
         * Formulario en: http://localhost:7000/login.html
         */
        config.routes.post("/autenticar", ctx -> {
            String nombreUsuario = ctx.formParam("usuario");
            String password = ctx.formParam("password");
            Usuario usuario = FakeServices.getInstancia().autheticarUsuario(nombreUsuario, password);
            ctx.sessionAttribute("usuario", usuario);
            ctx.redirect("/zona-admin-clasica/");
        });

        /**
         * Muestra los tres tipos de almacenamiento de estado en HTTP:
         * - Atributo de request (dura solo la petición actual)
         * - Atributo de sesión (persiste en la sesión, ~30 min)
         * - Variable de aplicación (estática, dura mientras corre la JVM)
         */
        config.routes.get("/contexto", ctx -> {
            ctx.req().setAttribute("variable-request", "valor");
            ctx.sessionAttribute("variable-sesion", ".....");
            lista.add("elemento de aplicación");
            ctx.result("Ver los distintos ámbitos (scopes) en el log del servidor.");
        });
    }
}
