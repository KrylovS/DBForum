DROP TABLE IF EXISTS "User" CASCADE ;
DROP TABLE IF EXISTS Forum CASCADE ;
DROP TABLE IF EXISTS Thread CASCADE ;
DROP TABLE IF EXISTS Post;
DROP TABLE IF EXISTS UserVoteThread;
DROP TABLE IF EXISTS UserForum;

DROP INDEX IF EXISTS forum_user_idx;
DROP INDEX IF EXISTS forum_slug_idx;

DROP INDEX IF EXISTS thread_author_idx;
DROP INDEX IF EXISTS thread_forum_idx;
DROP INDEX IF EXISTS thread_slug_idx;

DROP INDEX IF EXISTS post_author_idx;
DROP INDEX IF EXISTS post_forum_idx;
DROP INDEX IF EXISTS post_thread_idx;
DROP INDEX IF EXISTS post_parent_idx;

DROP INDEX IF EXISTS user_forum_user_idx;
DROP INDEX IF EXISTS user_forum_forum_idx;


CREATE EXTENSION IF NOT EXISTS citext;

SET SYNCHRONOUS_COMMIT = 'off';

CREATE TABLE IF NOT EXISTS "User" (
  id SERIAL PRIMARY KEY,
  nickname CITEXT COLLATE "ucs_basic" UNIQUE NOT NULL,
  fullname VARCHAR(256) NOT NULL,
  about TEXT,
  email CITEXT COLLATE "ucs_basic" UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS Forum (
  id SERIAL PRIMARY KEY,
  title VARCHAR(256),
  "user" CITEXT COLLATE "ucs_basic" NOT NULL REFERENCES "User" (nickname) ON DELETE CASCADE,
  slug CITEXT COLLATE "ucs_basic" NOT NULL UNIQUE,
  posts INTEGER DEFAULT 0,
  threads INTEGER DEFAULT 0
);
CREATE INDEX forum_user_idx ON Forum ("user");
CREATE INDEX forum_slug_idx ON Forum (slug);

CREATE TABLE IF NOT EXISTS Thread (
  id SERIAL PRIMARY KEY,
  title VARCHAR(256),
  author CITEXT COLLATE "ucs_basic" NOT NULL REFERENCES "User" (nickname) ON DELETE CASCADE,
  forum CITEXT NOT NULL REFERENCES Forum (slug) ON DELETE CASCADE,
  message TEXT,
  votes INTEGER DEFAULT 0,
  slug CITEXT UNIQUE,
  created TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX thread_author_idx ON Thread (author);
CREATE INDEX thread_forum_idx ON Thread (forum);
CREATE INDEX thread_slug_idx ON Thread (slug);

CREATE TABLE IF NOT EXISTS UserVoteThread (
  userID INTEGER REFERENCES "User" (id) ON DELETE CASCADE ,
  threadID INTEGER REFERENCES Thread (id) ON DELETE CASCADE,
  voice INTEGER DEFAULT 0
  --UNIQUE (userID, threadID)
);

CREATE TABLE IF NOT EXISTS Post (
  id SERIAL PRIMARY KEY,
  parent INTEGER DEFAULT 0,
  author CITEXT COLLATE "ucs_basic" REFERENCES "User" (nickname) ON DELETE CASCADE ,
  message VARCHAR,
  isEdited BOOLEAN DEFAULT FALSE,
  forum CITEXT REFERENCES Forum (slug) ON DELETE CASCADE ,
  thread INTEGER REFERENCES Thread (id) ON DELETE CASCADE ,
  created TIMESTAMP DEFAULT now(),
  tree INTEGER [] NOT NULL
);
CREATE INDEX post_author_idx ON Post (author);
CREATE INDEX post_forum_idx ON Post (forum);
CREATE INDEX post_thread_idx ON Post (thread);
CREATE INDEX post_parent_idx ON Post (parent);


CREATE TABLE IF NOT EXISTS UserForum (
  user_nickname CITEXT,
  forum CITEXT,
  UNIQUE(user_nickname, forum)
);

CREATE INDEX IF NOT EXISTS user_forum_user_idx ON UserForum (user_nickname);
CREATE INDEX IF NOT EXISTS user_forum_forum_idx ON UserForum (forum);
