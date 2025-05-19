-- PostgreSQL schema for Wedding Photo App
-- Run this script to initialize the database

-- Create the photos table
CREATE TABLE IF NOT EXISTS photos (
    photo_id SERIAL PRIMARY KEY,
    image_url TEXT NOT NULL,
    uploader_id TEXT NOT NULL,
    uploaded_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create an index on uploader_id for faster lookups by user
CREATE INDEX IF NOT EXISTS idx_photos_uploader_id ON photos(uploader_id);

-- Create an index on uploaded_at for efficient sorting by timestamp
CREATE INDEX IF NOT EXISTS idx_photos_uploaded_at ON photos(uploaded_at DESC);

-- Create a function to clean up old photos (optional, for future use)
CREATE OR REPLACE FUNCTION cleanup_old_photos(days_to_keep INTEGER)
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM photos
    WHERE uploaded_at < NOW() - (days_to_keep * INTERVAL '1 day');
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;