-- Initialize database schema

-- Create users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    points INTEGER DEFAULT 0,
    last_login TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create artworks table
CREATE TABLE artworks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    creation_date TIMESTAMP WITH TIME ZONE NOT NULL,
    rarity_points INTEGER DEFAULT 10,
    status VARCHAR(255) DEFAULT 'draft',
    location VARCHAR(255) DEFAULT '',
    nfc_tag_id INTEGER,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create nfc_tags table
CREATE TABLE nfc_tags (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    artwork_id INTEGER NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create collections table
CREATE TABLE collections (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_public BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create collection_artworks table
CREATE TABLE collection_artworks (
    collection_id INTEGER NOT NULL,
    artwork_id INTEGER NOT NULL,
    acquisition_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (collection_id, artwork_id)
);

-- Create badges table
CREATE TABLE badges (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    criteria TEXT NOT NULL,
    points INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create user_badges table
CREATE TABLE user_badges (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    badge_id INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create badge_progress table
CREATE TABLE badge_progress (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    badge_id INTEGER NOT NULL,
    progress INTEGER DEFAULT 0,
    completed BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create user_profiles table
CREATE TABLE user_profiles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    display_name VARCHAR(255),
    bio TEXT,
    location VARCHAR(255),
    website VARCHAR(255),
    badge_count INTEGER DEFAULT 0,
    collection_count INTEGER DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create notification_preferences table
CREATE TABLE notification_preferences (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    email_notifications BOOLEAN NOT NULL DEFAULT true,
    push_notifications BOOLEAN NOT NULL DEFAULT true,
    new_artwork_notify BOOLEAN NOT NULL DEFAULT true,
    badge_earned_notify BOOLEAN NOT NULL DEFAULT true,
    points_earned_notify BOOLEAN NOT NULL DEFAULT true,
    exhibition_notify BOOLEAN NOT NULL DEFAULT true,
    notification_token VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create notifications table
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(255) NOT NULL,
    priority INTEGER DEFAULT 1,
    read BOOLEAN DEFAULT false,
    sent BOOLEAN DEFAULT false,
    data JSONB,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create notification_queue table
CREATE TABLE notification_queue (
    id SERIAL PRIMARY KEY,
    notification_id INTEGER NOT NULL,
    priority INTEGER DEFAULT 0,
    retry_count INTEGER DEFAULT 0,
    status VARCHAR(255) DEFAULT 'pending',
    error TEXT,
    processed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create point_transactions table
CREATE TABLE point_transactions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    artwork_id INTEGER NOT NULL,
    points INTEGER NOT NULL,
    type VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create validation_rules table
CREATE TABLE validation_rules (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(255) NOT NULL,
    value TEXT NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create leaderboards table
CREATE TABLE leaderboards (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    points INTEGER DEFAULT 0,
    rank INTEGER,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create password_reset_tokens table
CREATE TABLE password_reset_tokens (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    token VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    used BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create schema_migrations table
CREATE TABLE schema_migrations (
    version BIGINT NOT NULL,
    dirty BOOLEAN NOT NULL
);

-- Create indexes
CREATE UNIQUE INDEX idx_users_username ON users(username);
CREATE UNIQUE INDEX idx_users_email ON users(email);
CREATE UNIQUE INDEX idx_nfc_tags_token ON nfc_tags(token);
CREATE INDEX idx_nfc_tags_artwork_id ON nfc_tags(artwork_id);
CREATE INDEX idx_collections_user_id ON collections(user_id);
CREATE INDEX idx_collection_artworks_collection_id ON collection_artworks(collection_id);
CREATE INDEX idx_collection_artworks_artwork_id ON collection_artworks(artwork_id);
CREATE INDEX idx_user_badges_user_id ON user_badges(user_id);
CREATE INDEX idx_user_badges_badge_id ON user_badges(badge_id);
CREATE INDEX idx_badge_progress_user_id ON badge_progress(user_id);
CREATE INDEX idx_badge_progress_badge_id ON badge_progress(badge_id);
CREATE UNIQUE INDEX idx_user_profiles_user_id ON user_profiles(user_id);
CREATE UNIQUE INDEX idx_notification_preferences_user_id ON notification_preferences(user_id);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notification_queue_notification_id ON notification_queue(notification_id);
CREATE INDEX idx_point_transactions_user_id ON point_transactions(user_id);
CREATE INDEX idx_point_transactions_artwork_id ON point_transactions(artwork_id);
CREATE UNIQUE INDEX idx_leaderboards_user_id ON leaderboards(user_id);
CREATE UNIQUE INDEX idx_password_reset_tokens_token ON password_reset_tokens(token);

-- Add foreign key constraints
ALTER TABLE artworks ADD CONSTRAINT fk_artworks_nfc_tag FOREIGN KEY (nfc_tag_id) REFERENCES nfc_tags(id);
ALTER TABLE nfc_tags ADD CONSTRAINT fk_nfc_tags_artwork FOREIGN KEY (artwork_id) REFERENCES artworks(id);
ALTER TABLE collections ADD CONSTRAINT fk_collections_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE collection_artworks ADD CONSTRAINT fk_collection_artworks_collection FOREIGN KEY (collection_id) REFERENCES collections(id);
ALTER TABLE collection_artworks ADD CONSTRAINT fk_collection_artworks_artwork FOREIGN KEY (artwork_id) REFERENCES artworks(id);
ALTER TABLE user_badges ADD CONSTRAINT fk_user_badges_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE user_badges ADD CONSTRAINT fk_user_badges_badge FOREIGN KEY (badge_id) REFERENCES badges(id);
ALTER TABLE badge_progress ADD CONSTRAINT fk_badge_progress_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE badge_progress ADD CONSTRAINT fk_badge_progress_badge FOREIGN KEY (badge_id) REFERENCES badges(id);
ALTER TABLE user_profiles ADD CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE notification_preferences ADD CONSTRAINT fk_notification_preferences_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE notifications ADD CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE notification_queue ADD CONSTRAINT fk_notification_queue_notification FOREIGN KEY (notification_id) REFERENCES notifications(id);
ALTER TABLE point_transactions ADD CONSTRAINT fk_point_transactions_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE point_transactions ADD CONSTRAINT fk_point_transactions_artwork FOREIGN KEY (artwork_id) REFERENCES artworks(id);
ALTER TABLE leaderboards ADD CONSTRAINT fk_leaderboards_user FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE password_reset_tokens ADD CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users(id);