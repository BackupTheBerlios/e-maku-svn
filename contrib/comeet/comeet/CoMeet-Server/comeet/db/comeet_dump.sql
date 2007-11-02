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

SELECT pg_catalog.setval('grupos_gid_seq', 65, true);


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

SELECT pg_catalog.setval('mensajes_mid_seq', 4296, true);


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

SELECT pg_catalog.setval('usuarios_uid_seq', 5238, true);


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
1	SIN GRUPO	f	t
2	ADMINISTRATIVOS	f	f
3	GERENCIA	t	f
4	AUDITORIA	t	f
5	FALLAS EQUIPOS	t	f
6	NORTE	f	t
7	CENTRO	f	t
8	SUR	f	t
9	OCCIDENTE	f	t
65	CORABASTOS	f	t
0	COMEET	f	f
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
4182	16	2007-10-31	11:02:00	Prueba de Concepto	Revisando...	f	t	3	4	f	0	\N
4183	16	2007-10-31	11:07:00	Prueba de Concepto II	Let's see what is going on...	f	t	3	4	f	0	\N
4184	16	2007-10-31	11:10:00	Prueba de Concepto	Este es un ensayo!	f	t	3	4	f	0	\N
4185	16	2007-10-31	11:14:00	Prueba de Concepto	Enviando...	f	t	3	4	f	0	\N
4186	16	2007-10-31	11:40:00	Mensaje de Prueba	Este es un mensaje de prueba!	f	t	3	4	f	0	\N
4187	16	2007-10-31	11:53:00	Prueba	prueba	f	t	3	4	f	0	\N
4188	16	2007-10-31	12:04:00	Prueba!	Ok Computer	f	t	3	4	f	0	\N
4189	16	2007-10-31	12:09:00	Prueba X	Prueba X	f	t	3	4	f	0	\N
4190	16	2007-10-31	12:10:00	Prueba	Pollito!	f	t	3	4	f	0	\N
4191	16	2007-10-31	12:16:00	Prueba	Prueba X	f	t	3	4	f	0	\N
4192	16	2007-10-31	12:18:00	Prueba X	Prueba	f	t	3	4	f	0	\N
4193	16	2007-10-31	12:20:00	Mensaje de Prueba	Prueba X	f	t	3	4	f	0	\N
4194	16	2007-10-31	12:22:00	Sigamos intentando!	Cheto?	f	t	3	4	f	0	\N
4195	16	2007-10-31	12:35:00	Otra prueba	a ver si si	f	t	3	4	f	0	\N
4196	4	2007-10-31	12:36:00	Otra prueba	El mié, 31-10-2007 a las 12:35 -0500, comeet@saludtotal.com escribió:\r\n> Carolina Perez Pum escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> a ver si si\r\n> --------------------------------------------\r\n> .\r\n> \r\nDale a ver!	t	t	3	16	f	-1	2007-10-31 12:36:56
4197	4	2007-10-31	12:48:00	Solicitud!	Por favor!	t	t	3	16	f	-1	2007-10-31 12:48:50
4198	4	2007-10-31	12:57:00	prueba	Ja!	t	t	3	16	f	-1	2007-10-31 12:57:58
4199	4	2007-10-31	12:58:00	Sigamos intentando!	De que diablos me hablas?\r\n\r\nEl mié, 31-10-2007 a las 12:22 -0500, comeet@saludtotal.com escribió:\r\n> Carolina Perez Pum escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> Cheto?\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-10-31 12:58:19
4200	16	2007-10-31	13:05:00	Prueba!!!	Si o no?	f	t	3	4	f	0	\N
4201	16	2007-10-31	13:07:00	Probando	a ver que pasa!	f	t	3	4	f	0	\N
4202	16	2007-10-31	13:12:00	Hola	Si o no?	f	t	3	4	f	0	\N
4203	4	2007-10-31	13:37:00	mensaje de prueba	Intentemos uno mas!	t	t	3	16	f	-1	2007-10-31 13:37:49
4204	16	2007-10-31	13:37:00	Tratemos	otra vez!	f	t	3	4	f	0	\N
4205	6	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4206	8	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4207	9	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4208	5	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4209	7	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4210	13	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4211	14	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4212	15	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4214	5234	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4215	5227	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4216	5228	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4217	5229	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4218	10	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4219	11	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4220	12	2007-10-31	13:38:00	probando!	a que no???	f	t	3	16	f	-1	\N
4213	4	2007-10-31	13:38:00	probando!	a que no???	t	t	3	16	f	-1	2007-10-31 13:38:46
4221	6	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4222	8	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4223	9	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4224	5	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4225	7	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4226	13	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4227	14	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4228	15	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4230	5234	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4231	5227	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4232	5228	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4233	5229	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4234	10	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4235	11	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4236	12	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	f	t	3	16	f	-1	\N
4229	4	2007-10-31	13:41:00	a que no adivinan???	estoy de vuelta!!! mujajajaja!	t	t	3	16	f	-1	2007-10-31 13:41:18
4237	4	2007-10-31	13:41:00	mensaje privado	solo para ti!	t	t	3	16	f	-1	2007-10-31 13:41:50
4238	4	2007-10-31	13:44:00	probando otra vez	a que no?	t	t	3	16	f	-1	2007-10-31 13:44:49
4239	16	2007-10-31	13:49:00	Miremo si llega	o no?	f	t	3	4	f	0	\N
4240	6	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4241	8	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4242	9	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4243	5	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4244	7	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4245	13	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4246	14	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4247	15	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4249	5234	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4250	5227	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4251	5228	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4252	5229	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4253	10	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4254	11	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4255	12	2007-10-31	13:50:00	evacuacion!	a que no???	f	t	3	16	f	-1	\N
4248	4	2007-10-31	13:50:00	evacuacion!	a que no???	t	t	3	16	f	-1	2007-10-31 13:50:07
4256	4	2007-10-31	13:52:00	probando para ver	si llega!	t	t	3	16	f	-1	2007-10-31 13:52:38
4257	16	2007-10-31	13:53:00	Probando desde	Bogota!	f	t	3	4	f	0	\N
4258	6	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4259	8	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4260	9	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4261	5	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4262	7	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4263	13	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4264	14	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4265	15	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4267	5234	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4268	5227	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4269	5228	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4270	5229	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4271	10	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4272	11	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4273	12	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	f	t	3	16	f	-1	\N
4266	4	2007-10-31	13:54:00	aviso urgente	para tener en cuenta!	t	t	3	16	f	-1	2007-10-31 13:54:39
4274	16	2007-10-31	13:54:00	[RE:aviso urgente]	de que se trata?	f	t	3	4	f	0	\N
4275	4	2007-10-31	13:55:00	probando!!!	a que no te la crees?	t	t	3	16	f	-1	2007-10-31 13:55:28
4276	16	2007-10-31	13:57:00	Sigamos probando	o que no?	f	t	3	4	f	0	\N
4277	4	2007-10-31	14:02:00	Sigamos probando	Ultima bola!\r\n\r\nEl mié, 31-10-2007 a las 13:57 -0500, comeet@saludtotal.com escribió:\r\n> Carolina Perez Pum escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> o que no?\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-10-31 14:02:14
4278	16	2007-10-31	23:00:00	Hola amigo	desde aca! ja!	f	t	3	5238	f	0	\N
4279	16	2007-10-31	23:15:00	Probando	A ver si llega	f	t	3	5238	f	0	\N
4280	5238	2007-10-31	23:28:00	Probando	Ok computer!\r\n\r\nEl mié, 31-10-2007 a las 23:15 -0500, comeet@saludtotal.com escribió:\r\n> Durango Palomino escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> A ver si llega\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-10-31 23:28:49
4281	16	2007-11-02	09:01:00	Bullet proof	I wish i was	f	t	3	5235	f	0	\N
4283	16	2007-11-02	09:09:00	Mensaje de Prueba	Ja!	f	t	3	5235	f	0	\N
4286	16	2007-11-02	09:13:00	Ensayando!	A ver si si!	f	t	3	5235	f	0	\N
4287	5235	2007-11-02	09:15:00	Ensayando!	No esta ni tibio!!!\r\n\r\nEl vie, 02-11-2007 a las 09:13 -0500, comeet@saludtotal.com escribió:\r\n> Diego Rodriguez escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> A ver si si!\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-11-02 09:15:13
4284	5235	2007-11-02	09:13:00	Mensaje de Prueba	Prueba!!!\r\n\r\nEl vie, 02-11-2007 a las 09:09 -0500, comeet@saludtotal.com escribió:\r\n> Diego Rodriguez escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> Ja!\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-11-02 09:15:14
4285	5235	2007-11-02	09:13:00	Bullet proof	Plop!\r\n\r\nEl vie, 02-11-2007 a las 09:01 -0500, comeet@saludtotal.com escribió:\r\n> Diego Rodriguez escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> I wish i was\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-11-02 09:15:14
4282	5235	2007-11-02	09:08:00	Bullet proof	El vie, 02-11-2007 a las 09:01 -0500, comeet@saludtotal.com escribió:\r\n> Diego Rodriguez escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> I wish i was\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-11-02 09:15:19
4288	5235	2007-11-02	09:16:00	Ensayando!	El vie, 02-11-2007 a las 09:13 -0500, comeet@saludtotal.com escribió:\r\n> Diego Rodriguez escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> A ver si si!\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-11-02 09:16:44
4289	16	2007-11-02	09:16:00	Prueba 13	pa que afine!	f	t	3	5235	f	0	\N
4290	5235	2007-11-02	09:17:00	Prueba 13	Pa las que sean papa!\r\n\r\nEl vie, 02-11-2007 a las 09:16 -0500, comeet@saludtotal.com escribió:\r\n> Diego Rodriguez escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> pa que afine!\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-11-02 09:17:23
4291	5235	2007-11-02	09:17:00	Mensaje de Prueba	No falle!\r\n\r\nEl vie, 02-11-2007 a las 09:09 -0500, comeet@saludtotal.com escribió:\r\n> Diego Rodriguez escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> Ja!\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-11-02 09:17:43
4292	5235	2007-11-02	09:18:00	Bullet proof	Por que tan salsa?\r\n\r\nEl vie, 02-11-2007 a las 09:01 -0500, comeet@saludtotal.com escribió:\r\n> Diego Rodriguez escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> I wish i was\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-11-02 09:18:04
4293	16	2007-11-02	09:18:00	Care pop	Ya veremos!	f	t	3	5235	f	0	\N
4294	5235	2007-11-02	09:18:00	Care pop	U2!\r\n\r\nEl vie, 02-11-2007 a las 09:18 -0500, comeet@saludtotal.com escribió:\r\n> Diego Rodriguez escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> Ya veremos!\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-11-02 09:18:44
4295	16	2007-11-02	09:20:00	Probando	123	f	t	3	5235	f	0	\N
4296	5235	2007-11-02	09:21:00	Probando	Houston! i copy that!\r\n\r\nEl vie, 02-11-2007 a las 09:20 -0500, comeet@saludtotal.com escribió:\r\n> Diego Rodriguez escribio desde PORVENIR:\r\n> --------------------------------------------\r\n> 123\r\n> --------------------------------------------\r\n> .\r\n>	t	t	3	16	f	-1	2007-11-02 09:21:06
\.


--
-- Data for Name: puntosv; Type: TABLE DATA; Schema: public; Owner: comeetadmin
--

COPY puntosv (codigo, nombre, ip, gid) FROM stdin;
19  	POLLITO	12.12.1.2	8
0000	PORTAL NORTE	0.0.0.0	6
0003	EL CAMPIN	0.0.0.0	7
0004	CHAPINERO CENTRO	0.0.0.0	7
0005	LA CANDELARIA	0.0.0.0	7
0006	USME	0.0.0.0	8
0007	QUIROGA	0.0.0.0	8
0008	OLAYA	0.0.0.0	8
0009	MODELIA	0.0.0.0	9
0010	SALITRE	0.0.0.0	9
0011	ZONA FRANCA	0.0.0.0	9
14  	POLICIA II	100.100.100.1	7
15  	PORVENIR	127.0.0.1	7
16  	PUNTO X	12.12.12.0	6
0001	ALCALA	0.0.0.3	6
17  	OTRO PUNTO	0.0.0.1	8
18  	NUEVO PUNTO	0.0.0.1	8
0002	LA CAMPINA	0.0.0.1	6
20  	EL ELEFANTE	0.0.0.4	8
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
TR009	Borrar Punto de Venta	com.kazak.comeet.server.businessrules.PointOfSaleManager	<root>\n    <action>remove</action>\n    <arg>DEL0004</arg>\n</root>
TR010	Actualizacion de ContraseÃ±a	com.kazak.comeet.server.businessrules.PasswordExchanger	<root>\n<args>UPD0002</args>\n</root>
TR011	Confirmacion de mensaje	com.kazak.comeet.server.businessrules.MessageConfirmer	<root>\n    <args>UPD0003</args>\n</root>
TR012	Sincronización de las bases de datos Oracle con PostgreSQL	com.kazak.comeet.server.businessrules.SyncManager	<root/>
TR008	Editar Punto de Venta	com.kazak.comeet.server.businessrules.PointOfSaleManager	<root>\n    <action>edit</action>\n    <arg>UPD0005</arg>\n</root>
\.


--
-- Data for Name: usuarios; Type: TABLE DATA; Schema: public; Owner: comeetadmin
--

COPY usuarios (uid, "login", clave, nombres, correo, "admin", habilitado, gid, audit) FROM stdin;
3	comeetadmin	827ccb0eea8a706c4c34a16891f84e7b	Usuario CoMeet	comeet@localhost	t	t	0	f
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
17	gustavo2	827ccb0eea8a706c4c34a16891f84e7b	Gustavo Gonzalez	gustavo@localhost	t	t	2	t
18	gustavo3	827ccb0eea8a706c4c34a16891f84e7b	Gustavo Gonzalez	gustavo@localhost	t	t	3	t
5227	CV0090	827ccb0eea8a706c4c34a16891f84e7b	Guz		f	t	1	f
5228	CV0092	827ccb0eea8a706c4c34a16891f84e7b	Proof		f	t	1	f
5229	CV0098	827ccb0eea8a706c4c34a16891f84e7b	Proof 2		f	t	1	f
5232	support	827ccb0eea8a706c4c34a16891f84e7b	Support User	support@localhost	t	t	0	f
5233	pollito	827ccb0eea8a706c4c34a16891f84e7b	Pollito Gallina	pollito@localhost	t	t	4	f
5234	CV0070	827ccb0eea8a706c4c34a16891f84e7b	Vendedor X		f	t	1	f
2	auditor	827ccb0eea8a706c4c34a16891f84e7b	Usuario Auditor	auditor@localhost	f	t	4	t
1	admin	21232f297a57a5a743894a0e4a801fc3	Usuario Administrador	admin@localhost	t	t	2	f
16	gustavo	827ccb0eea8a706c4c34a16891f84e7b	Gustavo Gonzalez G	gustavo@saludtotal.com	t	t	5	f
5235	DiegoR	827ccb0eea8a706c4c34a16891f84e7b	Diego Rodriguez		f	t	1	f
5236	pCastro	827ccb0eea8a706c4c34a16891f84e7b	Pablo Castro		f	t	1	f
4	CV0001	21232f297a57a5a743894a0e4a801fc3	Carolina Perez Pum		f	t	1	f
5237	canciller	827ccb0eea8a706c4c34a16891f84e7b	Canciller X	canciller@saludtotal.com	f	t	4	t
5238	DurangoP	827ccb0eea8a706c4c34a16891f84e7b	Durango Palomino		f	t	1	f
\.


--
-- Data for Name: usuarios_pventa; Type: TABLE DATA; Schema: public; Owner: comeetadmin
--

COPY usuarios_pventa (uid, codigo_pventa, valid_ip) FROM stdin;
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
5227	0001	f
5228	0002	f
5229	0011	f
5234	14  	f
5235	0001	f
5236	0001	f
4	0000	f
4	0001	f
5238	20  	f
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

