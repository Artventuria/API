// MongoDB initialization script based on InitialMigration.java

// Use the artventuria database
use("artventuria");

// Create the scan_logs collection with validation
db.createCollection("scan_logs", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["tag_id", "user_id", "artwork_id", "location", "timestamp"],
      properties: {
        tag_id: { bsonType: "long", minimum: 1 },
        user_id: { bsonType: "long", minimum: 1 },
        artwork_id: { bsonType: "long", minimum: 1 },
        location: { bsonType: "string" },
        device_id: { bsonType: ["string", "null"] },
        ip_address: { bsonType: ["string", "null"] },
        is_valid: { bsonType: "bool" },
        error_code: { bsonType: ["string", "null"] },
        timestamp: { bsonType: "date" },
        created_at: { bsonType: "date" },
        updated_at: { bsonType: "date" }
      }
    }
  },
  validationLevel: "moderate"
});

// Create the activity collection with validation
db.createCollection("activity", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["user_id", "type", "timestamp"],
      properties: {
        user_id: { bsonType: "int", minimum: 1 },
        type: {
          bsonType: "string",
          enum: ["scan", "share", "badge_earned", "collection"]
        },
        description: { bsonType: "string" },
        metadata: { bsonType: ["object", "null"] },
        timestamp: { bsonType: "date" },
        created_at: { bsonType: "date" },
        updated_at: { bsonType: "date" }
      }
    }
  },
  validationLevel: "moderate"
});

// Create the artwork_metadata collection with validation
db.createCollection("artwork_metadata", {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["artwork_id"],
      properties: {
        artwork_id: { bsonType: "long", minimum: 1 },
        description_extended: { bsonType: ["string", "null"] },
        historical_context: { bsonType: ["string", "null"] },
        materials: {
          bsonType: ["array", "null"],
          items: { bsonType: "string" }
        },
        dimensions: {
          bsonType: ["object", "null"],
          properties: {
            height: { bsonType: "double" },
            width: { bsonType: "double" },
            depth: { bsonType: "double" },
            unit: { bsonType: "string" }
          }
        },
        tags: {
          bsonType: ["array", "null"],
          items: { bsonType: "string" }
        },
        temporary_exhibition: {
          bsonType: "bool",
          description: "Whether the artwork is part of a temporary exhibition"
        },
        created_at: { bsonType: "date" },
        updated_at: { bsonType: "date" }
      }
    }
  },
  validationLevel: "moderate"
});

// Create indexes for scan_logs
db.scan_logs.createIndex({ user_id: 1, timestamp: -1 });
db.scan_logs.createIndex({ artwork_id: 1 });
db.scan_logs.createIndex({ tag_id: 1 });
db.scan_logs.createIndex({ timestamp: -1 });

// Create indexes for activity
db.activity.createIndex({ user_id: 1, timestamp: -1 });
db.activity.createIndex({ type: 1 });
db.activity.createIndex({ timestamp: -1 });

// Create indexes for artwork_metadata
db.artwork_metadata.createIndex({ artwork_id: 1 }, { unique: true });
db.artwork_metadata.createIndex({ tags: 1 });

// Print the created collections
print("Collections MongoDB created successfully:");
print(db.getCollectionNames());
