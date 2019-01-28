
DROP SCHEMA IF EXISTS "miner" CASCADE;
CREATE SCHEMA "miner";

-- -------------------------------------------------------------------------
-- - structure
-- -------------------------------------------------------------------------


-- faction data
-- --------------------------

CREATE TABLE "miner"."Faction" (
	"id" bigserial NOT NULL PRIMARY KEY,
	"score" bigint NOT NULL,
	"divinePower" bigint NOT NULL
);


-- user account data
-- --------------------------

CREATE TABLE "miner"."UserAccount" (
	"id" bigserial NOT NULL PRIMARY KEY,
	"username" character varying(255) NOT NULL,
	"passwordHash" character varying(255) NOT NULL,
	"deleted" boolean NOT NULL DEFAULT false
);


-- player data
-- --------------------------

CREATE TABLE "miner"."Player" (
	"id" bigserial NOT NULL PRIMARY KEY,
	"userAccountId" bigint NOT NULL REFERENCES "miner"."UserAccount" ON DELETE CASCADE,
	"name" character varying(255) NOT NULL,
	"factionId" bigint NOT NULL REFERENCES "miner"."Faction",
	"x" decimal(10,2) NOT NULL DEFAULT 0,
	"y" decimal(10,2) NOT NULL DEFAULT 0,
	"z" decimal(10,2) NOT NULL DEFAULT 0,
	"leftAngle" decimal(5,2) NOT NULL DEFAULT 0,
	"upAngle" decimal(5,2) NOT NULL DEFAULT 0,
	"coins" bigint NOT NULL,
	"deleted" boolean NOT NULL DEFAULT false
);

CREATE TABLE "miner"."PlayerAwardedAchievement" (
	"id" bigserial NOT NULL PRIMARY KEY,
	"playerId" bigint NOT NULL REFERENCES "miner"."Player" ON DELETE CASCADE,
	"achievementCode" character varying(255) NOT NULL
);
CREATE UNIQUE INDEX "PlayerAwardedAchievement_main" ON "miner"."PlayerAwardedAchievement" ("playerId", "achievementCode");

CREATE TABLE "miner"."PlayerInventorySlot" (
	"id" bigserial NOT NULL PRIMARY KEY,
	"playerId" bigint NOT NULL REFERENCES "miner"."Player" ON DELETE CASCADE,
	"equipped" boolean NOT NULL,
	"index" integer NOT NULL,
	"type" integer NOT NULL,
	"quantity" integer NOT NULL
);
CREATE INDEX "PlayerInventorySlot_main" ON "miner"."PlayerInventorySlot" ("playerId", "equipped", "index");



-- -------------------------------------------------------------------------
-- - data
-- -------------------------------------------------------------------------


-- factions
-- --------------------------

INSERT INTO "miner"."Faction" ("score", "divinePower") VALUES
(0, 0),
(0, 0),
(0, 0),
(0, 0);


-- users, players and related data
-- --------------------------

INSERT INTO "miner"."UserAccount" ("username", "passwordHash") VALUES
('martin', '$2a$12$.5KM.jQ/TnPn7bMET7.lO.CnGxUzssEr8w590eYQYl8XRkui2OCg6');

INSERT INTO	"miner"."Player" ("userAccountId", "name", "factionId", "coins") VALUES
(1, 'Big Boss', 1, 123);




-- -------------------------------------------------------------------------
-- - constraints
-- -------------------------------------------------------------------------
