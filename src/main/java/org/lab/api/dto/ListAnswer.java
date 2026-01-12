package org.lab.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collection;

@Data
public class ListAnswer<T> {
    private final Collection<T> items;
    private final long itemCount;

    @JsonCreator
    public ListAnswer(
            @JsonProperty("items")
            Collection<T> items,
            @JsonProperty("itemCount")
            long itemCount
    ) {
        this.items = items;
        this.itemCount = itemCount;
    }

    public ListAnswer(Collection<T> items) {
        this.items = items;
        this.itemCount = items.size();
    }

}
