package edu.lysak.sport.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public Sport(String name) {
        this.name = name;
    }
}
