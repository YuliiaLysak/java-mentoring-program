package edu.lysak.kafkastreams.websiteViews;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class WebsiteView {
    private Timestamp timestamp;
    private String user;
    private String topic;
    private Integer minutes;
}
