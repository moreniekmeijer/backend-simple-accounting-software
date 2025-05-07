package nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceInputDto {
    private String invoiceNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate invoiceDate;
    @NotNull
    private Long clientId;
    @NotEmpty
    private List<InvoiceLineDto> lines;
}

