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
-- Name: grupos_gid_seq; Type: SEQUENCE; Schema: public; Owner: comeetadmin
--

CREATE SEQUENCE grupos_gid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.grupos_gid_seq OWNER TO comeetadmin;

--
-- Name: grupos_gid_seq; Type: SEQUENCE SET; Schema: public; Owner: comeetadmin
--

SELECT pg_catalog.setval('grupos_gid_seq', 62, true);


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: grupos; Type: TABLE; Schema: public; Owner: comeetadmin; Tablespace: 
--

CREATE TABLE grupos (
    gid integer DEFAULT nextval('grupos_gid_seq'::regclass) NOT NULL,
    nombre_grupo character varying(40) NOT NULL,
    mostrable boolean DEFAULT false NOT NULL,
    zona boolean DEFAULT false NOT NULL
);


ALTER TABLE public.grupos OWNER TO comeetadmin;

--
-- Name: mensajes_mid_seq; Type: SEQUENCE; Schema: public; Owner: comeetadmin
--

CREATE SEQUENCE mensajes_mid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.mensajes_mid_seq OWNER TO comeetadmin;

--
-- Name: mensajes_mid_seq; Type: SEQUENCE SET; Schema: public; Owner: comeetadmin
--

SELECT pg_catalog.setval('mensajes_mid_seq', 4181, true);


--
-- Name: mensajes; Type: TABLE; Schema: public; Owner: comeetadmin; Tablespace: 
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


ALTER TABLE public.mensajes OWNER TO comeetadmin;

--
-- Name: puntosv; Type: TABLE; Schema: public; Owner: comeetadmin; Tablespace: 
--

CREATE TABLE puntosv (
    codigo character(4) NOT NULL,
    nombre character varying(50) NOT NULL,
    ip inet DEFAULT '127.0.0.1'::inet,
    gid integer DEFAULT 5 NOT NULL
);


ALTER TABLE public.puntosv OWNER TO comeetadmin;

--
-- Name: transacciones; Type: TABLE; Schema: public; Owner: comeetadmin; Tablespace: 
--

CREATE TABLE transacciones (
    codigo character(5) NOT NULL,
    nombre character varying(100) NOT NULL,
    driver character varying(200) NOT NULL,
    args text NOT NULL
);


ALTER TABLE public.transacciones OWNER TO comeetadmin;

--
-- Name: usuarios_uid_seq; Type: SEQUENCE; Schema: public; Owner: comeetadmin
--

CREATE SEQUENCE usuarios_uid_seq
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.usuarios_uid_seq OWNER TO comeetadmin;

--
-- Name: usuarios_uid_seq; Type: SEQUENCE SET; Schema: public; Owner: comeetadmin
--

SELECT pg_catalog.setval('usuarios_uid_seq', 5222, true);


--
-- Name: usuarios; Type: TABLE; Schema: public; Owner: comeetadmin; Tablespace: 
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


ALTER TABLE public.usuarios OWNER TO comeetadmin;

--
-- Name: usuarios_pventa; Type: TABLE; Schema: public; Owner: comeetadmin; Tablespace: 
--

CREATE TABLE usuarios_pventa (
    uid integer NOT NULL,
    codigo_pventa character(4) NOT NULL,
    valid_ip boolean DEFAULT false NOT NULL
);


ALTER TABLE public.usuarios_pventa OWNER TO comeetadmin;

--
-- Data for Name: grupos; Type: TABLE DATA; Schema: public; Owner: comeetadmin
--

COPY grupos (gid, nombre_grupo, mostrable, zona) FROM stdin;
0	COMEET	f	f
1	SIN GRUPO	f	t
2	ADMINISTRATIVOS	f	f
3	GERENCIA	t	f
4	AUDITORIA	t	f
5	FALLAS EQUIPOS	t	f
6	NORTE	f	t
7	CENTRO	f	t
8	SUR	f	t
9	OCCIDENTE	f	t
\.


--
-- Data for Name: mensajes; Type: TABLE DATA; Schema: public; Owner: comeetadmin
--

COPY mensajes (mid, uid_destino, fecha, hora, asunto, texto, confirmado, valido, tiempo_vida, uid_origen, control, minutos, fecha_confirmacion) FROM stdin;
4146	18	2007-10-09	21:47:00	Aumento de Salario	Cordial saludo, por medio de la presente deseo solicitar mi aumento de salario. Gracias.	f	t	3	4	f	0	\N
4147	6	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	f	t	3	16	f	-1	\N
4148	8	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	f	t	3	16	f	-1	\N
4149	9	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	f	t	3	16	f	-1	\N
4151	5	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	f	t	3	16	f	-1	\N
4152	7	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	f	t	3	16	f	-1	\N
4153	13	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	f	t	3	16	f	-1	\N
4154	14	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	f	t	3	16	f	-1	\N
4155	15	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	f	t	3	16	f	-1	\N
4156	11	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	f	t	3	16	f	-1	\N
4157	10	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	f	t	3	16	f	-1	\N
4158	12	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	f	t	3	16	f	-1	\N
4159	4	2007-10-09	22:31:00	Mensaje exclusivo para un usuario	Por medio de la presente, quiero notificarle que estoy enviando un\r\nmensaje unico y exclusivo.\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	t	t	3	16	f	-1	2007-10-09 22:32:00
4150	4	2007-10-09	22:19:00	mensaje de prueba	Hola! este es un mensaje de prueba para todos los puestos de trabajo!	t	t	3	16	f	-1	2007-10-09 22:32:02
4160	4	2007-10-09	22:49:00	pilas con este mensaje de control:3	Si esta en su puesto, responda!!!! ya!!!\r\n\r\n\n====================================\nEste mensaje es de control	f	t	3	16	t	3	\N
4161	12	2007-10-09	22:49:00	pilas con este mensaje de control:3	Si esta en su puesto, responda!!!! ya!!!\r\n\r\n\n====================================\nEste mensaje es de control	t	t	3	16	t	3	2007-10-09 22:49:49
4162	16	2007-10-09	22:49:00	[RE: pilas con este mensaje de control:3]	Estoy en mi puesto!!!!	f	t	3	12	f	0	\N
4163	4	2007-10-10	09:23:00	Citacion a reunion	Buenos dias, por favor presentarse a la sede principal para reunion de\r\ncontrol. \r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	t	t	3	16	f	-1	2007-10-10 09:23:52
4164	6	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4165	8	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4166	9	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4168	5	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4169	7	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4170	13	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4171	14	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4172	15	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4174	10	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4175	11	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4173	12	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	t	t	3	16	f	-1	2007-10-10 09:26:36
4167	4	2007-10-10	09:26:00	Informacion Urgente	Por favor, no vender mas a partir de las 4 el sistema sera bajado para\r\nmantenimiento. Gracias.\r\n\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	t	t	3	16	f	-1	2007-10-10 09:26:40
4177	5	2007-10-10	09:27:00	Citacion	Por favor todas las sedes del norte, asistir a reunion el proximo\r\nsabado.\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4178	7	2007-10-10	09:27:00	Citacion	Por favor todas las sedes del norte, asistir a reunion el proximo\r\nsabado.\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	f	t	3	16	f	-1	\N
4176	4	2007-10-10	09:27:00	Citacion	Por favor todas las sedes del norte, asistir a reunion el proximo\r\nsabado.\r\n-- \r\nGustavo Gonzalez <gustavo@localhost>	t	t	3	16	f	-1	2007-10-10 09:27:58
4179	16	2007-10-10	09:29:00	Aumento de sueldo	Por favor solicito se revise mi caso... bla bla bla	f	t	3	4	f	0	\N
4181	12	2007-10-10	09:31:00	reportarse:2	Por favor, responder para verificar que esta en el puesto.\r\n\r\n\n====================================\nEste mensaje es de control	f	t	3	16	t	2	\N
4180	4	2007-10-10	09:31:00	reportarse:2	Por favor, responder para verificar que esta en el puesto.\r\n\r\n\n====================================\nEste mensaje es de control	t	t	3	16	t	2	2007-10-10 09:31:51
\.


--
-- Data for Name: puntosv; Type: TABLE DATA; Schema: public; Owner: comeetadmin
--

COPY puntosv (codigo, nombre, ip, gid) FROM stdin;
0000	Portal Norte	0.0.0.0	6
0001	Alcala	0.0.0.0	6
0002	La Campiña	0.0.0.0	6
0003	El Campin	0.0.0.0	7
0004	Chapinero Centro	0.0.0.0	7
0005	La Candelaria	0.0.0.0	7
0006	Usme	0.0.0.0	8
0007	Quiroga	0.0.0.0	8
0008	Olaya	0.0.0.0	8
0009	Modelia	0.0.0.0	9
0010	Salitre	0.0.0.0	9
0011	Zona Franca	0.0.0.0	9
\.


--
-- Data for Name: transacciones; Type: TABLE DATA; Schema: public; Owner: comeetadmin
--

COPY transacciones (codigo, nombre, driver, args) FROM stdin;
TR001	Adiciona usuarios	com.kazak.comeet.server.businessrules.UserManager	<root>\n    <action>add</action>\n    <arg>INS0001</arg>\n    <arg>INS0002</arg>\n</root>
TR002	Edita Usuarios	com.kazak.comeet.server.businessrules.UserManager	<root>\n    <action>edit</action>\n    <arg>UPD0004</arg>\n    <arg>DEL0001</arg>\n    <arg>INS0002</arg>\n</root>
TR003	Borra Usuarios	com.kazak.comeet.server.businessrules.UserManager	<root>\n    <action>remove</action>\n    <arg>DEL0001</arg>\n    <arg>DEL0002</arg>\n</root>
TR004	Adiciona  un grupo	com.kazak.comeet.server.businessrules.GroupManager	<root>\n    <action>add</action>\n    <arg>INS0006</arg>\n</root>
TR005	Edita un grupo	com.kazak.comeet.server.businessrules.GroupManager	<root>\n    <action>edit</action>\n    <arg>UPD0001</arg>\n</root>
TR006	Borra un grupo	com.kazak.comeet.server.businessrules.GroupManager	<root>\n    <action>remove</action>\n    <arg>SEL0013</arg>\n    <arg>SEL0014</arg>\n    <arg>DEL0003</arg>\n</root>
TR007	Nuevo Punto de Venta	com.kazak.comeet.server.businessrules.PointOfSaleManager	<root>\n    <action>add</action>\n    <arg>INS0007</arg>\n</root>
TR008	Editar Punto de Venta	com.kazak.comeet.server.businessrules.PointOfSaleManager	<root>\n    <action>edit</action>\n    <arg>UPD0006</arg>\n    <arg>UPD0007</arg>\n    <arg>UPD0008</arg>\n</root>
TR009	Borrar Punto de Venta	com.kazak.comeet.server.businessrules.PointOfSaleManager	<root>\n    <action>remove</action>\n    <arg>DEL0004</arg>\n</root>
TR010	Actualizacion de ContraseÃ±a	com.kazak.comeet.server.businessrules.PasswordExchanger	<root>\n<args>UPD0002</args>\n</root>
TR011	Confirmacion de mensaje	com.kazak.comeet.server.businessrules.MessageConfirmer	<root>\n    <args>UPD0003</args>\n</root>
TR012	Sincronización de las bases de datos Oracle con PostgreSQL	com.kazak.comeet.server.businessrules.SyncManager	<root/>
\.


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: comeetadmin
--

COPY usuarios (uid, "login", clave, nombres, correo, "admin", habilitado, gid, audit) FROM stdin;
2	auditor	827ccb0eea8a706c4c34a16891f84e7b	Usuario Auditor	auditor@localhost	f	t	1	t
1	admin	827ccb0eea8a706c4c34a16891f84e7b	Usuario Administrador	admin@localhost	t	t	1	f
3	comeetadmin	827ccb0eea8a706c4c34a16891f84e7b	Usuario CoMeet	comeet@localhost	t	t	0	f
4	CV0001	827ccb0eea8a706c4c34a16891f84e7b	Carolina Perez		f	t	6	f
5	CV0002	827ccb0eea8a706c4c34a16891f84e7b	Adriana Medina		f	t	6	f
6	CV0003	827ccb0eea8a706c4c34a16891f84e7b	Jorge Triana		f	t	7	f
7	CV0004	827ccb0eea8a706c4c34a16891f84e7b	Joanna Arango		f	t	6	f
8	CV0005	827ccb0eea8a706c4c34a16891f84e7b	Pedro Garcia		f	t	7	f
9	CV0006	827ccb0eea8a706c4c34a16891f84e7b	Jose Viena		f	t	7	f
10	CV0007	827ccb0eea8a706c4c34a16891f84e7b	Daniel Rios		f	t	8	f
11	CV0008	827ccb0eea8a706c4c34a16891f84e7b	Andres Naranjo		f	t	8	f
12	CV0009	827ccb0eea8a706c4c34a16891f84e7b	Jairo Perez		f	t	8	f
13	CV0010	827ccb0eea8a706c4c34a16891f84e7b	Mario Andrade		f	t	9	f
14	CV0011	827ccb0eea8a706c4c34a16891f84e7b	Marisol Correa		f	t	9	f
15	CV0012	827ccb0eea8a706c4c34a16891f84e7b	Joaquin Aranda		f	t	9	f
16	gustavo	827ccb0eea8a706c4c34a16891f84e7b	Gustavo Gonzalez	gustavo@localhost	t	t	5	t
17	gustavo2	827ccb0eea8a706c4c34a16891f84e7b	Gustavo Gonzalez	gustavo@localhost	t	t	2	t
18	gustavo3	827ccb0eea8a706c4c34a16891f84e7b	Gustavo Gonzalez	gustavo@localhost	t	t	3	t
\.


--
-- Data for Name: usuarios_pventa; Type: TABLE DATA; Schema: public; Owner: comeetadmin
--

COPY usuarios_pventa (uid, codigo_pventa, valid_ip) FROM stdin;
4	0000	f
5	0001	f
6	0002	f
7	0003	f
8	0004	f
9	0005	f
10	0006	f
11	0007	f
12	0008	f
13	0009	f
13	0010	f
13	0011	f
\.


--
-- Name: grupos_nombre_grupo_key; Type: CONSTRAINT; Schema: public; Owner: comeetadmin; Tablespace: 
--

ALTER TABLE ONLY grupos
    ADD CONSTRAINT grupos_nombre_grupo_key UNIQUE (nombre_grupo);


--
-- Name: grupos_pkey; Type: CONSTRAINT; Schema: public; Owner: comeetadmin; Tablespace: 
--

ALTER TABLE ONLY grupos
    ADD CONSTRAINT grupos_pkey PRIMARY KEY (gid);


--
-- Name: mensajes_pkey; Type: CONSTRAINT; Schema: public; Owner: comeetadmin; Tablespace: 
--

ALTER TABLE ONLY mensajes
    ADD CONSTRAINT mensajes_pkey PRIMARY KEY (mid);


--
-- Name: puntosv_codigo_key; Type: CONSTRAINT; Schema: public; Owner: comeetadmin; Tablespace: 
--

ALTER TABLE ONLY puntosv
    ADD CONSTRAINT puntosv_codigo_key UNIQUE (codigo);


--
-- Name: puntosv_pkey; Type: CONSTRAINT; Schema: public; Owner: comeetadmin; Tablespace: 
--

ALTER TABLE ONLY puntosv
    ADD CONSTRAINT puntosv_pkey PRIMARY KEY (codigo);


--
-- Name: transacciones_pkey; Type: CONSTRAINT; Schema: public; Owner: comeetadmin; Tablespace: 
--

ALTER TABLE ONLY transacciones
    ADD CONSTRAINT transacciones_pkey PRIMARY KEY (codigo);


--
-- Name: usuarios_login_key; Type: CONSTRAINT; Schema: public; Owner: comeetadmin; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_login_key UNIQUE ("login");


--
-- Name: usuarios_pkey; Type: CONSTRAINT; Schema: public; Owner: comeetadmin; Tablespace: 
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_pkey PRIMARY KEY (uid);


--
-- Name: puntosv_gid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: comeetadmin
--

ALTER TABLE ONLY puntosv
    ADD CONSTRAINT puntosv_gid_fkey FOREIGN KEY (gid) REFERENCES grupos(gid);


--
-- Name: usuarios_gid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: comeetadmin
--

ALTER TABLE ONLY usuarios
    ADD CONSTRAINT usuarios_gid_fkey FOREIGN KEY (gid) REFERENCES grupos(gid);


--
-- Name: usuarios_pventa_codigo_pventa_fkey; Type: FK CONSTRAINT; Schema: public; Owner: comeetadmin
--

ALTER TABLE ONLY usuarios_pventa
    ADD CONSTRAINT usuarios_pventa_codigo_pventa_fkey FOREIGN KEY (codigo_pventa) REFERENCES puntosv(codigo) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: usuarios_pventa_uid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: comeetadmin
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

