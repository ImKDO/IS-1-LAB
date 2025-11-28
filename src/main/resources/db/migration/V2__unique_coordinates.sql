-- Add unique constraint on coordinates
CREATE UNIQUE INDEX IF NOT EXISTS idx_coordinates_unique ON coordinates(x_coordinate, y_coordinate);
