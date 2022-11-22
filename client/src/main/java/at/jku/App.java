package at.jku;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws IOException, InterruptedException {
        final HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/productionOrdersSorted")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status-Code: " + response.statusCode());
        final JSONArray jsonArray = new JSONArray(response.body());
        if (jsonArray.isEmpty()) {
            System.out.println("You have finished all work! Start cleaning!");
        } else {
            for (int i = 0; i < jsonArray.length(); i++) {
                System.out.println("---------------------------------------------------"
                        + "\nid: " + jsonArray.getJSONObject(i).get("id")
                        + "\ndescription: " + jsonArray.getJSONObject(i).get("description")
                        + "\npriority: " + jsonArray.getJSONObject(i).get("priority")
                        + "\nmachine: " + jsonArray.getJSONObject(i).get("machine"));
            }
            System.out.println("\n\n");

            System.out.println("========== Drill - Machine ==========");
            System.out.println("Enter a production order id:");

            final Scanner in = new Scanner(System.in);
            String line = in.nextLine();
            final int id = Integer.parseInt(line);

            request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/changeMachine?id=" + id + "&machine=drill-machine")).PUT(HttpRequest.BodyPublishers.noBody()).build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status-Code: " + response.statusCode());
            if (response.statusCode() == 200) {
                final JSONObject jsonObject = new JSONObject(response.body());
                System.out.println("id: " + jsonObject.get("id")
                        + "\ndescription: " + jsonObject.get("description")
                        + "\npriority: " + jsonObject.get("priority")
                        + "\nmachine: " + jsonObject.get("machine"));
            } else {
                System.out.println("bad request! id does not exist!");
            }
        }
    }
}
