import com.fastcgi.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

public class Server {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        System.setProperty("FCGI_PORT", "9000");
        FCGIInterface fcgi = new FCGIInterface();
        objectMapper.registerModule(new JavaTimeModule());

        while (fcgi.FCGIaccept() >= 0) {
            String method = FCGIInterface.request.params.getProperty("REQUEST_METHOD");
            FCGIRequest request = FCGIInterface.request;

            try {
                if (method.equals("POST")) {
                    Instant beginning = Instant.now();
                    int len = Integer.parseInt(request.params.getProperty("CONTENT_LENGTH"));
                    byte[] body = new byte[len];
                    request.inStream.read(body);
                    String requestBody = new String(body, StandardCharsets.UTF_8);

                    RequestData requestData = null;
                    try {
                        requestData = objectMapper.readValue(requestBody, RequestData.class);
                    } catch (JsonProcessingException e) {
                        sendBadRequest(request);
                        continue;
                    }
                    boolean result = checkShot(requestData.getX(), requestData.getY(), requestData.getR());
                    Instant end = Instant.now();
                    ResponseData responseData = new ResponseData(Duration.between(beginning, end).toMillis(), LocalDateTime.now(), result);
                    String responseBody = objectMapper.writeValueAsString(responseData);
                    String response = """
                            HTTP/1.1 200 OK
                            Content-Type: application/json
                            Content-Length: %d
                            
                            %s""";
                    request.outStream.write(String.format(response, responseBody.getBytes().length, responseBody).getBytes(StandardCharsets.UTF_8));
                }
                else {
                    sendBadRequest(request);
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            finally {
                request.outStream.flush();
                //request.outStream.close();
                //request.inStream.close();
            }
        }
    }

    private static void sendBadRequest(FCGIRequest request) throws IOException {
        String response = """
                HTTP/1.1 400 Bad Request
                Content-Length: 0
                
                """;
        request.outStream.write(response.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean checkShot(float x, float y, float r) {
        if (x >= 0 && y >= 0) {
            return x * x + y * y <= r * r / 4;
        }
        else if (x <= 0 && y >= 0) {
            return (x >= -1 * r && y <= r);
        }
        else if (x <= 0 && y <= 0) {
            return y >= -0.5 * x - r;
        }
        else {
            return false;
        }
    }
}
