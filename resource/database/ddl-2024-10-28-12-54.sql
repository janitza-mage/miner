--
-- PostgreSQL database dump
--

-- Dumped from database version 14.13 (Ubuntu 14.13-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.13 (Ubuntu 14.13-0ubuntu0.22.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: miner; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA miner;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: Player; Type: TABLE; Schema: miner; Owner: -
--

CREATE TABLE miner."Player" (
    id bigint NOT NULL,
    "userAccountId" bigint NOT NULL,
    name character varying(255) NOT NULL,
    faction integer NOT NULL,
    x numeric(10,2) DEFAULT 0 NOT NULL,
    y numeric(10,2) DEFAULT 0 NOT NULL,
    z numeric(10,2) DEFAULT 0 NOT NULL,
    "leftAngle" numeric(5,2) DEFAULT 0 NOT NULL,
    "upAngle" numeric(5,2) DEFAULT 0 NOT NULL,
    coins bigint NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    CONSTRAINT "Player_coins_check" CHECK ((coins >= 0)),
    CONSTRAINT "Player_faction_check" CHECK (((faction >= 0) AND (faction < 4)))
);


--
-- Name: PlayerAwardedAchievement; Type: TABLE; Schema: miner; Owner: -
--

CREATE TABLE miner."PlayerAwardedAchievement" (
    id bigint NOT NULL,
    "playerId" bigint NOT NULL,
    "achievementCode" character varying(255) NOT NULL
);


--
-- Name: PlayerAwardedAchievement_id_seq; Type: SEQUENCE; Schema: miner; Owner: -
--

CREATE SEQUENCE miner."PlayerAwardedAchievement_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: PlayerAwardedAchievement_id_seq; Type: SEQUENCE OWNED BY; Schema: miner; Owner: -
--

ALTER SEQUENCE miner."PlayerAwardedAchievement_id_seq" OWNED BY miner."PlayerAwardedAchievement".id;


--
-- Name: PlayerInventorySlot; Type: TABLE; Schema: miner; Owner: -
--

CREATE TABLE miner."PlayerInventorySlot" (
    id bigint NOT NULL,
    "playerId" bigint NOT NULL,
    type integer NOT NULL,
    quantity integer NOT NULL,
    equipped boolean NOT NULL,
    CONSTRAINT "PlayerInventorySlot_quantity_check" CHECK ((quantity > 0))
);


--
-- Name: PlayerInventorySlot_id_seq; Type: SEQUENCE; Schema: miner; Owner: -
--

CREATE SEQUENCE miner."PlayerInventorySlot_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: PlayerInventorySlot_id_seq; Type: SEQUENCE OWNED BY; Schema: miner; Owner: -
--

ALTER SEQUENCE miner."PlayerInventorySlot_id_seq" OWNED BY miner."PlayerInventorySlot".id;


--
-- Name: Player_id_seq; Type: SEQUENCE; Schema: miner; Owner: -
--

CREATE SEQUENCE miner."Player_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: Player_id_seq; Type: SEQUENCE OWNED BY; Schema: miner; Owner: -
--

ALTER SEQUENCE miner."Player_id_seq" OWNED BY miner."Player".id;


--
-- Name: UserAccount; Type: TABLE; Schema: miner; Owner: -
--

CREATE TABLE miner."UserAccount" (
    id bigint NOT NULL,
    username character varying(255) NOT NULL,
    "passwordHash" character varying(255) NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    CONSTRAINT "UserAccount_username_check" CHECK (((username)::text <> ''::text))
);


--
-- Name: UserAccount_id_seq; Type: SEQUENCE; Schema: miner; Owner: -
--

CREATE SEQUENCE miner."UserAccount_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: UserAccount_id_seq; Type: SEQUENCE OWNED BY; Schema: miner; Owner: -
--

ALTER SEQUENCE miner."UserAccount_id_seq" OWNED BY miner."UserAccount".id;


--
-- Name: Player id; Type: DEFAULT; Schema: miner; Owner: -
--

ALTER TABLE ONLY miner."Player" ALTER COLUMN id SET DEFAULT nextval('miner."Player_id_seq"'::regclass);


--
-- Name: PlayerAwardedAchievement id; Type: DEFAULT; Schema: miner; Owner: -
--

ALTER TABLE ONLY miner."PlayerAwardedAchievement" ALTER COLUMN id SET DEFAULT nextval('miner."PlayerAwardedAchievement_id_seq"'::regclass);


--
-- Name: PlayerInventorySlot id; Type: DEFAULT; Schema: miner; Owner: -
--

ALTER TABLE ONLY miner."PlayerInventorySlot" ALTER COLUMN id SET DEFAULT nextval('miner."PlayerInventorySlot_id_seq"'::regclass);


--
-- Name: UserAccount id; Type: DEFAULT; Schema: miner; Owner: -
--

ALTER TABLE ONLY miner."UserAccount" ALTER COLUMN id SET DEFAULT nextval('miner."UserAccount_id_seq"'::regclass);


--
-- Name: PlayerAwardedAchievement PlayerAwardedAchievement_pkey; Type: CONSTRAINT; Schema: miner; Owner: -
--

ALTER TABLE ONLY miner."PlayerAwardedAchievement"
    ADD CONSTRAINT "PlayerAwardedAchievement_pkey" PRIMARY KEY (id);


--
-- Name: PlayerInventorySlot PlayerInventorySlot_pkey; Type: CONSTRAINT; Schema: miner; Owner: -
--

ALTER TABLE ONLY miner."PlayerInventorySlot"
    ADD CONSTRAINT "PlayerInventorySlot_pkey" PRIMARY KEY (id);


--
-- Name: Player Player_pkey; Type: CONSTRAINT; Schema: miner; Owner: -
--

ALTER TABLE ONLY miner."Player"
    ADD CONSTRAINT "Player_pkey" PRIMARY KEY (id);


--
-- Name: UserAccount UserAccount_pkey; Type: CONSTRAINT; Schema: miner; Owner: -
--

ALTER TABLE ONLY miner."UserAccount"
    ADD CONSTRAINT "UserAccount_pkey" PRIMARY KEY (id);


--
-- Name: PlayerAwardedAchievement_main; Type: INDEX; Schema: miner; Owner: -
--

CREATE UNIQUE INDEX "PlayerAwardedAchievement_main" ON miner."PlayerAwardedAchievement" USING btree ("playerId", "achievementCode");


--
-- Name: PlayerInventorySlot_main; Type: INDEX; Schema: miner; Owner: -
--

CREATE INDEX "PlayerInventorySlot_main" ON miner."PlayerInventorySlot" USING btree ("playerId", id);


--
-- Name: PlayerAwardedAchievement PlayerAwardedAchievement_playerId_fkey; Type: FK CONSTRAINT; Schema: miner; Owner: -
--

ALTER TABLE ONLY miner."PlayerAwardedAchievement"
    ADD CONSTRAINT "PlayerAwardedAchievement_playerId_fkey" FOREIGN KEY ("playerId") REFERENCES miner."Player"(id) ON DELETE CASCADE;


--
-- Name: PlayerInventorySlot PlayerInventorySlot_playerId_fkey; Type: FK CONSTRAINT; Schema: miner; Owner: -
--

ALTER TABLE ONLY miner."PlayerInventorySlot"
    ADD CONSTRAINT "PlayerInventorySlot_playerId_fkey" FOREIGN KEY ("playerId") REFERENCES miner."Player"(id) ON DELETE CASCADE;


--
-- Name: Player Player_userAccountId_fkey; Type: FK CONSTRAINT; Schema: miner; Owner: -
--

ALTER TABLE ONLY miner."Player"
    ADD CONSTRAINT "Player_userAccountId_fkey" FOREIGN KEY ("userAccountId") REFERENCES miner."UserAccount"(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

