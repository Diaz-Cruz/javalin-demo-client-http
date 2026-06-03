package edu.pucmm.eict;

import edu.pucmm.eict.servicios.AnalizadorWeb;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese una URL: ");
        String url = scanner.nextLine().trim();

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            System.out.println("URL invalida. Debe iniciar con http:// o https://");
            return;
        }

        AnalizadorWeb analizador = new AnalizadorWeb();
        analizador.analizar(url);
    }
}
