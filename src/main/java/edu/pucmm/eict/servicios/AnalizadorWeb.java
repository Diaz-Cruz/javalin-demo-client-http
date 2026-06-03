package edu.pucmm.eict.servicios;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AnalizadorWeb {

    static final String MATRICULA_ID = "10153715";

    private final HttpClient cliente = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public void analizar(String url) {
        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

            String contentType = respuesta.headers().firstValue("Content-Type").orElse("desconocido");
            String tipo = tipoDeRecurso(contentType);
            System.out.println("Tipo de recurso: " + tipo);

            if (tipo.equals("HTML")) {
                analizarHtml(respuesta.body(), url);
            }
        } catch (Exception e) {
            System.out.println("Error al consultar la URL: " + e);
        }
    }

    private String tipoDeRecurso(String contentType) {
        String ct = contentType.toLowerCase();
        if (ct.contains("text/html")) {
            return "HTML";
        }
        if (ct.contains("application/pdf")) {
            return "PDF";
        }
        if (ct.contains("image/")) {
            return "Imagen";
        }
        if (ct.contains("application/json")) {
            return "JSON";
        }
        if (ct.contains("text/plain")) {
            return "Texto";
        }
        return "Otro (" + contentType + ")";
    }

    private void analizarHtml(String cuerpo, String url) {
        int lineas = cuerpo.split("\n", -1).length;
        System.out.println("Cantidad de lineas: " + lineas);

        Document doc = Jsoup.parse(cuerpo, url);

        Elements parrafos = doc.select("p");
        System.out.println("Cantidad de parrafos: " + parrafos.size());

        Elements imagenesEnParrafos = doc.select("p img");
        System.out.println("Imagenes dentro de parrafos: " + imagenesEnParrafos.size());

        Elements formularios = doc.select("form");
        int formulariosGet = 0;
        int formulariosPost = 0;
        for (Element formulario : formularios) {
            String metodo = formulario.attr("method").trim().toUpperCase();
            if (metodo.equals("POST")) {
                formulariosPost++;
            } else {
                formulariosGet++;
            }
        }
        System.out.println("Formularios GET: " + formulariosGet);
        System.out.println("Formularios POST: " + formulariosPost);

        int numero = 1;
        for (Element formulario : formularios) {
            String metodo = formulario.attr("method").trim().toUpperCase();
            if (metodo.isEmpty()) {
                metodo = "GET";
            }
            System.out.println("--- Formulario " + numero + " (metodo " + metodo + ") ---");

            Elements inputs = formulario.select("input");
            for (Element input : inputs) {
                String type = input.attr("type");
                if (type.isEmpty()) {
                    type = "text";
                }
                System.out.println("  input type=" + type);
            }

            if (metodo.equals("POST")) {
                String action = formulario.absUrl("action");
                if (action.isEmpty()) {
                    action = url;
                }
                enviarPost(action);
            }
            numero++;
        }
    }

    private void enviarPost(String action) {
        try {
            HttpRequest peticion = HttpRequest.newBuilder()
                    .uri(URI.create(action))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("matricula-id", MATRICULA_ID)
                    .POST(HttpRequest.BodyPublishers.ofString("asignatura=practica1"))
                    .build();

            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());

            System.out.println("  POST a " + action + " -> status " + respuesta.statusCode());

            String cuerpo = respuesta.body();
            if (cuerpo != null && !cuerpo.isEmpty()) {
                int limite = Math.min(200, cuerpo.length());
                System.out.println("  Respuesta: " + cuerpo.substring(0, limite));
            }
        } catch (Exception e) {
            System.out.println("  Error en POST a " + action + ": " + e);
        }
    }
}
