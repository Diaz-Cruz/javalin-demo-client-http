package edu.pucmm.eict.util;

import io.javalin.config.JavalinConfig;

/**
 * Clase base para todos los controladores del proyecto.
 * En Javalin 7, el registro de rutas se realiza a través de JavalinConfig
 * dentro del bloque Javalin.create(), por eso recibimos JavalinConfig.
 */
public abstract class BaseControlador {

    protected JavalinConfig config;

    public BaseControlador(JavalinConfig config) {
        this.config = config;
    }

    abstract public void aplicarRutas();
}
