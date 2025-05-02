package nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String invoiceNumber;

    @NotNull
    private LocalDate invoiceDate;

    @NotNull
    private Long clientId;

    @NotEmpty
    private List<InvoiceLineDto> lines;
}
