package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.Collection;
import sk.stuba.sdg.isbe.domain.model.Module;

import sk.stuba.sdg.isbe.domain.model.Scenario;
import sk.stuba.sdg.isbe.handlers.exceptions.*;
import sk.stuba.sdg.isbe.repositories.CollectionRepository;
import sk.stuba.sdg.isbe.services.CollectionService;
import sk.stuba.sdg.isbe.services.ModuleService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CollectionServiceImpl implements CollectionService {

    private static final String EMPTY_STRING = "";

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private ModuleService moduleService;

    @Override
    public Collection createCollection(Collection collection) {
        if (collection.getName() == null || collection.getName().equals(EMPTY_STRING)) {
            throw new InvalidEntityException("Collection has no name set!");
        }

        collection.setCreatedAt(Instant.now().toEpochMilli());
        collectionRepository.save(collection);

        return collection;
    }

    @Override
    public List<Collection> getCollections() {
        return collectionRepository.findAll();
    }

    @Override
    public Collection getCollectionById(String collectionId) {
        Optional<Collection> optionalCollection = collectionRepository.findById(collectionId);
        if (optionalCollection.isEmpty()) {
            throw new NotFoundCustomException("Collection with ID: '" + collectionId + "' was not found!");
        }
        return optionalCollection.get();
    }

    @Override
    public List<Module> getModulesInCollection(String collectionId) {
        Collection collection = getCollectionById(collectionId);
        return collection.getModules();
    }

    @Override
    public Collection updateCollection(String collectionId, Collection updatedCollection) {
        Collection collection = getCollectionById(collectionId);

        if (updatedCollection.getName() != null) {
            collection.setName(updatedCollection.getName());
        }
        if (updatedCollection.getModules() != null) {
            collection.setModules(updatedCollection.getModules());
        }

        collectionRepository.save(collection);
        return collection;
    }

    @Override
    public ResponseEntity<Collection> deleteCollection(String collectionId) {
        Optional<Collection> collectionToDeleteOptional = collectionRepository.findById(collectionId);

        if (collectionToDeleteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        collectionRepository.deleteById(collectionId);
        return ResponseEntity.ok(collectionToDeleteOptional.get());
    }

    @Override
    public ResponseEntity<Module> addModuleToCollection(String collectionId, String moduleId) {
        Collection collection = getCollectionById(collectionId);
        Module moduleToAdd = moduleService.getModuleById(moduleId);

        // Check if the module is already in the collection
        if (collection.getModules().contains(moduleToAdd)) {
            throw new InvalidEntityException("Module is already in the collection!");
        }

        collection.getModules().add(moduleToAdd);
        collectionRepository.save(collection);

        return ResponseEntity.ok(moduleToAdd);
    }

    @Override
    public ResponseEntity<Module> removeModuleFromCollection(String collectionId, String moduleId) {
        Collection collection = getCollectionById(collectionId);
        Module moduleToRemove = moduleService.getModuleById(moduleId);

        // Check if the collection contains the module to be removed
        if (!collection.getModules().contains(moduleToRemove)) {
            throw new NotFoundCustomException("Module not found in the collection!");
        }

        collection.getModules().remove(moduleToRemove);
        collectionRepository.save(collection);

        return ResponseEntity.ok(moduleToRemove);
    }
}
