-- 1. Сделать таблицу artist
CREATE TABLE artist (
    mbid STRING,
    artist_mb STRING,
    artist_lastfm STRING,
    country_mb STRING,
    country_lastfm STRING,
    tags_mb array <STRING>,
    tags_lastfm array <STRING>,
    listeners_lastfm INT,
    scrobbles_lastfm INT,
    ambiguous_artist BOOLEAN
);

-- Загрузка данных
LOAD DATA INPATH '/artists.csv' OVERWRITE INTO TABLE artist;

-- 2. Исполнитель с максимальным числом скробблов
SELECT artist_lastfm
FROM artist
WHERE scrobbles_lastfm IN (
    SELECT max(scrobbles_lastfm) from artist
);
-- Ответ:
-- artist_lastfm
-- The Beatles

-- 3. Самый популрный тэг на lastfm
with tmp_tags_table as (
    SELECT
        trim(tag) AS trim_tag,
        COUNT(*) AS cnt_tag
    FROM artist LATERAL VIEW explode(tags_lastfm) t AS tag
    WHERE trim(tag) != ''
    GROUP BY trim(tag)
    ORDER BY cnt_tag DESC
    LIMIT 1
);

SELECT trim_tag FROM tmp_tags_table;
-- Ответ:
-- trim_tag
-- seen live

-- 4. Самые популярные исполнители 10 самых популярных тегов
with tmp_artists_pop as (
    SELECT
        artist_lastfm,
        trim(tag) as tag,
        scrobbles_lastfm
    FROM artist LATERAL VIEW explode(tags_lastfm) t AS tag
    WHERE
        scrobbles_lastfm IS NOT NULL
        AND trim(tag) != ''
), tmp_tags as (
    SELECT
        trim(tag) AS tag,
        count(*) AS cnt_tag
    FROM artist LATERAL VIEW explode(tags_lastfm) t AS tag
    WHERE
        trim(tag) != ''
    GROUP BY trim(tag)
    ORDER BY cnt_tag DESC
    LIMIT 10
);

SELECT
    DISTINCT artist_lastfm,
    scrobbles_lastfm
FROM tmp_artists_pop
WHERE tag IN ( SELECT tag FROM tmp_tags)
ORDER BY scrobbles_lastfm DESC
LIMIT 10;
-- Ответ:
-- artist_lastfm	scrobbles_lastfm
-- The Beatles	517126254
-- Radiohead	499548797
-- Coldplay	360111850
-- Muse	344838631
-- Arctic Monkeys	332306552
-- Pink Floyd	313236119
-- Linkin Park	294986508
-- Red Hot Chili Peppers	293784041
-- Lady Gaga	285469647
-- Metallica	281172228

-- 5. Самая популярная группа в России
with tmp_table_pop_art as (
    SELECT max(scrobbles_lastfm)
    FROM artist
    WHERE country_lastfm = 'Russia'
);

SELECT artist_lastfm
FROM artist
WHERE scrobbles_lastfm IN ( select * from tmp_table_pop_art);
-- Ответ:
-- artist_lastfm
-- Сплин