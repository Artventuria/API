-- Add venue_id column to artworks table
ALTER TABLE artworks ADD COLUMN venue_id VARCHAR(100);

-- Create index for faster location badge calculations
CREATE INDEX idx_artworks_venue_id ON artworks(venue_id);
