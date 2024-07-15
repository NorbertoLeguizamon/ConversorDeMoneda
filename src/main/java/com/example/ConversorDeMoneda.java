package com.example;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

public class ConversorDeMoneda {
    // Clave API para el servicio de ExchangeRate-API
    private static final String CLAVE_API = "98186edfce8284a03936a894";
    // URL base para las solicitudes de la API
    private static final String URL_BASE = "https://v6.exchangerate-api.com/v6/";

    public static void main(String[] args) {
        Scanner escaner = new Scanner(System.in); // Crear un objeto Scanner para leer la entrada del usuario

        while (true) {
            // Mostrar todas las monedas disponibles
            System.out.println("Monedas disponibles:");
            JsonObject tasasDeConversion = null; // Variable para almacenar las tasas de conversión
            try {
                // Obtener las tasas de conversión para USD
                tasasDeConversion = obtenerTasasDeConversion("USD");
                // Iterar sobre las tasas de conversión y mostrar cada moneda
                for (Map.Entry<String, JsonElement> entrada : tasasDeConversion.entrySet()) {
                    System.out.println(entrada.getKey());
                }
            } catch (Exception e) {
                // Manejar cualquier error al obtener las tasas de conversión
                System.out.println("Error al obtener las tasas de cambio: " + e.getMessage());
                return; // Salir del programa si hay un error
            }

            // Pedir al usuario que ingrese la moneda base
            System.out.print("Ingrese la moneda base: ");
            String monedaBase = escaner.nextLine().toUpperCase(); // Leer la entrada y convertirla a mayúsculas

            // Pedir al usuario que ingrese la moneda de destino
            System.out.print("Ingrese la moneda de destino: ");
            String monedaDestino = escaner.nextLine().toUpperCase(); // Leer la entrada y convertirla a mayúsculas

            // Pedir al usuario que ingrese la cantidad a convertir
            System.out.print("Ingrese la cantidad a convertir: ");
            double cantidad = escaner.nextDouble(); // Leer la cantidad a convertir

            try {
                // Obtener la tasa de conversión entre la moneda base y la moneda de destino
                double tasa = obtenerTasaDeCambio(monedaBase, monedaDestino);
                // Calcular la cantidad convertida
                double cantidadConvertida = cantidad * tasa;
                // Mostrar el resultado de la conversión
                System.out.printf("%.2f %s son %.2f %s%n", cantidad, monedaBase, cantidadConvertida, monedaDestino);
            } catch (Exception e) {
                // Manejar cualquier error al obtener la tasa de conversión
                System.out.println("Error al obtener la tasa de cambio: " + e.getMessage());
            }

            // Preguntar al usuario si desea realizar otra conversión
            System.out.print("¿Desea realizar otra conversión? (s/n): ");
            escaner.nextLine(); // Consumir el salto de línea pendiente
            String respuesta = escaner.nextLine(); // Leer la respuesta del usuario
            if (!respuesta.equalsIgnoreCase("s")) {
                // Salir del ciclo si la respuesta no es 's'
                break;
            }
        }

        // Mensaje de despedida
        System.out.println("Gracias por usar el conversor de moneda.");
    }

    // Método para obtener la tasa de conversión entre dos monedas
    private static double obtenerTasaDeCambio(String monedaBase, String monedaDestino) throws Exception {
        // Obtener las tasas de conversión para la moneda base
        JsonObject tasasDeConversion = obtenerTasasDeConversion(monedaBase);
        // Devolver la tasa de conversión para la moneda de destino
        return tasasDeConversion.get(monedaDestino).getAsDouble();
    }

    // Método para obtener las tasas de conversión para una moneda base
    private static JsonObject obtenerTasasDeConversion(String monedaBase) throws Exception {
        // Construir la URL de la solicitud de la API
        String urlStr = URL_BASE + CLAVE_API + "/latest/" + monedaBase;
        URL url = new URL(urlStr); // Crear un objeto URL con la URL de la solicitud
        HttpURLConnection solicitud = (HttpURLConnection) url.openConnection(); // Abrir la conexión HTTP
        solicitud.connect(); // Conectar a la API

        // Crear un analizador de JSON para leer la respuesta
        JsonParser jp = new JsonParser();
        // Leer la respuesta y convertirla a un objeto JSON
        JsonElement root = jp.parse(new InputStreamReader((InputStream) solicitud.getContent()));
        JsonObject jsonobj = root.getAsJsonObject(); // Convertir el elemento JSON raíz a un objeto JSON

        // Verificar si la solicitud fue exitosa
        if (!jsonobj.get("result").getAsString().equals("success")) {
            throw new Exception("API request failed"); // Lanzar una excepción si la solicitud falló
        }

        // Devolver el objeto JSON que contiene las tasas de conversión
        return jsonobj.getAsJsonObject("conversion_rates");
    }
}
