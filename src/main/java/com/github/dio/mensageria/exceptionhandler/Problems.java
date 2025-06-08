package com.github.dio.mensageria.exceptionhandler;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * The type Problems.
 */
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
