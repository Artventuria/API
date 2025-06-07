package com.artventuria.api.config.mongodb.migration;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ValidationOptions;
import com.mongodb.client.model.ValidationLevel;
import com.mongodb.client.model.CreateCollectionOptions;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;

@ChangeUnit(id = "initial-migration", order = "001", author = "system")
public class InitialMigration {

    
    @Execution
    public void execution(final MongoDatabase db) {
        // Create collections with validation
        createScanLogsCollection(db);
        createActivityCollection(db);
        createArtworkMetadataCollection(db);

        // Create indexes
        createScanLogsIndexes(db);
        createActivityIndexes(db);
        createArtworkMetadataIndexes(db);
    }

    @RollbackExecution
    public void rollbackExecution(final MongoDatabase db) {
        db.getCollection("scan_logs").drop();
        db.getCollection("activity").drop();
        db.getCollection("artwork_metadata").drop();
    }

    private void createScanLogsCollection(final MongoDatabase db) {
        Document validator = new Document("$jsonSchema", new Document()
                .append("bsonType", "object")
                .append("required", java.util.Arrays.asList("tag_id", "user_id", "artwork_id", "location", "timestamp"))
                .append("properties", new Document()
                        .append("tag_id", new Document("bsonType", "long").append("minimum", 1))
                        .append("user_id", new Document("bsonType", "long").append("minimum", 1))
                        .append("artwork_id", new Document("bsonType", "long").append("minimum", 1))
                        .append("location", new Document("bsonType", "string"))
                        .append("device_id", new Document("bsonType", java.util.Arrays.asList("string", "null")))
                        .append("ip_address", new Document("bsonType", java.util.Arrays.asList("string", "null")))
                        .append("is_valid", new Document("bsonType", "bool"))
                        .append("error_code", new Document("bsonType", java.util.Arrays.asList("string", "null")))
                        .append("timestamp", new Document("bsonType", "date"))
                        .append("created_at", new Document("bsonType", "date"))
                        .append("updated_at", new Document("bsonType", "date"))));

        CreateCollectionOptions options = new CreateCollectionOptions()
            .validationOptions(new ValidationOptions()
                .validator(validator)
                .validationLevel(ValidationLevel.MODERATE));
        db.createCollection("scan_logs", options);
    }

    private void createActivityCollection(final MongoDatabase db) {
        Document validator = new Document("$jsonSchema", new Document()
                .append("bsonType", "object")
                .append("required", java.util.Arrays.asList("user_id", "type", "timestamp"))
                .append("properties", new Document()
                        .append("user_id", new Document("bsonType", "int").append("minimum", 1))
                        .append("type", new Document("bsonType", "string")
                                .append("enum", java.util.Arrays.asList("scan", "share", "badge_earned", "collection")))
                        .append("description", new Document("bsonType", "string"))
                        .append("metadata", new Document("bsonType", java.util.Arrays.asList("object", "null")))
                        .append("timestamp", new Document("bsonType", "date"))
                        .append("created_at", new Document("bsonType", "date"))
                        .append("updated_at", new Document("bsonType", "date"))));

        CreateCollectionOptions options = new CreateCollectionOptions()
            .validationOptions(new ValidationOptions()
                .validator(validator)
                .validationLevel(ValidationLevel.MODERATE));
        db.createCollection("activity", options);
    }

    private void createArtworkMetadataCollection(final MongoDatabase db) {
        Document validator = new Document("$jsonSchema", new Document()
                .append("bsonType", "object")
                .append("required", java.util.Arrays.asList("artwork_id"))
                .append("properties", new Document()
                        .append("artwork_id", new Document("bsonType", "long").append("minimum", 1))
                        .append("description_extended", new Document("bsonType", java.util.Arrays.asList("string", "null")))
                        .append("historical_context", new Document("bsonType", java.util.Arrays.asList("string", "null")))
                        .append("materials", new Document("bsonType", java.util.Arrays.asList("array", "null"))
                                .append("items", new Document("bsonType", "string")))
                        .append("dimensions", new Document("bsonType", java.util.Arrays.asList("object", "null"))
                                .append("properties", new Document()
                                        .append("height", new Document("bsonType", "double"))
                                        .append("width", new Document("bsonType", "double"))
                                        .append("depth", new Document("bsonType", "double"))
                                        .append("unit", new Document("bsonType", "string"))))
                        .append("tags", new Document("bsonType", java.util.Arrays.asList("array", "null"))
                            .append("items", new Document("bsonType", "string")))
                    .append("image_url", new Document("bsonType", java.util.Arrays.asList("string", "null")))
                    .append("temporary_exhibition", new Document("bsonType", "bool")
                            .append("description", "Whether the artwork is part of a temporary exhibition"))
                    .append("created_at", new Document("bsonType", "date"))
                    .append("updated_at", new Document("bsonType", "date"))));

        CreateCollectionOptions options = new CreateCollectionOptions()
            .validationOptions(new ValidationOptions()
                .validator(validator)
                .validationLevel(ValidationLevel.MODERATE));
        db.createCollection("artwork_metadata", options);
    }

    private void createScanLogsIndexes(final MongoDatabase db) {
        db.getCollection("scan_logs").createIndex(
                Indexes.compoundIndex(Indexes.ascending("user_id"), Indexes.descending("timestamp")));
        db.getCollection("scan_logs").createIndex(Indexes.ascending("artwork_id"));
        db.getCollection("scan_logs").createIndex(Indexes.ascending("tag_id"));
        db.getCollection("scan_logs").createIndex(Indexes.descending("timestamp"));
    }

    private void createActivityIndexes(final MongoDatabase db) {
        db.getCollection("activity").createIndex(
                Indexes.compoundIndex(Indexes.ascending("user_id"), Indexes.descending("timestamp")));
        db.getCollection("activity").createIndex(Indexes.ascending("type"));
        db.getCollection("activity").createIndex(Indexes.descending("timestamp"));
    }

    private void createArtworkMetadataIndexes(final MongoDatabase db) {
        db.getCollection("artwork_metadata").createIndex(
                Indexes.ascending("artwork_id"), new IndexOptions().unique(true));
        db.getCollection("artwork_metadata").createIndex(Indexes.ascending("tags"));
    }
}