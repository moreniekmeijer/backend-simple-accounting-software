package nl.moreniekmeijer.backendsimpleaccountingsoftware.detectors;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VatDetector {

    private static final Pattern VAT_PATTERN = Pattern.compile("(?i)(btw|vat)[^\\d]*(\\d+[.,]\\d{2})");

    public BigDecimal detectVat(String[] lines) {
        for (String line : lines) {
            Matcher matcher = VAT_PATTERN.matcher(line);
            if (matcher.find()) {
                return parseDecimal(matcher.group(2));
            }
        }
        return null;
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
