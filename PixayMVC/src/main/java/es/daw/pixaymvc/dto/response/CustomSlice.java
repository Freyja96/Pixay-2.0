package es.daw.pixaymvc.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class CustomSlice<T> {
    private List<T> content;
    private int number;
    private int size;
    private boolean last;
    private boolean first;
    private boolean hasNext;
}