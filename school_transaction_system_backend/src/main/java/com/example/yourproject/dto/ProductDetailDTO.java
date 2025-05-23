package com.example.yourproject.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductDetailDTO extends ProductBasicDTO {
    private String description;
    // Mo?emy tu doda? wi?cej szczegó?ów, je?li s? potrzebne, np.:
    // private String condition; // Stan produktu (np. Nowy, U?ywany)
    // private String location; // Lokalizacja sprzedaj?cego
    // private Integer stock; // Dla produktów, których mo?e by? wi?cej ni? 1 (cho? w za?o?eniu s? to pojedyncze towary)
    private LocalDateTime updatedDate;
}
