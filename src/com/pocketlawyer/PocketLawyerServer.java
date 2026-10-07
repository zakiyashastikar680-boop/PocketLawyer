package com.pocketlawyer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class PocketLawyerServer {

    public static void main(String[] args) throws Exception {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8080), 0);

        server.createContext(
                "/api/analyze",
                PocketLawyerServer::analyzeProblem
        );

        server.start();

        System.out.println(
                "================================="
        );

        System.out.println(
                " Pocket Lawyer AI Server Started"
        );

        System.out.println(
                " http://localhost:8080"
        );

        System.out.println(
                "================================="
        );
    }


    private static void analyzeProblem(
            HttpExchange exchange) throws IOException {


        exchange.getResponseHeaders()
                .add("Access-Control-Allow-Origin", "*");

        exchange.getResponseHeaders()
                .add("Access-Control-Allow-Headers",
                        "Content-Type");

        exchange.getResponseHeaders()
                .add("Access-Control-Allow-Methods",
                        "POST, OPTIONS");


        if (exchange.getRequestMethod()
                .equalsIgnoreCase("OPTIONS")) {

            exchange.sendResponseHeaders(204, -1);

            return;
        }


        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    405,
                    "{\"error\":\"POST required\"}"
            );

            return;
        }


        InputStream input =
                exchange.getRequestBody();


        String body =
                new String(
                        input.readAllBytes(),
                        StandardCharsets.UTF_8
                );


        String category =
                extractValue(body, "category");


        String explanation;


        String[] documents;


        switch (category) {

            case "Rental":

                explanation =
                        "This appears to be a rental or housing-related issue. Review your agreement, payment records and relevant communications. The exact legal process depends on the facts and applicable local rules.";

                documents =
                        new String[]{
                                "Rental agreement",
                                "Rent payment records",
                                "Notices or messages",
                                "Relevant receipts"
                        };

                break;


            case "Consumer":

                explanation =
                        "This appears to be a consumer-related issue. Keep records of the purchase, payment, communication and any complaint already submitted.";

                documents =
                        new String[]{
                                "Purchase invoice",
                                "Payment receipt",
                                "Warranty information",
                                "Complaint communication"
                        };

                break;


            case "Employment":

                explanation =
                        "This appears to be an employment-related issue. Organize employment records, relevant communications and any notices connected with the situation.";

                documents =
                        new String[]{
                                "Employment documents",
                                "Salary records",
                                "Relevant emails",
                                "Notices or communications"
                        };

                break;


            case "Family":

                explanation =
                        "This appears to involve a family-related legal matter. Because these matters can be sensitive and fact-specific, consider organizing relevant records and seeking qualified legal advice.";

                documents =
                        new String[]{
                                "Relevant identity documents",
                                "Official records",
                                "Existing agreements",
                                "Relevant communications"
                        };

                break;


            default:

                explanation =
                        "Your description has been received. The appropriate next step depends on the specific facts and applicable law. Organize relevant documents and consider qualified legal advice for important matters.";

                documents =
                        new String[]{
                                "Relevant documents",
                                "Receipts or records",
                                "Important communications"
                        };

                break;
        }


        String json =
                createJSON(
                        category,
                        explanation,
                        documents
                );


        sendResponse(
                exchange,
                200,
                json
        );
    }


    private static String extractValue(
            String json,
            String key) {


        String search =
                "\"" + key + "\"";


        int keyPosition =
                json.indexOf(search);


        if (keyPosition == -1) {

            return "General";

        }


        int colon =
                json.indexOf(
                        ":",
                        keyPosition
                );


        int start =
                json.indexOf(
                        "\"",
                        colon
                );


        int end =
                json.indexOf(
                        "\"",
                        start + 1
                );


        if (start == -1 || end == -1) {

            return "General";

        }


        return json.substring(
                start + 1,
                end
        );
    }


    private static String createJSON(
            String category,
            String explanation,
            String[] documents) {


        StringBuilder json =
                new StringBuilder();


        json.append("{");

        json.append("\"category\":\"")
                .append(escape(category))
                .append("\",");


        json.append("\"explanation\":\"")
                .append(escape(explanation))
                .append("\",");


        json.append("\"documents\":[");


        for (int i = 0;
             i < documents.length;
             i++) {

            if (i > 0) {

                json.append(",");

            }

            json.append("\"")
                    .append(escape(documents[i]))
                    .append("\"");
        }


        json.append("]");

        json.append("}");


        return json.toString();
    }


    private static String escape(
            String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }


    private static void sendResponse(
            HttpExchange exchange,
            int status,
            String response)
            throws IOException {


        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json"
                );


        exchange.sendResponseHeaders(
                status,
                bytes.length
        );


        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(bytes);

        }
    }
}