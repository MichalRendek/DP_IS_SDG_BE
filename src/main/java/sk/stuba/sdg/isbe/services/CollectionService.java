package sk.stuba.sdg.isbe.services;

import org.springframework.http.ResponseEntity;
import sk.stuba.sdg.isbe.domain.model.Collection;
import sk.stuba.sdg.isbe.domain.model.Module;

import java.util.List;

public interface CollectionService {
    Collection createCollection(Collection collection);

    List<Collection> getCollections();

    Collection getCollectionById(String collectionId);

    List<Module> getModulesInCollection(String collectionId);

    Collection updateCollection(String collectionId, Collection updatedCollection);

    ResponseEntity<Collection> deleteCollection(String collectionId);

    ResponseEntity<Module> addModuleToCollection(String collectionId, String moduleId);

    ResponseEntity<Module> removeModuleFromCollection(String collectionId, String moduleId);
}
