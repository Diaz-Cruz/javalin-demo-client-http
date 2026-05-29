package edu.pucmm.eict.controladores;

import edu.pucmm.eict.encapsulaciones.Usuario;
import edu.pucmm.eict.servicios.FakeServices;
import edu.pucmm.eict.util.BaseControlador;
import edu.pucmm.eict.util.RolesApp;
import io.javalin.config.JavalinConfig;
import io.javalin.http.UnauthorizedResponse;

/**
 * Demuestra el control de acceso basado en roles (RBAC) en Javalin 7.
 * Cada endpoint declara los roles que pueden accedlo; el filtro beforeMatched
 * verifica que el usuario autenticado tenga al menos uno de esos roles.
 *
 * Usuarios de prueba (ver FakeServices):
 *  - admin   / 1234       → roles: ROLE_ADMIN, LOGUEADO, CUALQUIERA
 *  - logueado / logueado  → roles: CUALQUIERA
 *  - usuario  / usuario   → roles: ROLE_USUARIO
 */
public class ZonaAdminConRoles extends BaseControlador {

    private final FakeServices fakeServices = FakeServices.getInstancia();

    public ZonaAdminConRoles(JavalinConfig config) {
        super(config);
    }

    @Override
    public void aplicarRutas() {

        /**
         * beforeMatched se ejecuta solo cuando se encontró un endpoint que coincide.
         * Verifica sesión y rol antes de dejar pasar la petición.
         */
        config.routes.beforeMatched("/zona-admin-role*", ctx -> {
            if (ctx.routeRoles().isEmpty()) {
                return; // endpoint sin roles declarados, acceso libre
            }

            Usuario usuario = ctx.sessionAttribute("usuario");
            if (usuario == null) {
                throw new UnauthorizedResponse();
            }

            System.out.println("Roles requeridos por la ruta: " + ctx.routeRoles());

            Usuario usuarioTmp = fakeServices.getListaUsuarios().stream()
                    .filter(u -> u.getUsuario().equalsIgnoreCase(usuario.getUsuario()))
                    .findAny()
                    .orElseThrow(() -> new UnauthorizedResponse("Usuario sin roles para acceder."));

            System.out.println("Roles del usuario: " + usuarioTmp.getListaRoles());

            boolean tienePermiso = usuarioTmp.getListaRoles().stream()
                    .anyMatch(role -> ctx.routeRoles().contains(role));

            if (!tienePermiso) {
                throw new UnauthorizedResponse("No tiene el rol requerido para acceder.");
            }
        });

        // Requiere estar autenticado (LOGUEADO). http://localhost:7000/zona-admin-role
        config.routes.get("/zona-admin-role", ctx ->
            ctx.result("Con permiso para acceder a la zona"), RolesApp.LOGUEADO
        );

        // Solo administradores. http://localhost:7000/zona-admin-role/admin
        config.routes.get("/zona-admin-role/admin", ctx ->
            ctx.result("Debe ser administrador"), RolesApp.ROLE_ADMIN
        );

        // Solo usuarios con rol ROLE_USUARIO. http://localhost:7000/zona-admin-role/cliente
        config.routes.get("/zona-admin-role/cliente", ctx ->
            ctx.result("Debe ser cliente"), RolesApp.ROLE_USUARIO
        );

        // Cualquier rol declarado. http://localhost:7000/zona-admin-role/otro-rol
        config.routes.get("/zona-admin-role/otro-rol", ctx ->
            ctx.result("Cualquier rol definido puede acceder"), RolesApp.CUALQUIERA
        );
    }
}
