package es.daw.pixayapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SavedImageSummaryDto {

    private Long id;
    private String title;
    private String category;
    private String subcategory;
    private String autorUsername;

}
