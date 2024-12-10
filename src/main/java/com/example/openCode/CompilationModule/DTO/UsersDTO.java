package com.example.openCode.CompilationModule.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
public class UsersDTO {

    //todo: refactor dto classes
    private int id;
    private String username;

    List<Long> userSolutionsId;
    List<Long> taskListId;
}
