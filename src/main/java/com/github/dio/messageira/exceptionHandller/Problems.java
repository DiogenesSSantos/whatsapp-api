package com.github.dio.messageira.exceptionHandller;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Problems {

    private Long status;
    private String type;
    private String Title;
    private String details;


    private LocalDateTime date;
    private String userMassage;

}
