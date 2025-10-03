package es.uib.lotery.entity;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {
    private String number;
    private int sorteo;
}
