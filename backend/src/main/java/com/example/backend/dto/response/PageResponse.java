package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> list;
    private Pagination pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Pagination {
        private int page;
        private int size;
        private long total;
        private int totalPages;
    }

    public static <T> PageResponse<T> of(List<T> list, int page, int size, long total, int totalPages) {
        return PageResponse.<T>builder()
                .list(list)
                .pagination(Pagination.builder()
                        .page(page)
                        .size(size)
                        .total(total)
                        .totalPages(totalPages)
                        .build())
                .build();
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .list(page.getContent())
                .pagination(Pagination.builder()
                        .page(page.getNumber() + 1)
                        .size(page.getSize())
                        .total(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build())
                .build();
    }

    public static <T, R> PageResponse<R> of(Page<T> page, Function<T, R> mapper) {
        List<R> list = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());
        return PageResponse.<R>builder()
                .list(list)
                .pagination(Pagination.builder()
                        .page(page.getNumber() + 1)
                        .size(page.getSize())
                        .total(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .build())
                .build();
    }
}
