package nl.moreniekmeijer.backendsimpleaccountingsoftware.detectors;

import java.util.List;

public class VendorDetector {

    private static final List<String> KNOWN_VENDORS = List.of(
            "Thomann",
            "Musicstore",
            "Bax Music",
            "Makro",
            "Coolblue",
            "Bol.com",
            "Amazon",
            "IKEA",
            "MediaMarkt",
            "Kamera Express",
            "Conrad",
            "Reverb",
            "AliExpress",
            "Gear4music",
            "Just Music",
            "T-Systems",
            "Marktplaats",
            "Ebay",
            "Apple",
            "Google",
            "Microsoft"
    );

    public String detectVendor(String[] lines) {
        for (String line : lines) {
            for (String knownVendor : KNOWN_VENDORS) {
                if (line.toLowerCase().contains(knownVendor.toLowerCase())) {
                    return knownVendor;
                }
            }
        }

        // fallback: lijn met hoofdletters en zonder cijfers
        for (String line : lines) {
            if (!line.matches(".*\\d.*") && line.trim().length() > 3 && line.equals(line.toUpperCase())) {
                return line.trim();
            }
        }

        return lines.length > 0 ? lines[0].trim() : "Onbekend";
    }
}
