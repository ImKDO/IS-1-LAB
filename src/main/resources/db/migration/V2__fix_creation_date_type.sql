-- Drop the existing column (we'll recreate with correct type)
ALTER TABLE cities DROP COLUMN creation_date;

-- Add the column back with the correct type
ALTER TABLE cities ADD COLUMN creation_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP;
