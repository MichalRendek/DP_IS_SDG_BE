package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Collection;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Module;
import sk.stuba.sdg.isbe.services.CollectionService;

import java.util.List;

@RestController
@RequestMapping("api/collection")
public class CollectionController {

    @Autowired
    private CollectionService collectionService;

    @Operation(summary = "Get all collections")
    @GetMapping
    public List<Collection> getCollections() {
        return collectionService.getCollections();
    }

    @Operation(summary = "Create a new collection")
    @PostMapping("/create")
    public Collection createCollection(@Valid @RequestBody Collection collection) {
        return collectionService.createCollection(collection);
    }

    @Operation(summary = "Update an existing collection by ID")
    @PutMapping("/update/{collectionId}")
    public Collection updateCollection(@PathVariable String collectionId, @RequestBody Collection collection) {
        return collectionService.updateCollection(collectionId, collection);
    }

    @Operation(summary = "Get a collection by its ID")
    @GetMapping("/{collectionId}")
    public Collection getCollectionById(@PathVariable String collectionId) {
        return collectionService.getCollectionById(collectionId);
    }

    @Operation(summary = "Delete a collection by its ID")
    @DeleteMapping("/delete/{collectionId}")
    public ResponseEntity<Collection> deleteCollection(@PathVariable String collectionId) {
        return collectionService.deleteCollection(collectionId);
    }

    @Operation(summary = "Add a module to a collection")
    @PutMapping("/addModule/{collectionId}/{moduleId}")
    public ResponseEntity<Module> addModuleToCollection(@PathVariable String collectionId, @PathVariable String moduleId) {
        return collectionService.addModuleToCollection(collectionId, moduleId);
    }

    @Operation(summary = "Remove a module from a collection")
    @DeleteMapping("/removeModule/{collectionId}/{moduleId}")
    public ResponseEntity<Module> removeModuleFromCollection(@PathVariable String collectionId, @PathVariable String moduleId) {
        return collectionService.removeModuleFromCollection(collectionId, moduleId);
    }

    @Operation(summary = "Get all modules in a collection")
    @GetMapping("/getModules/{collectionId}")
    public List<Module> getModulesInCollection(@PathVariable String collectionId) {
        return collectionService.getModulesInCollection(collectionId);
    }
}
