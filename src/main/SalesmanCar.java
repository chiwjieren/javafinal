import java.io.*;
import java.util.*;

// class to handle car inventory and details
public class SalesmanCar {
    private String carID, model, type, brand, category;
    private double price;

    // create a new car record
    public SalesmanCar(String carID, String model, double price, String type, String brand, String category) {
        this.carID = carID;
        this.model = model;
        this.price = price;
        this.type = type;
        this.brand = brand;
        this.category = category;
    }

    // getters
    public String getCarID() { return carID; }
    public String getModel() { return model; }
    public double getPrice() { return price; }
    public String getType() { return type; }
    public String getBrand() { return brand; }
    public String getCategory() { return category; }

    // get formatted car info for display
    public String getCarInfo() {
        return "Car ID: " + carID +
               "\nModel: " + model +
               "\nPrice: " + price +
               "\nType: " + type +
               "\nBrand: " + brand +
               "\nCategory: " + category;
    }

    // load all cars from file
    public static List<SalesmanCar> loadCarsFromFile(String filename) {
        List<SalesmanCar> cars = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 6) {
                    String carID = parts[0];
                    String model = parts[1];
                    // Extract numeric value from price string (remove "RM")
                    double price = Double.parseDouble(parts[2].substring(2));
                    String type = parts[3];
                    String brand = parts[4];
                    String category = parts[5];
                    cars.add(new SalesmanCar(carID, model, price, type, brand, category));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading cars: " + e.getMessage());
        }
        return cars;
    }

    // save all cars to file
    public static void saveCarsToFile(List<SalesmanCar> cars, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (SalesmanCar car : cars) {
                writer.println(String.join(",", 
                    car.getCarID(), 
                    car.getModel(), 
                    "RM" + (int)car.getPrice(), 
                    car.getType(),
                    car.getBrand(), 
                    car.getCategory()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error saving cars: " + e.getMessage());
        }
    }

    // get next available car ID
    public static String getNextCarID(String filename) throws IOException {
        File file = new File(filename);
        
        // if file is empty or doesn't exist, start with CAR001
        if (!file.exists() || file.length() == 0) return "CAR001";

        // read the last line to get the last ID
        String lastLine = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lastLine = line;
                }
            }
        }

        // extract number from last ID and increment
        String[] parts = lastLine.split(",");
        String lastID = parts[0];
        int num = Integer.parseInt(lastID.substring(3));
        return String.format("CAR%03d", num + 1);
    }
}
