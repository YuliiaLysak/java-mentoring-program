package edu.lysak.sport;

import edu.lysak.sport.domain.Sport;
import edu.lysak.sport.domain.dto.SportAttributes;
import edu.lysak.sport.domain.dto.SportDto;

public final class TestUtil {
    private TestUtil() {
    }

    public static Sport getSport(Long sportId, int decathlonId, String name, String description, String slug, String icon) {
        return Sport.builder()
                .sportId(sportId)
                .decathlonId(decathlonId)
                .name(name)
                .description(description)
                .slug(slug)
                .icon(icon)
                .build();
    }

    public static SportDto getSportDto(long id, String name, String description, String slug, String icon) {
        return SportDto.builder()
                .id(id)
                .attributes(SportAttributes.builder()
                        .name(name)
                        .description(description)
                        .slug(slug)
                        .icon(icon)
                        .build()
                )
                .build();
    }
}
