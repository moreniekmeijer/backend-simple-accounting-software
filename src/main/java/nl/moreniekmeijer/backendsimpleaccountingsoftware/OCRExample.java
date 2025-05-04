package nl.moreniekmeijer.backendsimpleaccountingsoftware;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Scanner;

public class OCRExample {

    public static void main(String[] args) throws Exception {
        String apiKey = "K88089129888957"; // jouw OCR.space API-key
        File file = new File("/Users/macbook/Downloads/WA202503.pdf");

        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";

        URL url = new URL("https://api.ocr.space/parse/image");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("apikey", apiKey);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)
        ) {
            // Bestand toevoegen aan multipart
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(file.getName()).append("\"").append(CRLF);
            writer.append("Content-Type: application/pdf").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(file.toPath(), output);
            output.flush(); // belangrijk!
            writer.append(CRLF).flush();

            // Form param toevoegen: language=eng (optioneel)
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"language\"").append(CRLF);
            writer.append(CRLF).append("eng").append(CRLF).flush();

            // Einde van multipart
            writer.append("--").append(boundary).append("--").append(CRLF).flush();
        }

        // Lees de response
        InputStream responseStream = connection.getInputStream();
        Scanner scanner = new Scanner(responseStream);
        StringBuilder response = new StringBuilder();
        while (scanner.hasNextLine()) {
            response.append(scanner.nextLine());
        }

        System.out.println("API response:\n" + response);

        // JSON parsen
        JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
        if (json.has("ParsedResults") && json.get("ParsedResults").isJsonArray()) {
            String parsedText = json.getAsJsonArray("ParsedResults")
                    .get(0).getAsJsonObject()
                    .get("ParsedText").getAsString();
            System.out.println("\n📝 Gedetecteerde tekst:\n" + parsedText);
        } else {
            System.err.println("OCR mislukt: " + json);
        }
    }
}
