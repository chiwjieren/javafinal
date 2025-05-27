import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Car {
    private String carID;
    private String carModel;
    private int carPrice;
    private String carType;
    private String carBrand;
    private String carCategory;
    private String status;

    public Car(String carID, String carModel, int carPrice, String carType, String carBrand, String carCategory, String status) {
        this.carID = carID;
        this.carModel = carModel;
        this.carPrice = carPrice;
        this.carType = carType;
        this.carBrand = carBrand;
        this.carCategory = carCategory;
        this.status = status;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        if (!cleanInput(carID)) throw new IllegalArgumentException("Invalid ID");
        this.carID = carID;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        if (!cleanInput(carModel)) throw new IllegalArgumentException("Invalid model");
        this.carModel = carModel;
    }

    public int getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(int carPrice) {
        if (carPrice < 0) throw new IllegalArgumentException("Price must be non-negative");
        this.carPrice = carPrice;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        if (!cleanInput(carType)) throw new IllegalArgumentException("Invalid type");
        this.carType = carType;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        if (!cleanInput(carBrand)) throw new IllegalArgumentException("Invalid brand");
        this.carBrand = carBrand;
    }

    public String getCarCategory() {
        return carCategory;
    }

    public void setCarCategory(String carCategory) {
        if (!cleanInput(carCategory)) throw new IllegalArgumentException("Invalid category");
        this.carCategory = carCategory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!cleanInput(status)) throw new IllegalArgumentException("Invalid status");
        this.status = status;
    }

    public static String getNextCarID(String filename) throws IOException {
        File file = new File(filename);

        if (!file.exists() || file.length() == 0) return "CAR001";

        String lastLine = "";

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lastLine = line;
                }
            }
        }

        String[] line = lastLine.split(",", 7);
        String lastID = line[0];
        
        int num = Integer.parseInt(lastID.substring(3));

        return String.format("CAR%03d", num + 1);
    }

    public static boolean cleanInput(String input) {
        if (input.trim().isEmpty()) return false;
        return !(input.contains(",") || input.contains("\"") || input.contains("\n") || input.contains("\r"));
    }


    public static Car searchCar(String filename, String id) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", 7);
                if (parts.length < 6) continue;
                if (parts[0].equals(id)) {
                    int price = Integer.parseInt(parts[2]);
                    return new Car(parts[0], parts[1], price,
                                   parts[3], parts[4], parts[5], parts[6]);
                }
            }
        }
        return null;
    }

    public static void addCar(String filename, String id, String model, int price, String type, String brand, String category) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, true))) {
            out.println();
            out.println(String.join(",", id, model, Integer.toString(price), type, brand, category, "Available"));
        }
    }

    public static boolean delete(String filename, String idToDelete) throws IOException {
        ArrayList<String> keptLines = new ArrayList<>();
        boolean deleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {

            if (!cleanInput(idToDelete)) {
                throw new IllegalArgumentException("Invalid Input!");
            }   

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 7);
                String id = parts[0];

                if (id.equals(idToDelete)) {
                    deleted = true;    
                } 
                
                else {
                    keptLines.add(line);
                }
            }
        }

        if (deleted) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename, false))) {
                for (String kept : keptLines) {
                    writer.println(kept);
                }
            }
        }

        return deleted;
    }

    public static boolean update(String filename, String idToUpdate, String newCarModel, int newCarPrice, String newCarType, String newCarBrand, String newCarCategory, String status) throws IOException {
        File inputFile = new File(filename);
        if (!inputFile.exists()) return false;

        File tempFile  = new File(inputFile.getParent(), "cars.tmp");

        boolean updated = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    writer.write(line);
                    writer.newLine();
                    continue;
                }

                String[] fields = line.split(",", 7);
                if (fields.length < 7) {
                    writer.write(line);
                }

                else if (fields[0].equals(idToUpdate)) {
                    if (!cleanInput(idToUpdate) || !cleanInput(newCarModel) || !cleanInput(newCarType) || !cleanInput(newCarBrand) || !cleanInput(newCarCategory)) {
                        throw new IllegalArgumentException("Invalid Input!");
                    }

                    String newLine = String.join(",", idToUpdate, newCarModel, newCarType, newCarBrand, newCarCategory, status);
                    writer.write(newLine);
                    updated = true;
                } 
                
                else {
                    writer.write(line);
                }

                writer.newLine();
            }
        }

        if (updated) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                throw new IOException("Could not replace original file.");
            }
        } 
        
        else {
            tempFile.delete();
        }

        return updated;
    }   
}
