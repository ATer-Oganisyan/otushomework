import java.io.IOException;
import java.io.OutputStream;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.sql.*;

public class OtusHttpCrudServer {

    static Connection connection = null;

    public static void main(String[] args) throws Exception {
        String host = args[0];
        String port = args[1];
        String user = args[2];
        String password = args[3];
        String db = args[4];
        System.out.println("Started");
        System.out.println(host);
        System.out.println(port);
        System.out.println(user);
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
	    Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":"+port + "/"+ db, user, password);
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("Request accepted");
            String path = t.getRequestURI().getPath();
            System.out.println("Path: " + path);
            if ("/health".equals(path)) {
                routeHealth(t);
                System.out.println("matched");
            } else if ("/user".equals(path)) {
                routeRead(t);
                System.out.println("matched");
            } else if ("/user/update".equals(path)) {
                routeUpdate(t);
                System.out.println("matched");
            } else if ("/users".equals(path)) {
                routeReadAll(t);
                System.out.println("matched");
            } else if ("/user/delete".equals(path)) {
                routeDelete(t);
                System.out.println("matched");
            } else if ("/user/create".equals(path)) {
                routeCreate(t);
                System.out.println("matched");
            } else {
                    String response = "{\"status\": \"not found\"}";
                    t.sendResponseHeaders(404, response.length());
                    OutputStream os = t.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                    System.out.println("not matched");
                }
            }
    }


    static private void routeHealth(HttpExchange t) throws IOException {
        System.out.println("Request accepted");
        String response = "{\"status\": \"OK\"}";
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    static private void routeRead(HttpExchange t) throws IOException {
        System.out.println("Read request accepted");
        Map<String, String> q = queryToMap(t.getRequestURI().getQuery());
        String qId = q.get("id");
        String r;
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("select * from user where id = " + qId);
            System.out.println("request to database");
            if (rs.next()) {
                System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
                String id = "" + rs.getInt(1);
                String name = rs.getString(2);
                String age = "" + rs.getInt(3);
                r = "{id: " + id + ", name: " + name + ", age: " + age + " }";
            } else {
                r = "{}";
            }
            System.out.println("send headers");
            t.sendResponseHeaders(200, r.length());
            System.out.println("success");
        } catch (Throwable e) {
            System.out.println("error: " + e.getMessage());
            r = "internal server error";
            t.sendResponseHeaders(500, r.length());
        }
        OutputStream os = t.getResponseBody();
        os.write(r.getBytes());
        os.close();
    }

    static private void routeDelete(HttpExchange t) throws IOException {
        System.out.println("Read request accepted");
        Map<String, String> q = queryToMap(t.getRequestURI().getQuery());
        String qId = q.get("id");
        String r;
        try {
            Statement stmt=connection.createStatement();
            stmt.executeUpdate("delete from user where id = " + qId);
            System.out.println("request to database");
            r = "";
            System.out.println("send headers");
            t.sendResponseHeaders(200, r.length());
            System.out.println("success");
        } catch (Throwable e) {
            System.out.println("error: " + e.getMessage());
            r = "internal server error";
            t.sendResponseHeaders(500, r.length());
        }
        OutputStream os = t.getResponseBody();
        os.write(r.getBytes());
        os.close();
    }

    static private void routeUpdate(HttpExchange t) throws IOException {
        System.out.println("Read request accepted");
        Map<String, String> q = postToMap(buf(t.getRequestBody()));
        String name = q.get("name");
        String uId = q.get("id");
        String age = q.get("age");
        String r;
        try {
            Statement stmt=connection.createStatement();
            String sql = "update user set name = \"" + name + "\", age=" + age + " where id = " + uId;
            System.out.println("request to database: " + sql);
            stmt.executeUpdate(sql);
            r = "";
            System.out.println("send headers");
            t.sendResponseHeaders(200, r.length());
            System.out.println("success");
        } catch (Throwable e) {
            System.out.println("error: " + e.getMessage());
            r = "internal server error";
            t.sendResponseHeaders(500, r.length());
        }
        OutputStream os = t.getResponseBody();
        os.write(r.getBytes());
        os.close();
    }

    static private void routeCreate(HttpExchange t) throws IOException {
        System.out.println("Read request accepted");
        Map<String, String> q = postToMap(buf(t.getRequestBody()));
        String name = q.get("name");
        String age = q.get("age");
        String r;
        try {
            Statement stmt=connection.createStatement();
            String sql = "insert into user (name, age) values (\"" + name + "\", " + age + ")";
            System.out.println("request to database: " + sql);
            stmt.executeUpdate(sql);
            r = "";
            System.out.println("send headers");
            t.sendResponseHeaders(200, r.length());
            System.out.println("success");
        } catch (Throwable e) {
            System.out.println("error: " + e.getMessage());
            r = "internal server error";
            t.sendResponseHeaders(500, r.length());
        }
        OutputStream os = t.getResponseBody();
        os.write(r.getBytes());
        os.close();
    }

    static private void routeReadAll(HttpExchange t) throws IOException {
        System.out.println("Read request accepted");
        Map<String, String> q = queryToMap(t.getRequestURI().getQuery());
        String qId = q.get("id");
        String r;
        try {
            Statement stmt=connection.createStatement();
            ResultSet rs=stmt.executeQuery("select * from user");
            System.out.println("request to database");
            List<String> items = new ArrayList<>();
            while (rs.next()) {
                System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));
                String id = "" + rs.getInt(1);
                String name = rs.getString(2);
                String age = "" + rs.getInt(3);
                items.add("{id: " + id + ", name: " + name + ", age: " + age + " }");
            }
            String resp = "{" + String.join(",", items) + "}";
            System.out.println("send headers");
            t.sendResponseHeaders(200, resp.length());
            System.out.println("success");
            OutputStream os = t.getResponseBody();
            os.write(resp.getBytes());
            os.close();
        } catch (Throwable e) {
            System.out.println("error: " + e.getMessage());
            r = "internal server error";
            t.sendResponseHeaders(500, r.length());
            OutputStream os = t.getResponseBody();
            os.write(r.getBytes());
            os.close();
        }
    }

    static private Map<String, String> queryToMap(String query) {
        if(query == null) {
            return new HashMap<>();
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            }else{
                result.put(entry[0], "");
            }
        }
        return result;
    }

    static private Map<String, String> postToMap(StringBuilder body){
        String[] parts = body
                .toString()
                .replaceAll("\r", "")
                .split("\n");
        Map<String, String> result = new HashMap<>();
        for (String part: parts) {
            String[] keyVal = part.split(":");
            result.put(keyVal[0], keyVal[1]);
        }
        System.out.println("buf: " + result.toString());
        return result;
    }

    static private StringBuilder buf(InputStream inp)  throws UnsupportedEncodingException, IOException {
        InputStreamReader isr =  new InputStreamReader(inp,"utf-8");
        BufferedReader br = new BufferedReader(isr);
        int b;
        StringBuilder buf = new StringBuilder(512);
        while ((b = br.read()) != -1) {
            buf.append((char) b);
        }
        br.close();
        isr.close();
        System.out.println("buf : " + buf);
        return buf;
    }
}