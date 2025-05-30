import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.*;

public class Analysis {

    public static double totalRevenue(List<Payment> payments) {
        return payments.stream()
                       .filter(p -> p.getStatus().equalsIgnoreCase("paid"))
                       .mapToDouble(Payment::getAmount)
                       .sum();
    }

    public static Map<String,Double> revenueByStatus(List<Payment> payments) {
        return payments.stream()
                .collect(Collectors.groupingBy(
                   Payment::getStatus,
                   Collectors.summingDouble(Payment::getAmount)
                ));
    }

    public static double averageRating(List<Sale> sales) {
        return sales.stream()
                    .mapToInt(s -> {
                        String rating = s.getRating();
                        try {
                            return Integer.parseInt(rating.split(" ")[0]);
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            return 0;
                        }
                    })
                    .average()
                    .orElse(0.0);
    }

    public static Map<Integer,Long> ratingDistribution(List<Sale> sales) {
        return sales.stream()
                    .collect(Collectors.groupingBy(
                        s -> {
                            String rating = s.getRating();
                            try {
                                return Integer.parseInt(rating.split(" ")[0]);
                            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                return 0;
                            }
                        },
                        Collectors.counting()
                    ));
    }

    public static List<Sale> lowRatedSales(List<Sale> sales, int threshold) {
        return sales.stream()
                    .filter(s -> {
                        String rating = s.getRating();
                        try {
                            return Integer.parseInt(rating.split(" ")[0]) <= threshold;
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
    }

    public static Map<String, Long> topSellingBrands(List<Sale> sales, int topN) {
        Map<String, Long> brandCounts = new HashMap<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader("sales.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    String carID = parts[3];
                    try {
                        Car car = Car.searchCar("cars.txt", carID);
                        if (car != null) {
                            String brand = car.getCarBrand();
                            brandCounts.merge(brand, 1L, Long::sum);
                        }
                    } catch (IOException ex) {
                        System.err.println("Error looking up car " + carID + ": " + ex.getMessage());
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return brandCounts;
        }

        return brandCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(topN)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }

    public static Map<LocalDate, Double> dailyRevenue(List<Payment> payments) {
        return payments.stream()
            .filter(p -> p.getStatus().equalsIgnoreCase("paid"))
            .collect(Collectors.groupingBy(
                p -> p.getTimestamp().toLocalDate(),
                TreeMap::new,
                Collectors.summingDouble(Payment::getAmount)
            ));
    }

    public static double todayRevenue(List<Payment> payments) {
        LocalDate today = LocalDate.now();
        return payments.stream()
            .filter(p -> p.getStatus().equalsIgnoreCase("paid"))
            .filter(p -> p.getTimestamp().toLocalDate().equals(today))
            .mapToDouble(Payment::getAmount)
            .sum();
    }

    public static double revenueOnDate(List<Payment> payments, LocalDate date) {
        return payments.stream()
            .filter(p -> p.getStatus().equalsIgnoreCase("paid"))
            .filter(p -> p.getTimestamp().toLocalDate().equals(date))
            .mapToDouble(Payment::getAmount)
            .sum();
    }
}
