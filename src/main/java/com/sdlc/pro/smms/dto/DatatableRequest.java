package com.sdlc.pro.smms.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class DatatableRequest {
    private int draw;
    private int start;
    private int length;
    private Search search;
    private List<Order> order;
    private List<Column> columns;

    @Setter
    @Getter
    public static class Search {
        private String value;
    }

    @Setter
    @Getter
    public static class Order {
        private int column;
        private String dir;
    }

    @Setter
    @Getter
    public static class Column {
        private String data;
    }

    public String searchValue() {
        return this.getSearch().getValue();
    }

    public Pageable toPageable() {
        return this.toPageable(null);
    }

    public Pageable toPageable(Map<String, String> fieldToDbColumnMap) {
        int page = this.getStart() / this.getLength();
        int size = this.getLength();

        if (this.getOrder() == null || this.getOrder().isEmpty()) {
            return PageRequest.of(page, size);
        }

        DatatableRequest.Order order = this.getOrder().get(0);
        String columnName = this.getColumns().get(order.getColumn()).getData();

        String dbColumnName = fieldToDbColumnMap != null ?
                fieldToDbColumnMap.getOrDefault(columnName, columnName) : columnName;

        Sort sort = order.getDir().equalsIgnoreCase("asc")
                ? Sort.by(dbColumnName).ascending()
                : Sort.by(dbColumnName).descending();

        return PageRequest.of(page, size, sort);
    }

}
