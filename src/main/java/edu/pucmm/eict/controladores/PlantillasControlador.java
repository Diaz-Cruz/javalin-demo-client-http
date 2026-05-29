package edu.pucmm.eict.controladores;

import edu.pucmm.eict.encapsulaciones.Estudiante;
import edu.pucmm.eict.util.BaseControlador;
import io.javalin.config.JavalinConfig;
import io.javalin.http.ContentType;
import io.javalin.rendering.template.JavalinFreemarker;
import io.javalin.rendering.template.JavalinVelocity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Demuestra el uso de los tres motores de plantillas incluidos:
 * - Thymeleaf (motor por defecto, configurado en Main)
 * - FreeMarker (.ftl)
 * - Velocity (.vm)
 */
public class PlantillasControlador extends BaseControlador {

    public PlantillasControlador(JavalinConfig config) {
        super(config);
    }

    @Override
    public void aplicarRutas() {

        /**
         * Ejemplo con Thymeleaf (motor por defecto configurado en Main).
         * Documentación: https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html
         * http://localhost:7000/thymeleaf
         */
        config.routes.get("/thymeleaf", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("titulo", "Ejemplo de funcionalidad Thymeleaf");
            modelo.put("listaEstudiante", getEstudiantes());
            ctx.render("/templates/thymeleaf/funcionalidad.html", modelo);
        });

        /**
         * Ejemplo con FreeMarker.
         * Documentación: https://freemarker.apache.org/docs/dgui.html
         * http://localhost:7000/freemarker/datosEstudiante/20011136
         */
        config.routes.get("/freemarker/datosEstudiante/{matricula}", ctx -> {
            int matricula = ctx.pathParamAsClass("matricula", Integer.class).required().get();
            Estudiante estudiante = new Estudiante(matricula, "Estudiante matrícula: " + matricula, "ISC");

            Map<String, Object> modelo = new HashMap<>();
            modelo.put("estudiante", estudiante);

            var render = new JavalinFreemarker();
            ctx.contentType(ContentType.HTML);
            ctx.result(render.render("/templates/freemarker/datosEstudiante.ftl", modelo, ctx));
        });

        /**
         * Ejemplo con Apache Velocity.
         * Documentación: https://velocity.apache.org/engine/2.2/user-guide.html
         * http://localhost:7000/velocity
         */
        config.routes.get("/velocity", ctx -> {
            Map<String, Object> modelo = new HashMap<>();
            modelo.put("titulo", "Ejemplo de funcionalidad Velocity");
            modelo.put("listaEstudiante", getEstudiantes());

            var render = new JavalinVelocity();
            ctx.contentType(ContentType.HTML);
            ctx.result(render.render("/templates/velocity/funcionalidad.vm", modelo, ctx));
        });
    }

    @NotNull
    private List<Estudiante> getEstudiantes() {
        List<Estudiante> lista = new ArrayList<>();
        lista.add(new Estudiante(20011136, "Carlos Camacho", "ITT"));
        lista.add(new Estudiante(20011137, "Otro Estudiante", "ISC"));
        lista.add(new Estudiante(20011138, "Otro más", "ISC"));
        return lista;
    }
}
