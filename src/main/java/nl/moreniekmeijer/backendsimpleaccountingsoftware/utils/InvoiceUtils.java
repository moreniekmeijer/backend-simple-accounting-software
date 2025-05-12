package nl.moreniekmeijer.backendsimpleaccountingsoftware.utils;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class InvoiceUtils {

    public static String formatMoney(BigDecimal value) {
        return "€ " + value.setScale(2, RoundingMode.HALF_UP)
                .toString().replace('.', ',');
    }

    public static String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%d:%02d", hours, mins);
    }

    public static PdfPCell makeRightAlignedCell(String text, int colspan, Font font, Color backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setColspan(colspan);
        cell.setBorderColor(new Color(200, 200, 255));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        if (backgroundColor != null) {
            cell.setBackgroundColor(backgroundColor);
        }
        return cell;
    }
}