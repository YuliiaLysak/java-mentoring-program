package edu.lysak.kafkastreams.alertsAndTresholds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Alert {

    private Timestamp timestamp;
    private String level;
    private String code;
    private String msg;

}
