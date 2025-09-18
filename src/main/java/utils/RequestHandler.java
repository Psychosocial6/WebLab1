package utils;

import com.fastcgi.FCGIRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RequestHandler {
    private static final FilePrinter filePrinter = new FilePrinter(Path.of("logs/logs.txt"));
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Instant startTime;
    private static Instant endTime;

    private static final String RESPONSE_CREATED = """
                            Status: 201 Created
                            Content-Type: application/json
                            Content-Length: %d
                            
                            %s
                            
                            """;
    private static final String RESPONSE_BAD_REQUEST = """
                            Status: 400 Bad Request
                            Content-Type: application/json
                            Content-Length: %d
                            
                            %s
                            
                            """;
    private static final String RESPONSE_METHOD_NOT_ALLOWED = """
            Status: 405 Method Not Allowed
            Allow: POST
            """;

    static {
        objectMapper.registerModule(new JavaTimeModule());
    };

    public static void handleRequest(FCGIRequest request) throws IOException {
        String method = request.params.getProperty("REQUEST_METHOD");

        if (method.equals("POST")) {
            filePrinter.getPrintWriter().println("processing POST request");
            startTime = Instant.now();
            String requestBody = getBody(request);
            RequestData requestData = objectMapper.readValue(requestBody, RequestData.class);
            float x = requestData.getX();
            BigDecimal y = requestData.getY();
            float r = requestData.getR();

            if (Validator.validateData(x, y, r)) {
                filePrinter.getPrintWriter().println("request data validated successfully");
                boolean result = AreaHitChecker.checkHit(x, y, r);
                endTime = Instant.now();
                sendResponseCreated(new ResponseData(startTime.until(endTime, ChronoUnit.MILLIS), LocalDateTime.now(), result, "OK"));
            }
            else {
                filePrinter.getPrintWriter().println("invalid request data");
                endTime = Instant.now();
                sendResponseBadRequest(new ResponseData(startTime.until(endTime, ChronoUnit.MILLIS), LocalDateTime.now(), false, "Invalid coordinates values"));
            }

        }
        else {
            sendResponseMethodNotAllowed();
        }

    }

    private static String getBody(FCGIRequest request) throws IOException {
        int contentLength = Integer.parseInt(request.params.getProperty("CONTENT_LENGTH"));
        byte[] body = request.inStream.readNBytes(contentLength);
        return  new String(body, StandardCharsets.UTF_8);
    }

    private static void sendResponseCreated(ResponseData responseData) throws JsonProcessingException {
        String responseBody = objectMapper.writeValueAsString(responseData);
        System.out.println(String.format(RESPONSE_CREATED, responseBody.getBytes(StandardCharsets.UTF_8).length, responseBody));
    }

    private static void sendResponseBadRequest(ResponseData responseData) throws JsonProcessingException {
        String responseBody = objectMapper.writeValueAsString(responseData);
        System.out.println(String.format(RESPONSE_BAD_REQUEST, responseBody.getBytes(StandardCharsets.UTF_8).length, responseBody));
    }

    private static void sendResponseMethodNotAllowed() {
        System.out.println(RESPONSE_METHOD_NOT_ALLOWED);
    }

}
