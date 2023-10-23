package cz.jansimerda.homebrewdash.model;

import java.time.LocalDateTime;

public interface CreationAware {
    /**
     * @return date and time of creation
     */
    public LocalDateTime getCreatedAt();

    /**
     * @param date date and time of creation
     */
    public void setCreatedAt(LocalDateTime date);
}
