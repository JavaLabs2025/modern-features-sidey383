package org.lab.api.dto;

import lombok.Data;

import java.util.Collection;

@Data
public class ListAnswer<T> {
    private final Collection<T> items;
    private final long itemCount;

    public ListAnswer(Collection<T> items) {
        this.items = items;
        this.itemCount = items.size();
    }

}
