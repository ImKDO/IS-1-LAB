
-- Delete one city by climate (any one)
CREATE OR REPLACE FUNCTION fn_delete_by_climate(p_climate TEXT)
RETURNS INTEGER AS $$
DECLARE v_id INTEGER;
BEGIN
  SELECT id INTO v_id FROM cities WHERE climate = p_climate::text LIMIT 1;
  IF v_id IS NULL THEN
    RETURN 0;
  END IF;
  DELETE FROM cities WHERE id = v_id;
  RETURN v_id;
END;
$$ LANGUAGE plpgsql;

-- Average meters above sea level across all
CREATE OR REPLACE FUNCTION fn_avg_masl()
RETURNS DOUBLE PRECISION AS $$
DECLARE v_avg DOUBLE PRECISION;
BEGIN
  SELECT AVG(meters_above_sea_level) INTO v_avg FROM cities;
  RETURN v_avg;
END;
$$ LANGUAGE plpgsql;

-- Find cities by name containing substring
CREATE OR REPLACE FUNCTION fn_find_by_name_like(p_substr TEXT)
RETURNS SETOF cities AS $$
BEGIN
  RETURN QUERY
    SELECT * FROM cities WHERE name LIKE '%' || p_substr || '%';
END;
$$ LANGUAGE plpgsql;

-- Relocate population from city A to city B
CREATE OR REPLACE FUNCTION fn_relocate(p_from INTEGER, p_to INTEGER)
RETURNS VOID AS $$
DECLARE v_from_pop INTEGER;
BEGIN
  IF p_from = p_to THEN
    RAISE EXCEPTION 'Source and target must differ';
  END IF;
  SELECT population INTO v_from_pop FROM cities WHERE id = p_from FOR UPDATE;
  IF v_from_pop IS NULL THEN
    RAISE EXCEPTION 'Source city not found';
  END IF;
  PERFORM 1 FROM cities WHERE id = p_to FOR UPDATE;
  IF NOT FOUND THEN
    RAISE EXCEPTION 'Target city not found';
  END IF;
  UPDATE cities SET population = population + v_from_pop WHERE id = p_to;
  UPDATE cities SET population = 0 WHERE id = p_from;
END;
$$ LANGUAGE plpgsql;

-- Relocate to min population city
CREATE OR REPLACE FUNCTION fn_relocate_to_min(p_from INTEGER)
RETURNS INTEGER AS $$
DECLARE v_min_id INTEGER; v_from_pop INTEGER;
BEGIN
  SELECT id INTO v_min_id FROM cities ORDER BY population ASC LIMIT 1;
  IF v_min_id IS NULL THEN
    RAISE EXCEPTION 'No target city';
  END IF;
  IF v_min_id = p_from THEN
    RAISE EXCEPTION 'Source is already minimal';
  END IF;
  SELECT population INTO v_from_pop FROM cities WHERE id = p_from FOR UPDATE;
  IF v_from_pop IS NULL THEN
    RAISE EXCEPTION 'Source city not found';
  END IF;
  UPDATE cities SET population = population + v_from_pop WHERE id = v_min_id;
  UPDATE cities SET population = 0 WHERE id = p_from;
  RETURN v_min_id;
END;
$$ LANGUAGE plpgsql;


