package com.quangduy.productservice.Presentation.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO <T>{
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private boolean last;
    private int size;
    private int number;
    private boolean first;
    private int numberOfElements;
    private boolean empty;
}
