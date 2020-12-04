package socialnetwork.repository.memory;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    Map<ID,E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    /**Finds an entity of id ID
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return
     */
    @Override
    public E findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return entities.get(id);
    }

    /**Finds all entities of type E
     * @return entities of type E
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    /**Saves the entity
     * @param entity entity must be not null
     * @return the saved entity
     */
    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            throw new ValidationException("The id already exists!");
        }
        else entities.put(entity.getId(),entity);
        return null;
    }

    /**Deletes the entity
     * @param id id must be not null
     * @param l list of entities of type E
     * @return the deleted entity
     */
    @Override
    public E delete(ID id, List<E> l) {
        E entity = entities.get(id);
        if(entity == null){
            throw new ValidationException("The id doesn't exist!");
        }
        entities.remove(id);
        return entity;
    }

    /**Updates a entity
     * @param entity entity must not be null
     * @return the updated entity
     */
    @Override
    public E update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;

    }

}
