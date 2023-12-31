package cz.jansimerda.homebrewdash.exception.exposed;

public class EntityNotFoundException extends ExposedException {
    public EntityNotFoundException() {
        super("Entity not found");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public <ID> EntityNotFoundException(ID id) {
        super("Entity with ID " + id + " was not found");
    }

    public <ID> EntityNotFoundException(String entityName, ID id) {
        super("Entity " + entityName + " with ID " + id + " was not found");
    }

    public <E, ID> EntityNotFoundException(Class<E> entityName, ID id) {
        this(entityName.toString(), id);
    }

    @Override
    public ExposedExceptionTypeEnum getType() {
        return ExposedExceptionTypeEnum.ENTITY_NOT_FOUND;
    }
}
