package cz.jansimerda.homebrewdash.model;

import java.time.LocalDateTime;

public interface CreationAware {
    /**
     * @return date and time of creation
     */
    LocalDateTime getCreatedAt();

    /**
     * @param date date and time of creation
     */
    void setCreatedAt(LocalDateTime date);
}
