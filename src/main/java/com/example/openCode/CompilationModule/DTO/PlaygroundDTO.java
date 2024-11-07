package com.example.openCode.CompilationModule.DTO;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlaygroundDTO {

    public PlaygroundDTO(String programmingLanguage, String code) {
        this.programmingLanguage = programmingLanguage.toUpperCase();
        this.code = code;
    }

    private String programmingLanguage;
    private String code;
}
