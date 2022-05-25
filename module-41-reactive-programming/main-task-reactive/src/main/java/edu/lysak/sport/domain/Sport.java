package edu.lysak.sport.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("sport")
public class Sport {

    @Id
    @Column("sport_id")
    private Long sportId;

    @Column("decathlon_id")
    private int decathlonId;

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("slug")
    private String slug;

    @Column("icon")
    private String icon;

}
