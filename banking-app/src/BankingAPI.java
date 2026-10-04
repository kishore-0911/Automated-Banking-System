import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BankingAPI {
    private BankingService bankingService;
    private Gson gson;

    public static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
            } else {
                out.value(value.format(formatter));
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == null) {
                in.nextNull();
                return null;
            } else {
                return LocalDateTime.parse(in.nextString(), formatter);
            }
        }
    }

    public BankingAPI() {
        this.bankingService = new BankingService();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/api/accounts", new AccountHandler());
        server.createContext("/api/deposit", new DepositHandler());
        server.createContext("/api/withdraw", new WithdrawHandler());
        server.createContext("/api/transfer", new TransferHandler());
        server.createContext("/api/balance", new BalanceHandler());
        server.createContext("/api/transactions", new TransactionHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Banking API Server started on port 8080");
    }

    private class AccountHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                setCORSHeaders(exchange);
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 204, "");
                    return;
                }

                if ("POST".equals(exchange.getRequestMethod())) {
                    String requestBody = getRequestBody(exchange);
                    Map<String, Object> data = gson.fromJson(requestBody, Map.class);

                    String accountNumber = bankingService.createAccount(
                            (String) data.get("name"),
                            (String) data.get("email"),
                            new BigDecimal(data.get("initialDeposit").toString()),
                            (String) data.get("accountType"));

                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("accountNumber", accountNumber != null ? accountNumber : "");
                    String response = gson.toJson(responseMap);
                    sendResponse(exchange, 200, response);
                } else if ("GET".equals(exchange.getRequestMethod())) {
                    String response = gson.toJson(bankingService.getAllAccounts());
                    sendResponse(exchange, 200, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    private class DepositHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                setCORSHeaders(exchange);
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 204, "");
                    return;
                }
                if ("POST".equals(exchange.getRequestMethod())) {
                    String requestBody = getRequestBody(exchange);
                    Map<String, Object> data = gson.fromJson(requestBody, Map.class);

                    boolean success = bankingService.deposit(
                            (String) data.get("accountNumber"),
                            new BigDecimal(data.get("amount").toString()),
                            (String) data.get("description"));

                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("success", success);
                    String response = gson.toJson(responseMap);
                    sendResponse(exchange, 200, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    private class WithdrawHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                setCORSHeaders(exchange);
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 204, "");
                    return;
                }
                if ("POST".equals(exchange.getRequestMethod())) {
                    String requestBody = getRequestBody(exchange);
                    Map<String, Object> data = gson.fromJson(requestBody, Map.class);

                    boolean success = bankingService.withdraw(
                            (String) data.get("accountNumber"),
                            new BigDecimal(data.get("amount").toString()),
                            (String) data.get("description"));

                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("success", success);
                    String response = gson.toJson(responseMap);
                    sendResponse(exchange, 200, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    private class TransferHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                setCORSHeaders(exchange);
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 204, "");
                    return;
                }
                if ("POST".equals(exchange.getRequestMethod())) {
                    String requestBody = getRequestBody(exchange);
                    Map<String, Object> data = gson.fromJson(requestBody, Map.class);

                    boolean success = bankingService.transfer(
                            (String) data.get("fromAccount"),
                            (String) data.get("toAccount"),
                            new BigDecimal(data.get("amount").toString()),
                            (String) data.get("description"));

                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("success", success);
                    String response = gson.toJson(responseMap);
                    sendResponse(exchange, 200, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    private class BalanceHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                setCORSHeaders(exchange);
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 204, "");
                    return;
                }
                if ("GET".equals(exchange.getRequestMethod())) {
                    String query = exchange.getRequestURI().getQuery();
                    String accountNumber = query.split("=")[1];

                    BigDecimal balance = bankingService.getBalance(accountNumber);
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("balance", balance);
                    String response = gson.toJson(responseMap);
                    sendResponse(exchange, 200, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    private class TransactionHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                setCORSHeaders(exchange);
                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 204, "");
                    return;
                }
                if ("GET".equals(exchange.getRequestMethod())) {
                    String query = exchange.getRequestURI().getQuery();
                    String accountNumber = query.split("=")[1];

                    String response = gson.toJson(bankingService.getTransactionHistory(accountNumber));
                    sendResponse(exchange, 200, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, e.getMessage());
            }
        }
    }

    private void setCORSHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private String getRequestBody(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) {
        try {
            Map<String, String> errMap = new HashMap<>();
            errMap.put("error", message != null ? message : "Internal Server Error");
            String response = gson.toJson(errMap);
            sendResponse(exchange, statusCode, response);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new BankingAPI().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
