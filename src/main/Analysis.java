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
}
