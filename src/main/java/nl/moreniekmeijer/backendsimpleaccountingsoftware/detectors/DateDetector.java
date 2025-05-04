package nl.moreniekmeijer.backendsimpleaccountingsoftware.detectors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.*;

public class DateDetector {

    private static final Pattern[] DATE_PATTERNS = new Pattern[] {
            Pattern.compile("(\\d{2}-\\d{2}-\\d{4})"),
            Pattern.compile("(\\d{4}-\\d{2}-\\d{2})"),
            Pattern.compile("(\\d{2}/\\d{2}/\\d{4})")
    };

    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    };

    public LocalDate detectDate(String[] lines) {
        for (String line : lines) {
            for (int i = 0; i < DATE_PATTERNS.length; i++) {
                Matcher matcher = DATE_PATTERNS[i].matcher(line);
                if (matcher.find()) {
                    try {
                        return LocalDate.parse(matcher.group(1), FORMATTERS[i]);
                    } catch (DateTimeParseException ignored) {}
                }
            }
        }
        return null;
    }
}
