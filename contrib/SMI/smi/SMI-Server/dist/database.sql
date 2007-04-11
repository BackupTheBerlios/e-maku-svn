--
-- PostgreSQL database dump
--

-- Started on 2007-03-29 17:13:11 COT

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- TOC entry 1664 (class 0 OID 0)
-- Dependencies: 5
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 1278 (class 1259 OID 16494)
-- Dependencies: 1617 1618 5
-- Name: grupos; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE grupos (
    gid integer NOT NULL,
    nombre_grupo character varying(40) NOT NULL,
    mostrable boolean DEFAULT false NOT NULL,
    zona boolean DEFAULT false NOT NULL
);


--
-- TOC entry 1277 (class 1259 OID 16492)
-- Dependencies: 1278 5
-- Name: grupos_gid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE grupos_gid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1666 (class 0 OID 0)
-- Dependencies: 1277
-- Name: grupos_gid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE grupos_gid_seq OWNED BY grupos.gid;


--
-- TOC entry 1667 (class 0 OID 0)
-- Dependencies: 1277
-- Name: grupos_gid_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('grupos_gid_seq', 20, true);


--
-- TOC entry 1280 (class 1259 OID 16501)
-- Dependencies: 1620 1621 1622 1623 1624 5
-- Name: mensajes; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE mensajes (
    mid integer NOT NULL,
    uid_destino integer,
    fecha date NOT NULL,
    hora time without time zone NOT NULL,
    asunto character varying(256) NOT NULL,
    texto text NOT NULL,
    confirmado boolean DEFAULT false NOT NULL,
    valido boolean DEFAULT false NOT NULL,
    tiempo_vida integer DEFAULT 1 NOT NULL,
    uid_origen integer NOT NULL,
    control boolean DEFAULT false NOT NULL,
    minutos smallint DEFAULT -1 NOT NULL,
    fecha_confirmacion timestamp without time zone
);


--
-- TOC entry 1279 (class 1259 OID 16499)
-- Dependencies: 1280 5
-- Name: mensajes_mid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE mensajes_mid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1668 (class 0 OID 0)
-- Dependencies: 1279
-- Name: mensajes_mid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE mensajes_mid_seq OWNED BY mensajes.mid;


--
-- TOC entry 1669 (class 0 OID 0)
-- Dependencies: 1279
-- Name: mensajes_mid_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('mensajes_mid_seq', 1, false);


--
-- TOC entry 1281 (class 1259 OID 16510)
-- Dependencies: 1625 1626 5
-- Name: puntosv; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE puntosv (
    codigo character(4) NOT NULL,
    nombre character varying(50) NOT NULL,
    ip inet DEFAULT '127.0.0.1'::inet,
    gid integer DEFAULT 5 NOT NULL
);


--
-- TOC entry 1282 (class 1259 OID 16517)
-- Dependencies: 5
-- Name: transacciones; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE transacciones (
    codigo character(5) NOT NULL,
    nombre character varying(100) NOT NULL,
    driver character varying(200) NOT NULL,
    args text NOT NULL
);


--
-- TOC entry 1284 (class 1259 OID 16524)
-- Dependencies: 1628 1629 1630 1631 1632 1633 5
-- Name: usuarios; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE usuarios (
    uid integer NOT NULL,
    "login" character varying(30) NOT NULL,
    clave character(32) DEFAULT 'd41d8cd98f00b204e9800998ecf8427e'::bpchar NOT NULL,
    nombres character varying(100) DEFAULT 'Sin Nombre'::character varying,
    correo character varying(200),
    "admin" boolean DEFAULT false NOT NULL,
    habilitado boolean DEFAULT true NOT NULL,
    gid integer DEFAULT 5,
    audit boolean DEFAULT false NOT NULL
);


--
-- TOC entry 1285 (class 1259 OID 16533)
-- Dependencies: 1634 5
-- Name: usuarios_pventa; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE usuarios_pventa (
    uid integer NOT NULL,
    codigo_pventa character(4) NOT NULL,
    valid_ip boolean DEFAULT false NOT NULL
);


--
-- TOC entry 1283 (class 1259 OID 16522)
-- Dependencies: 1284 5
-- Name: usuarios_uid_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE usuarios_uid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1670 (class 0 OID 0)
-- Dependencies: 1283
-- Name: usuarios_uid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE usuarios_uid_seq OWNED BY usuarios.uid;


--
-- TOC entry 1671 (class 0 OID 0)
-- Dependencies: 1283
-- Name: usuarios_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('usuarios_uid_seq', 1, false);


--
-- TOC entry 1616 (class 2604 OID 16496)
-- Dependencies: 1277 1278 1278
-- Name: gid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE grupos ALTER COLUMN gid SET DEFAULT nextval('grupos_gid_seq'::regclass);


--
-- TOC entry 1619 (class 2604 OID 16503)
-- Dependencies: 1279 1280 1280
-- Name: mid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE mensajes ALTER COLUMN mid SET DEFAULT nextval('mensajes_mid_seq'::regclass);


--
-- TOC entry 1627 (class 2604 OID 16526)
-- Dependencies: 1284 1283 1284
-- Name: uid; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE usuarios ALTER COLUMN uid SET DEFAULT nextval('usuarios_uid_seq'::regclass);


--
-- TOC entry 1655 (class 0 OID 16494)
-- Dependencies: 1278
-- Data for Name: grupos; Type: TABLE DATA; Schema: public; Owner: -
--

COPY grupos (gid, nombre_grupo, mostrable, zona) FROM stdin;
4	INFORMATICA	t	f
15	PALMIRA	f	t
17	AUDITORIA	t	f
5	SIN GRUPO	f	t
6	ADMINISTRATIVOS	t	f
20	NONE	f	t
\.


--
-- TOC entry 1656 (class 0 OID 16501)
-- Dependencies: 1280
-- Data for Name: mensajes; Type: TABLE DATA; Schema: public; Owner: -
--

COPY mensajes (mid, uid_destino, fecha, hora, asunto, texto, confirmado, valido, tiempo_vida, uid_origen, control, minutos, fecha_confirmacion) FROM stdin;
\.


--
-- TOC entry 1657 (class 0 OID 16510)
-- Dependencies: 1281
-- Data for Name: puntosv; Type: TABLE DATA; Schema: public; Owner: -
--

COPY puntosv (codigo, nombre, ip, gid) FROM stdin;
\.


--
-- TOC entry 1658 (class 0 OID 16517)
-- Dependencies: 1282
-- Data for Name: transacciones; Type: TABLE DATA; Schema: public; Owner: -
--

COPY transacciones (codigo, nombre, driver, args) FROM stdin;
TR010	Actualizacion de Contraseña	com.kazak.smi.server.businesrules.PasswordExchange	<root>\n    <args>UPD0002</args>\n</root>
TR011	Confirmacion de mensaje	com.kazak.smi.server.businesrules.ConfirmMessage	<root>\n    <args>UPD0003</args>\n</root>
TR001	Adiciona usuarios	com.kazak.smi.server.businesrules.UserManager	<root>\n    <action>add</action>\n    <arg>INS0001</arg>\n    <arg>INS0002</arg>\n</root>
TR004	Adiciona  un grupo	com.kazak.smi.server.businesrules.GroupManager	<root>\n    <action>add</action>\n    <arg>INS0006</arg>\n</root>
TR005	Edita un grupo	com.kazak.smi.server.businesrules.GroupManager	<root>\n    <action>edit</action>\n    <arg>UPD0001</arg>\n</root>
TR006	Borra un grupo	com.kazak.smi.server.businesrules.GroupManager	<root>\n    <action>remove</action>\n    <arg>SEL0013</arg>\n    <arg>SEL0014</arg>\n    <arg>DEL0003</arg>\n</root>
TR007	Nuevo Punto de Venta	com.kazak.smi.server.businesrules.PointSaleManager	<root>\n    <action>add</action>\n    <arg>INS0007</arg>\n</root>
TR002	Edita Usuarios	com.kazak.smi.server.businesrules.UserManager	<root>\n    <action>edit</action>\n    <arg>UPD0004</arg>\n    <arg>DEL0001</arg>\n    <arg>INS0002</arg>\n</root>
TR008	Editar Punto de Venta	com.kazak.smi.server.businesrules.PointSaleManager	<root>\n    <action>edit</action>\n    <arg>UPD0006</arg>\n    <arg>UPD0007</arg>\n</root>
TR009	Borrar Punto de Venta	com.kazak.smi.server.businesrules.PointSaleManager	<root>\n    <action>remove</action>\n    <arg>DEL0004</arg>\n</root>
TR012	Sincronización de las bases de datos Oracle con PostgreSQL	com.kazak.smi.server.businesrules.Sync	<roo/>
TR003	Borra Usuarios	com.kazak.smi.server.businesrules.UserManager	<root>\n    <action>remove</action>\n    <arg>DEL0001</arg>\n    <arg>DEL0002</arg>\n</root>
TR013	Reporte de control de colocadores	com.kazak.smi.server.businesrules.MessageControlReporter	<root/>
\.


--
-- TOC entry 1659 (class 0 OID 16524)
-- Dependencies: 1284
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: -
--

COPY usuarios (uid, "login", clave, nombres, correo, "admin", habilitado, gid, audit) FROM stdin;
1	admin	827ccb0eea8a706c4c34a16891f84e7b	NOMBRE DE PRUEBA	NO TIENE EMAIL	t	t	6	f
\.


--
-- TOC entry 1660 (class 0 OID 16533)
-- Dependencies: 1285
-- Data for Name: usuarios_pventa; Type: TABLE DATA; Schema: public; Owner: -
--

COPY usuarios_pventa (uid, codigo_pventa, valid_ip) FROM stdin;
\.


--
-- TOC entry 1636 (class 2606 OID 16537)
-- Dependencies: 1278 1278
-- Name: grupos_nombre_grupo_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY grupos
    ADD CONSTRAINT grupos_nombre_grupo_key UNIQUE (nombre_grupo);


--
-- TOC entry 1638 (class 2606 OID 16539)
-- Dependencies: 1278 1278
-- Name: grupos_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY grupos
    ADD CONSTRAINT grupos_pkey PRIMARY KEY (gid);


--
-- TOC entry 1640 (class 2606 OID 16541)
-- Dependencies: 1280 1280
-- Name: mensajes_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY mensajes
    ADD CONSTRAINT mensajes_pkey PRIMARY KEY (mid);


--
-- TOC entry 1642 (class 2606 OID 16543)
-- Dependencies: 1281 1281
-- Name: puntosv_codigo_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY puntosv
    ADD CONSTRAINT puntosv_codigo_key UNIQUE (codigo);


--
-- TOC entry 1644 (class 2606 OID 16545)
-- Dependencies: 1281 1281
-- Name: puntosv_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY puntosv
    ADD CONSTRAINT puntosv_pkey PRIMARY KEY (codigo);


--
-- TOC entry 1646 (class 2606 OID 16547)
-- Dependencies: 1282 1282
-- Name: transacciones_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY transacciones
    ADD CONSTRAINT transacciones_pkey PRIMARY KEY (codigo);


--
-- TOC entry 1648 (class 2606 OID 16549)
-- Dependencies: 1284 1284
-- Name: usuarios_login_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_login_key UNIQUE ("login");


--
-- TOC entry 1650 (class 2606 OID 16551)
-- Dependencies: 1284 1284
-- Name: usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (uid);


--
-- TOC entry 1651 (class 2606 OID 16552)
-- Dependencies: 1278 1281 1637
-- Name: puntosv_gid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY puntosv
    ADD CONSTRAINT puntosv_gid_fkey FOREIGN KEY (gid) REFERENCES grupos(gid);


--
-- TOC entry 1652 (class 2606 OID 16557)
-- Dependencies: 1278 1284 1637
-- Name: usuarios_gid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_gid_fkey FOREIGN KEY (gid) REFERENCES grupos(gid);


--
-- TOC entry 1653 (class 2606 OID 16562)
-- Dependencies: 1641 1285 1281
-- Name: usuarios_pventa_codigo_pventa_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usuarios_pventa
    ADD CONSTRAINT usuarios_pventa_codigo_pventa_fkey FOREIGN KEY (codigo_pventa) REFERENCES puntosv(codigo) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 1654 (class 2606 OID 16567)
-- Dependencies: 1285 1284 1649
-- Name: usuarios_pventa_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY usuarios_pventa
    ADD CONSTRAINT usuarios_pventa_uid_fkey FOREIGN KEY (uid) REFERENCES usuarios(uid);


--
-- TOC entry 1665 (class 0 OID 0)
-- Dependencies: 5
-- Name: public; Type: ACL; Schema: -; Owner: -
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2007-03-29 17:13:12 COT

--
-- PostgreSQL database dump complete
--

