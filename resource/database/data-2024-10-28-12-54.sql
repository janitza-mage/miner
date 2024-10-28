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
-- Data for Name: UserAccount; Type: TABLE DATA; Schema: miner; Owner: -
--

INSERT INTO miner."UserAccount" VALUES (1, 'martin', '$2a$12$.5KM.jQ/TnPn7bMET7.lO.CnGxUzssEr8w590eYQYl8XRkui2OCg6', false);


--
-- Data for Name: Player; Type: TABLE DATA; Schema: miner; Owner: -
--

INSERT INTO miner."Player" VALUES (1, 1, 'Big Boss', 0, 0.00, 0.00, 0.00, 0.00, 0.00, 123, false);


--
-- Data for Name: PlayerAwardedAchievement; Type: TABLE DATA; Schema: miner; Owner: -
--



--
-- Data for Name: PlayerInventorySlot; Type: TABLE DATA; Schema: miner; Owner: -
--



--
-- Name: PlayerAwardedAchievement_id_seq; Type: SEQUENCE SET; Schema: miner; Owner: -
--

SELECT pg_catalog.setval('miner."PlayerAwardedAchievement_id_seq"', 1, false);


--
-- Name: PlayerInventorySlot_id_seq; Type: SEQUENCE SET; Schema: miner; Owner: -
--

SELECT pg_catalog.setval('miner."PlayerInventorySlot_id_seq"', 1, false);


--
-- Name: Player_id_seq; Type: SEQUENCE SET; Schema: miner; Owner: -
--

SELECT pg_catalog.setval('miner."Player_id_seq"', 1, true);


--
-- Name: UserAccount_id_seq; Type: SEQUENCE SET; Schema: miner; Owner: -
--

SELECT pg_catalog.setval('miner."UserAccount_id_seq"', 1, true);


--
-- PostgreSQL database dump complete
--

