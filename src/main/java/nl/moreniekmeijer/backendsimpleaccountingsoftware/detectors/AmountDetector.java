package nl.moreniekmeijer.backendsimpleaccountingsoftware.detectors;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmountDetector {

    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(?i)(totaal|total|amount).*?([\\d,.]+)");

    public BigDecimal detectTotalAmount(String[] lines) {
        for (String line : lines) {
            Matcher matcher = AMOUNT_PATTERN.matcher(line);
            if (matcher.find()) {
                return parseDecimal(matcher.group(2));
            }
        }

        // fallback: grootste bedrag
        BigDecimal max = BigDecimal.ZERO;
        Pattern fallbackPattern = Pattern.compile("([\\d]+[,.][\\d]{2})");
        for (String line : lines) {
            Matcher matcher = fallbackPattern.matcher(line);
            while (matcher.find()) {
                BigDecimal current = parseDecimal(matcher.group(1));
                if (current.compareTo(max) > 0) {
                    max = current;
                }
            }
        }

        return max.compareTo(BigDecimal.ZERO) > 0 ? max : null;
    }

    private BigDecimal parseDecimal(String str) {
        str = str.replace(",", ".").replaceAll("[^\\d.]", "");
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
