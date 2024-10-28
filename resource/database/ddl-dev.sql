--
-- PostgreSQL database dump
--

-- Dumped from database version 14.10 (Ubuntu 14.10-0ubuntu0.22.04.1)
-- Dumped by pg_dump version 14.10 (Ubuntu 14.10-0ubuntu0.22.04.1)

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
-- Name: edu4; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA miner;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: Author; Type: TABLE; Schema: edu4; Owner: -
--

CREATE TABLE miner."Author" (
    id bigint NOT NULL,
    name character varying NOT NULL
);


--
-- Name: Author_id_seq; Type: SEQUENCE; Schema: edu4; Owner: -
--

ALTER TABLE edu4."Author" ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME edu4."Author_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: Course; Type: TABLE; Schema: edu4; Owner: -
--

CREATE TABLE edu4."Course" (
    id bigint NOT NULL,
    "authorId" bigint NOT NULL,
    title character varying NOT NULL,
    description character varying NOT NULL,
    "scriptLibrary" character varying NOT NULL
);


--
-- Name: Course_id_seq; Type: SEQUENCE; Schema: edu4; Owner: -
--

ALTER TABLE edu4."Course" ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME edu4."Course_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: Unit; Type: TABLE; Schema: edu4; Owner: -
--

CREATE TABLE edu4."Unit" (
    id bigint NOT NULL,
    "courseId" bigint NOT NULL,
    index integer NOT NULL,
    title character varying NOT NULL,
    script character varying NOT NULL
);


--
-- Name: Unit_id_seq; Type: SEQUENCE; Schema: edu4; Owner: -
--

ALTER TABLE edu4."Unit" ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME edu4."Unit_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: Author Author_pkey; Type: CONSTRAINT; Schema: edu4; Owner: -
--

ALTER TABLE ONLY edu4."Author"
    ADD CONSTRAINT "Author_pkey" PRIMARY KEY (id);


--
-- Name: Course Course_pkey; Type: CONSTRAINT; Schema: edu4; Owner: -
--

ALTER TABLE ONLY edu4."Course"
    ADD CONSTRAINT "Course_pkey" PRIMARY KEY (id);


--
-- Name: Unit Unit_pkey; Type: CONSTRAINT; Schema: edu4; Owner: -
--

ALTER TABLE ONLY edu4."Unit"
    ADD CONSTRAINT "Unit_pkey" PRIMARY KEY (id);


--
-- Name: Course Course_authorId_fkey; Type: FK CONSTRAINT; Schema: edu4; Owner: -
--

ALTER TABLE ONLY edu4."Course"
    ADD CONSTRAINT "Course_authorId_fkey" FOREIGN KEY ("authorId") REFERENCES edu4."Author"(id);


--
-- Name: Unit Unit_courseId_fkey; Type: FK CONSTRAINT; Schema: edu4; Owner: -
--

ALTER TABLE ONLY edu4."Unit"
    ADD CONSTRAINT "Unit_courseId_fkey" FOREIGN KEY ("courseId") REFERENCES edu4."Course"(id);


--
-- PostgreSQL database dump complete
--

