package com.github.dio.mensageria.exceptionhandler;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Classe model para representação do {@link org.springframework.beans.factory.parsing.Problem}
 * @hidden
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
