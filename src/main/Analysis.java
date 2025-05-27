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
                    .mapToInt(Sale::getRating)
                    .average()
                    .orElse(0.0);
    }

    public static Map<Integer,Long> ratingDistribution(List<Sale> sales) {
        return sales.stream()
                    .collect(Collectors.groupingBy(
                       Sale::getRating,
                       Collectors.counting()
                    ));
    }

    public static List<Sale> lowRatedSales(List<Sale> sales, int threshold) {
        return sales.stream()
                    .filter(s -> s.getRating() <= threshold)
                    .collect(Collectors.toList());
    }

    public static Map<String, Long> topSellingBrands(List<Sale> sales, int topN) {
        return sales.stream()
            .collect(Collectors.groupingBy(
                Sale::getCarID,         
                Collectors.counting()
            ))
            .entrySet().stream()
            .collect(Collectors.groupingBy(
                e -> {
                    return sales.stream()
                                .filter(s -> s.getCarID().equals(e.getKey()))
                                .findFirst()
                                .map(Sale::getCarID) 
                                .map(carID -> {
                                    try {
                                        return Car.searchCar("cars.txt", carID).getCarBrand();
                                    } catch (IOException ex) {
                                        return "Unknown";
                                    }
                                })
                                .orElse("Unknown");
                },
                Collectors.summingLong(Map.Entry::getValue)
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String,Long>comparingByValue(Comparator.reverseOrder()))
            .limit(topN)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a,b)->a,
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
