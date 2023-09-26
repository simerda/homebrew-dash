package cz.jansimerda.homebrewdash.model;

/**
 * Common supertype for domain types.
 *
 * @param <ID> primary key type
 */
public interface DomainEntity<ID> {
    /**
     * ID setter
     * @param id the primary key value of this instance
     */
    void setId(ID id);

    /**
     * ID getter
     * @return the primary key value of this instance
     */
    ID getId();
}
