--
-- PostgreSQL database dump
--

SET client_encoding = 'LATIN1';
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

--
-- Name: grupos_gid_seq; Type: SEQUENCE; Schema: public; Owner: smi
--

CREATE SEQUENCE grupos_gid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.grupos_gid_seq OWNER TO smi;

--
-- Name: grupos_gid_seq; Type: SEQUENCE SET; Schema: public; Owner: smi
--

SELECT pg_catalog.setval('grupos_gid_seq', 62, true);


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: grupos; Type: TABLE; Schema: public; Owner: smi; Tablespace: 
--

CREATE TABLE grupos (
    gid integer DEFAULT nextval('grupos_gid_seq'::regclass) NOT NULL,
    nombre_grupo character varying(40) NOT NULL,
    mostrable boolean DEFAULT false NOT NULL,
    zona boolean DEFAULT false NOT NULL
);


ALTER TABLE public.grupos OWNER TO smi;

--
-- Name: mensajes_mid_seq; Type: SEQUENCE; Schema: public; Owner: smi
--

CREATE SEQUENCE mensajes_mid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.mensajes_mid_seq OWNER TO smi;

--
-- Name: mensajes_mid_seq; Type: SEQUENCE SET; Schema: public; Owner: smi
--

SELECT pg_catalog.setval('mensajes_mid_seq', 4145, true);


--
-- Name: mensajes; Type: TABLE; Schema: public; Owner: smi; Tablespace: 
--

CREATE TABLE mensajes (
    mid integer DEFAULT nextval('mensajes_mid_seq'::regclass) NOT NULL,
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


ALTER TABLE public.mensajes OWNER TO smi;

--
-- Name: puntosv; Type: TABLE; Schema: public; Owner: smi; Tablespace: 
--

CREATE TABLE puntosv (
    codigo character(4) NOT NULL,
    nombre character varying(50) NOT NULL,
    ip inet DEFAULT '127.0.0.1'::inet,
    gid integer DEFAULT 5 NOT NULL
);


ALTER TABLE public.puntosv OWNER TO smi;

--
-- Name: transacciones; Type: TABLE; Schema: public; Owner: smi; Tablespace: 
--

CREATE TABLE transacciones (
    codigo character(5) NOT NULL,
    nombre character varying(100) NOT NULL,
    driver character varying(200) NOT NULL,
    args text NOT NULL
);


ALTER TABLE public.transacciones OWNER TO smi;

--
-- Name: usuarios_uid_seq; Type: SEQUENCE; Schema: public; Owner: smi
--

CREATE SEQUENCE usuarios_uid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.usuarios_uid_seq OWNER TO smi;

--
-- Name: usuarios_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: smi
--

SELECT pg_catalog.setval('usuarios_uid_seq', 5222, true);


--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: smi; Tablespace: 
--

CREATE TABLE usuarios (
    uid integer DEFAULT nextval('usuarios_uid_seq'::regclass) NOT NULL,
    "login" character varying(30) NOT NULL,
    clave character(32) DEFAULT 'd41d8cd98f00b204e9800998ecf8427e'::bpchar NOT NULL,
    nombres character varying(100) DEFAULT 'Sin Nombre'::character varying,
    correo character varying(200),
    "admin" boolean DEFAULT false NOT NULL,
    habilitado boolean DEFAULT true NOT NULL,
    gid integer DEFAULT 5,
    audit boolean DEFAULT false NOT NULL
);


ALTER TABLE public.usuarios OWNER TO smi;

--
-- Name: usuarios_pventa; Type: TABLE; Schema: public; Owner: smi; Tablespace: 
--

CREATE TABLE usuarios_pventa (
    uid integer NOT NULL,
    codigo_pventa character(4) NOT NULL,
    valid_ip boolean DEFAULT false NOT NULL
);


ALTER TABLE public.usuarios_pventa OWNER TO smi;

--
-- Data for Name: grupos; Type: TABLE DATA; Schema: public; Owner: smi
--

COPY grupos (gid, nombre_grupo, mostrable, zona) FROM stdin;
0	SMI	f	f
1	SIN GRUPO	f	t
2	ADMINISTRATIVOS	f	f
3	GERENCIA	t	f
4	AUDITORIA	t	f
5	FALLAS EQUIPOS	t	f
\.


--
-- Data for Name: mensajes; Type: TABLE DATA; Schema: public; Owner: smi
--

COPY mensajes (mid, uid_destino, fecha, hora, asunto, texto, confirmado, valido, tiempo_vida, uid_origen, control, minutos, fecha_confirmacion) FROM stdin;
\.


--
-- Data for Name: puntosv; Type: TABLE DATA; Schema: public; Owner: smi
--

COPY puntosv (codigo, nombre, ip, gid) FROM stdin;
\.


--
-- Data for Name: transacciones; Type: TABLE DATA; Schema: public; Owner: smi
--

COPY transacciones (codigo, nombre, driver, args) FROM stdin;
TR001	Adiciona usuarios	com.kazak.smi.server.businessrules.UserManager	<root>\n    <action>add</action>\n    <arg>INS0001</arg>\n    <arg>INS0002</arg>\n</root>
TR004	Adiciona  un grupo	com.kazak.smi.server.businessrules.GroupManager	<root>\n    <action>add</action>\n    <arg>INS0006</arg>\n</root>
TR005	Edita un grupo	com.kazak.smi.server.businessrules.GroupManager	<root>\n    <action>edit</action>\n    <arg>UPD0001</arg>\n</root>
TR006	Borra un grupo	com.kazak.smi.server.businessrules.GroupManager	<root>\n    <action>remove</action>\n    <arg>SEL0013</arg>\n    <arg>SEL0014</arg>\n    <arg>DEL0003</arg>\n</root>
TR002	Edita Usuarios	com.kazak.smi.server.businessrules.UserManager	<root>\n    <action>edit</action>\n    <arg>UPD0004</arg>\n    <arg>DEL0001</arg>\n    <arg>INS0002</arg>\n</root>
TR003	Borra Usuarios	com.kazak.smi.server.businessrules.UserManager	<root>\n    <action>remove</action>\n    <arg>DEL0001</arg>\n    <arg>DEL0002</arg>\n</root>
TR010	Actualizacion de ContraseÃ±a	com.kazak.smi.server.businessrules.PasswordExchanger	<root>\n<args>UPD0002</args>\n</root>
TR009	Borrar Punto de Venta	com.kazak.smi.server.businessrules.PointOfSaleManager	<root>\n    <action>remove</action>\n    <arg>DEL0004</arg>\n</root>
TR011	Confirmacion de mensaje	com.kazak.smi.server.businessrules.MessageConfirmer	<root>\n    <args>UPD0003</args>\n</root>
TR012	Sincronización de las bases de datos Oracle con PostgreSQL	com.kazak.smi.server.businessrules.SyncManager	<root/>
TR007	Nuevo Punto de Venta	com.kazak.smi.server.businessrules.PointOfSaleManager	<root>\n    <action>add</action>\n    <arg>INS0007</arg>\n</root>
TR008	Editar Punto de Venta	com.kazak.smi.server.businessrules.PointOfSaleManager	<root>\n    <action>edit</action>\n    <arg>UPD0006</arg>\n    <arg>UPD0007</arg>\n    <arg>UPD0008</arg>\n</root>
\.


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: smi
--

COPY usuarios (uid, "login", clave, nombres, correo, "admin", habilitado, gid, audit) FROM stdin;
2	auditor	827ccb0eea8a706c4c34a16891f84e7b	Usuario Auditor	auditor@localhost	f	t	1	t
1	admin	827ccb0eea8a706c4c34a16891f84e7b	Usuario Administrador	admin@localhost	t	t	1	f
3	smi	827ccb0eea8a706c4c34a16891f84e7b	Usuario SMI	smi@localhost	t	t	0	f
\.


--
-- Data for Name: usuarios_pventa; Type: TABLE DATA; Schema: public; Owner: smi
--

COPY usuarios_pventa (uid, codigo_pventa, valid_ip) FROM stdin;
\.


--
-- Name: grupos_nombre_grupo_key; Type: CONSTRAINT; Schema: public; Owner: smi; Tablespace: 
--

ALTER TABLE ONLY grupos
    ADD CONSTRAINT grupos_nombre_grupo_key UNIQUE (nombre_grupo);


--
-- Name: grupos_pkey; Type: CONSTRAINT; Schema: public; Owner: smi; Tablespace: 
--

ALTER TABLE ONLY grupos
    ADD CONSTRAINT grupos_pkey PRIMARY KEY (gid);


--
-- Name: mensajes_pkey; Type: CONSTRAINT; Schema: public; Owner: smi; Tablespace: 
--

ALTER TABLE ONLY mensajes
    ADD CONSTRAINT mensajes_pkey PRIMARY KEY (mid);


--
-- Name: puntosv_codigo_key; Type: CONSTRAINT; Schema: public; Owner: smi; Tablespace: 
--

ALTER TABLE ONLY puntosv
    ADD CONSTRAINT puntosv_codigo_key UNIQUE (codigo);


--
-- Name: puntosv_pkey; Type: CONSTRAINT; Schema: public; Owner: smi; Tablespace: 
--

ALTER TABLE ONLY puntosv
    ADD CONSTRAINT puntosv_pkey PRIMARY KEY (codigo);


--
-- Name: transacciones_pkey; Type: CONSTRAINT; Schema: public; Owner: smi; Tablespace: 
--

ALTER TABLE ONLY transacciones
    ADD CONSTRAINT transacciones_pkey PRIMARY KEY (codigo);


--
-- Name: usuarios_login_key; Type: CONSTRAINT; Schema: public; Owner: smi; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_login_key UNIQUE ("login");


--
-- Name: usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: smi; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (uid);


--
-- Name: puntosv_gid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: smi
--

ALTER TABLE ONLY puntosv
    ADD CONSTRAINT puntosv_gid_fkey FOREIGN KEY (gid) REFERENCES grupos(gid);


--
-- Name: usuarios_gid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: smi
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_gid_fkey FOREIGN KEY (gid) REFERENCES grupos(gid);


--
-- Name: usuarios_pventa_codigo_pventa_fkey; Type: FK CONSTRAINT; Schema: public; Owner: smi
--

ALTER TABLE ONLY usuarios_pventa
    ADD CONSTRAINT usuarios_pventa_codigo_pventa_fkey FOREIGN KEY (codigo_pventa) REFERENCES puntosv(codigo) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: usuarios_pventa_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: smi
--

ALTER TABLE ONLY usuarios_pventa
    ADD CONSTRAINT usuarios_pventa_uid_fkey FOREIGN KEY (uid) REFERENCES usuarios(uid);


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

