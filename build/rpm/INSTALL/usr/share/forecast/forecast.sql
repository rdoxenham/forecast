--
-- PostgreSQL database cluster dump
--

\connect postgres

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;


--
-- Database creation
--

CREATE DATABASE engine WITH TEMPLATE = template0 OWNER = postgres;
ALTER DATABASE engine SET client_min_messages TO 'error';
CREATE DATABASE forecast WITH TEMPLATE = template0 OWNER = forecast;
REVOKE ALL ON DATABASE template1 FROM PUBLIC;
REVOKE ALL ON DATABASE template1 FROM postgres;
GRANT ALL ON DATABASE template1 TO postgres;
GRANT CONNECT ON DATABASE template1 TO PUBLIC;


\connect engine

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

\connect forecast

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: adaptors; Type: TABLE; Schema: public; Owner: forecast; Tablespace: 
--

CREATE TABLE adaptors (
    adaptor_name character varying(100) NOT NULL,
    adaptor_url character varying(100) NOT NULL,
    adaptor_type character varying(20) NOT NULL,
    adaptor_user character varying(100) NOT NULL,
    adaptor_pass character varying(100) NOT NULL,
    adaptor_enabled boolean NOT NULL
);


ALTER TABLE public.adaptors OWNER TO forecast;

--
-- Name: exports; Type: TABLE; Schema: public; Owner: forecast; Tablespace:
--

CREATE TABLE exports (
    export_id serial,
    export_date timestamp without time zone NOT NULL,
    export_units bigint NOT NULL
);


ALTER TABLE public.exports OWNER TO forecast;

--
-- Name: exports_pkey; Type: CONSTRAINT; Schema: public; Owner: forecast; Tablespace:
--

ALTER TABLE ONLY exports
    ADD CONSTRAINT export_id PRIMARY KEY (export_id);

--
-- Name: vm_stats; Type: TABLE; Schema: public; Owner: forecast; Tablespace: 
--

CREATE TABLE vm_stats (
    fc_id character varying(32) NOT NULL,
    vm_id character varying(100) NOT NULL,
    vm_name character varying(50) NOT NULL,
    vm_hyp character varying(30) NOT NULL,
    vm_type character varying(30) NOT NULL,
    vm_vcpu bigint NOT NULL,
    vm_mem bigint NOT NULL,
    vm_units numeric NOT NULL,
    first_created timestamp without time zone,
    last_updated timestamp without time zone
);


ALTER TABLE public.vm_stats OWNER TO forecast;

--
-- Name: adaptors_pkey; Type: CONSTRAINT; Schema: public; Owner: forecast; Tablespace: 
--

ALTER TABLE ONLY adaptors
    ADD CONSTRAINT adaptors_pkey PRIMARY KEY (adaptor_url);


--
-- Name: vm_stats_pkey; Type: CONSTRAINT; Schema: public; Owner: forecast; Tablespace: 
--

ALTER TABLE ONLY vm_stats
    ADD CONSTRAINT vm_stats_pkey PRIMARY KEY (fc_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

\connect postgres

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: postgres; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON DATABASE postgres IS 'default administrative connection database';



--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

\connect template1

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: template1; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON DATABASE template1 IS 'default template for new databases';



--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

--
-- PostgreSQL database cluster dump complete
--

