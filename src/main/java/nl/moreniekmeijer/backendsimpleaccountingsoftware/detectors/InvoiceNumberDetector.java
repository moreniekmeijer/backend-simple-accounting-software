package nl.moreniekmeijer.backendsimpleaccountingsoftware.detectors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InvoiceNumberDetector {

    private static final Pattern[] PATTERNS = new Pattern[] {
            Pattern.compile("(?i)factuurnummer[:\\s]*([A-Z0-9-]+)"),
            Pattern.compile("(?i)invoice no.?[:\\s]*([A-Z0-9-]+)"),
            Pattern.compile("(?i)factuur[:\\s#]*([A-Z0-9-]{4,})")
    };

    public String detectInvoiceNumber(String[] lines) {
        for (String line : lines) {
            for (Pattern pattern : PATTERNS) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
        }
        return null;
    }
}
