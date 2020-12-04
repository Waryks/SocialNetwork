package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;

import java.util.Arrays;
import java.util.List;


///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlucui solutia propusa cu un Factori (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName=fileName;
        loadData();

    }

    /**Loads data from file
     *
     */
    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while((linie=br.readLine())!=null){
                List<String> attr=Arrays.asList(linie.split(";"));
                E e=extractEntity(attr);
                super.save(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //sau cu lambda - curs 4, sem 4 si 5
//        Path path = Paths.get(fileName);
//        try {
//            List<String> lines = Files.readAllLines(path);
//            lines.forEach(linie -> {
//                E entity=extractEntity(Arrays.asList(linie.split(";")));
//                super.save(entity);
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);
    ///Observatie-Sugestie: in locul metodei template extractEntity, puteti avea un factory pr crearea instantelor entity

    protected abstract String createEntityAsString(E entity);

    /**Saves an entity
     * @param entity
     * @return an entity of type E
     */
    @Override
    public E save(E entity){
        E e=super.save(entity);
        if (e==null)
        {
            writeToFile(entity);
        }
        return e;

    }

    /**Delets an entity
     * @param id an Id of type ID
     * @param l a list of type E
     * @return an entity of type E
     */
    /*@Override
    public E delete(ID id) {
        E entity = entities.get(id);
        System.out.println(entities.get(id));
        if(entity == null){
            throw new ValidationException("The id doesn't exist!");
        }
        entities.remove(id);
        return entity;
    }*/
    @Override
    public E delete(ID id, List<E> l){
        E entity = super.delete(id,l);
        boolean ok=false;
        if(entity != null){
            for (E user : l){
                if(entity != user){
                    if(ok==false ){
                        ok = true;
                        writeToFileD(user);
                    }
                    else
                        writeToFile(user);
                }
                //System.out.println(user);
            }
        };/*
        if(entity != null){
            writeToFileD(entity);
        }*/
        return entity;
    }

    /**Writes to file if there are elements
     * @param entity
     */
    protected void writeToFile(E entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName,true))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**Writes to file if there are no elements
     * @param entity
     */
    protected void writeToFileD(E entity){
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

