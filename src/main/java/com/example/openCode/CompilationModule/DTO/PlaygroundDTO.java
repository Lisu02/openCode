package com.example.openCode.CompilationModule.DTO;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlaygroundDTO {

    private String programmingLanguage;
    private String code;
}
