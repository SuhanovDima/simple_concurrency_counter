package suhanov.pattern.example.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Statistics {

    @Builder.Default
    private LocalDate date = LocalDate.now();

    private int clickCount;

    @Builder.Default
    private BigDecimal paymentTotal = BigDecimal.ZERO;
}
