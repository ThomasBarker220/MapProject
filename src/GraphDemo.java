import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;
/**
 * Demonstrates the calculation of shortest paths in the US Highway
 * network, showing the functionality of GraphProcessor and using
 * Visualize
 * To do: Add your name(s) as authors
 */
public class GraphDemo {
    public static void main(String[] args) throws Exception {

        GraphProcessor processor = new GraphProcessor();
        Visualize visualize = new Visualize("data/usa.vis", "images/usa.png");
        FileInputStream inputStream = new FileInputStream("data/usa.graph");
        processor.initialize(inputStream);

        try (// Prompt the user to input the names of two cities
        Scanner scanner = new Scanner(System.in).useDelimiter("\r\n")) {
            System.out.println("Enter the first city: ");
            String city1 = scanner.next();
            System.out.println("Enter the second city: ");
            String city2 = scanner.next();
            FileInputStream file = new FileInputStream("data/uscities.csv");
            InputStreamReader fr = new InputStreamReader(file, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            String[] tempArr;
            Double lat1 = 0.0;
            Double long1 = 0.0;
            Double lat2 = 0.0;
            Double long2 = 0.0;
            while((line = br.readLine()) != null) {
                tempArr = line.split(",");
                String city = tempArr[0];
                if(!Character.isLetter(city.charAt(0))) {
                    city = city.substring(1);
                }
                Double latitude = Double.valueOf(tempArr[2]);
                Double longitude = Double.valueOf(tempArr[3]);
                if(city.equals(city1)) {
                    lat1 = latitude;
                    long1 = longitude;
                }
                if(city.equals(city2)) {
                    lat2 = latitude;
                    long2 = longitude;
                    break;
                }                
            }
            br.close();
            // System.out.println("Enter the first city's latitude: ");
            // Double lat1 = Double.valueOf(scanner.nextLine());
            // System.out.println("Enter the first city's longitude: ");
            // Double long1 = Double.valueOf(scanner.nextLine());
            // System.out.println("Enter the second city's latitude: ");
            // Double lat2 = Double.valueOf(scanner.nextLine());
            // System.out.println("Enter the second city's longitude: ");
            // Double long2 = Double.valueOf(scanner.nextLine());

            // try (BufferedReader br = new BufferedReader(new FileReader("data/uscities.csv"))) {
            //     while((line = br.readLine()) != null) {
            //         String[] cityInfo = line.split(splitBy);
            //         String city = cityInfo[0];
            //         Double latitude = Double.valueOf(cityInfo[2]);
            //         Double longitude = Double.valueOf(cityInfo[3]);
            //         if(city.equals(city1)) {
            //             lat1 = latitude;
            //             long1 = longitude;
            //         }
            //         if(city.equals(city2)) {
            //             lat2 = latitude;
            //             long2 = longitude;
            //         }
            //     }
            // }
            long startTime = System.nanoTime();

            Point p1 = new Point(lat1, long1);
            Point p2 = new Point(lat2, long2);

            // Find the closest vertices of the road network to the cities
            Point start = processor.nearestPoint(p1);
            Point end = processor.nearestPoint(p2);

            // Calculate the route (shortest path) between the two closest vertices
            List<Point> route = processor.route(start, end);

            // Calculate the total distance of the route
            double distance = processor.routeDistance(route);
            System.out.println("Distance: " + distance + " miles");
            visualize.drawRoute(route);

            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            System.out.println("Elapsed time: " + elapsedTime + " ns");
        }
        // Measure and report the time it takes to calculate the route
        // Calculate the route and distance
        //Dallas lat, long = 32.7935,-96.7667
        //Phoenix,AZ,33.5722,-112.0892
        //Tampa,FL,27.9945,-82.4447
        //Seattle,WA,47.6211,-122.3244
    }    
}