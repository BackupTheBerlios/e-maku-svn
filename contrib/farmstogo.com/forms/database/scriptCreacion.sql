/************************************************************************/
/*       	ISP															*/
/*______________________________________________________________________*/
/*			Investigaci�n y Desarrollo									*/
/*                                                                      */
/*                    	TERRA LYCOS COLOMBIA                          	*/
/*        	Sistema de alimentaci�n de TERRA COLOMBIA					*/
/************************************************************************/

/************************************************************************/
/* Definici�n de tablas e inserts necesarios							*/
/*______________________________________________________________________*/
/* tablas:				1. 	TRLYISP_CONFIGACTIV							*/
/*						2. 	TRLYISP_SOPORTE								*/
/*						3. 	TRLYISP_MOTIVOVENTA							*/
/* 						1.	TRLYISP_OPERADOR							*/
/************************************************************************/


CREATE TABLE TRLYISP_CONFIGACTIV (
  ID INT(10) AUTO_INCREMENT NOT NULL,
  FECHA	DATE,
  SOLICITUD INT(15) NOT NULL,
  NOMBRECLIENTE VARCHAR (50) NOT NULL,
  APELLIDOCLIENTE VARCHAR (50) NOT NULL,
  TELEFONOCLIENTE VARCHAR (12) NOT NULL,
  PLANACONFIGURAR VARCHAR (80) NOT NULL,
  FORMAPAGO VARCHAR (50) NOT NULL,
  VENDEDOR VARCHAR (60) NOT NULL,
  HORAVENTA VARCHAR (6) NOT NULL,
  OBSERVACIONVENTA TEXT,
  ESTADOCONFIGURACION VARCHAR (10) NOT NULL,
  TIPOINSTALACION VARCHAR (30) NOT NULL,
  OPERADOR VARCHAR (60) NOT NULL,
  CONFIGOUTLOOK VARCHAR (2) NOT NULL,
  USUARIOCONFIG VARCHAR (80) NOT NULL,
  OBSERVACIONESSOPORTE TEXT,
  PRIMARY KEY  (ID)
) TYPE=InnoDB;


CREATE TABLE TRLYISP_SOPORTE (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	FECHA DATE,
	NUMEROSOLICITUD INT (10) NOT NULL,
	HORALLAMADA VARCHAR (10) NOT NULL,
	CEDULACLIENTE VARCHAR (15) NOT NULL,
	NOMBRECLIENTE VARCHAR (50) NOT NULL,
	APELLIDOCLIENTE VARCHAR (50) NOT NULL,
	TELEFONOCLIENTE VARCHAR (12) NOT NULL,
	OPERADOR VARCHAR (50) NOT NULL,
	MOTIVOLLAMADA TEXT,
	SOLUCIONGENERADA TEXT,
	ESTADOSOLICITUD VARCHAR (20) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;

CREATE TABLE TRLYISP_MOTIVOVENTA (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	FECHA DATE,
	NOMBRECLIENTE VARCHAR (50) NOT NULL, 
	APELLIDOCLIENTE VARCHAR (50) NOT NULL,
	TELEFONOCLIENTE VARCHAR (12) NOT NULL,
	COMOSEENTERO VARCHAR (50) NOT NULL,
	MOTIVONOVENTA VARCHAR (60) NOT NULL,
	ISPANTERIOR VARCHAR (50) NOT NULL,
	OPERADOR VARCHAR (50) NOT NULL,
	ESTADO VARCHAR (2) NOT NULL,
	PLANINTERES VARCHAR (80) NOT NULL,
	ADICIONALES VARCHAR (80) NOT NULL,
	OBSERVACIONES TEXT,
	FECHANUEVALLAMADA DATE,
	HORANUEVALLAMADA VARCHAR (20) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;
	

CREATE TABLE TRLYISP_OPERADOR (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	NOMBRE_OPERADOR VARCHAR (50) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;

CREATE TABLE TRLYISP_MOTIVOLLAMADA (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	MOTIVO_LLAMADA VARCHAR (50) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;

CREATE TABLE TRLYISP_OPERADOR_SOPORTE (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	NOMBRE_OPERADOR VARCHAR (50) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;	
	
	
CREATE TABLE TRLYISP_EMPRESAS_ISP (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	NOMBRE_EMPRESA VARCHAR (50) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;

CREATE TABLE TRLYISP_FORMA_PAGO (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	FORMA_PAGO VARCHAR (50) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;


CREATE TABLE TRLYISP_MOTIVO_NO_VENTA (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	MOTIVO VARCHAR (80) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;


CREATE TABLE TRLYISP_PLAN_DE_INTERES (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	PLANES VARCHAR (80) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;
	
CREATE TABLE USUARIO (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	LOGIN VARCHAR (20) NOT NULL,
	PASSWORD VARCHAR (8) NOT NULL,
	PRIMARY KEY  (ID)
)

CREATE TABLE TRLYISP_ESCOLARIDAD (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	ESCOLARIDAD VARCHAR (50) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;

INSERT INTO TRLYISP_ESCOLARIDAD (ESCOLARIDAD) VALUES ('Primaria');
INSERT INTO TRLYISP_ESCOLARIDAD (ESCOLARIDAD) VALUES ('Bachillerato');
INSERT INTO TRLYISP_ESCOLARIDAD (ESCOLARIDAD) VALUES ('Universidad');
INSERT INTO TRLYISP_ESCOLARIDAD (ESCOLARIDAD) VALUES ('Universidad T�cnica');

CREATE TABLE TRLYISP_OCUPACION (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	OCUPACION  VARCHAR (50) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;

INSERT INTO TRLYISP_OCUPACION (OCUPACION) VALUES ('Administrador');
INSERT INTO TRLYISP_OCUPACION (OCUPACION) VALUES ('Agronom�a/Agr�cola');
INSERT INTO TRLYISP_OCUPACION (OCUPACION) VALUES ('Arquitectura');
INSERT INTO TRLYISP_OCUPACION (OCUPACION) VALUES ('Arte y decoraci�n');

CREATE TABLE TRLYISP_PERIODO (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	PERIODO  VARCHAR (50) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;

INSERT INTO TRLYISP_OCUPACION (OCUPACION) VALUES ('Anual');
INSERT INTO TRLYISP_OCUPACION (OCUPACION) VALUES ('Semestral');
INSERT INTO TRLYISP_OCUPACION (OCUPACION) VALUES ('Trimestral');
INSERT INTO TRLYISP_OCUPACION (OCUPACION) VALUES ('Mensual');

CREATE TABLE TRLYISP_BARRIO (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	BARRIO  VARCHAR (50) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;

INSERT INTO TRLYISP_BARRIO (BARRIO) VALUES ('Cedritos');
INSERT INTO TRLYISP_BARRIO (BARRIO) VALUES ('Chico');
INSERT INTO TRLYISP_BARRIO (BARRIO) VALUES ('Country');
INSERT INTO TRLYISP_BARRIO (BARRIO) VALUES ('Orquideas');

CREATE TABLE TRLYISP_COMO_SE_ENTERO (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	FORMA VARCHAR (80) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;


INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Televisi�n');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Radio las 40 pales');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Radio radioactiva');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Radio Tropicana');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Radio Mega');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Radio Vallenata');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Radio 104.9');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Radio 88.9');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Email Terra');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Revista Cromos');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Prensa El Tiempo');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Trasmilenio');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Amigos');
INSERT INTO TRLYISP_COMO_SE_ENTERO (FORMA) VALUES ('Volantes');

CREATE TABLE TRLYISP_ESTADO (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	ESTADO VARCHAR (80) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;


INSERT INTO TRLYISP_ESTADO (ESTADO) VALUES ('Volver a llamar');
INSERT INTO TRLYISP_ESTADO (ESTADO) VALUES ('No le interesa');
INSERT INTO TRLYISP_ESTADO (ESTADO) VALUES ('Venta Cerrada');
INSERT INTO TRLYISP_ESTADO (ESTADO) VALUES ('Otro');

CREATE TABLE TRLYISP_ADCIONALES (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	ADICION VARCHAR (80) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;

INSERT INTO TRLYISP_ADCIONALES (ADICION) VALUES ('Antipornograf�a');
INSERT INTO TRLYISP_ADCIONALES (ADICION) VALUES ('Publicidad no deseada - Anti pop ups-');



INSERT INTO TRLYISP_PLAN_DE_INTERES (PLANES) VALUES ('Turbo ilimitado');
INSERT INTO TRLYISP_PLAN_DE_INTERES (PLANES) VALUES ('Turbo 1500');
INSERT INTO TRLYISP_PLAN_DE_INTERES (PLANES) VALUES ('Turbo 900');
INSERT INTO TRLYISP_PLAN_DE_INTERES (PLANES) VALUES ('Turbo 300');
INSERT INTO TRLYISP_PLAN_DE_INTERES (PLANES) VALUES ('B�sico ilimitado');
INSERT INTO TRLYISP_PLAN_DE_INTERES (PLANES) VALUES ('B�sico 1500');
INSERT INTO TRLYISP_PLAN_DE_INTERES (PLANES) VALUES ('B�sico 900');
INSERT INTO TRLYISP_PLAN_DE_INTERES (PLANES) VALUES ('B�sico 300');
INSERT INTO TRLYISP_PLAN_DE_INTERES (PLANES) VALUES ('Antipornograf�a');
INSERT INTO TRLYISP_PLAN_DE_INTERES (PLANES) VALUES ('Publicidad no deseada (Anti pop ups)');

INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Plan my costoso');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Cliente ya tiene otro proveedor');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('No tiene dinero');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Solo llama a cotizar');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('No tiene medio de pago');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('No le interesa');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Cliente no puede decidir la compra');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Cliente va a pensar la oferta');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Cliente menor de edad');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Le interesa otro tipo de conexi�n');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Cliente Pyme o empresarial');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Cliente Terra bloqueado');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Cliente no tiene m�dem');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Oferta mejor de la competencia');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Llamada se cort�');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('No hay servicio en la ciudad del cliente');
INSERT INTO TRLYISP_MOTIVO_NO_VENTA (MOTIVO) VALUES ('Cliente desea hablar con otra �rea');


INSERT INTO TRLYISP_FORMA_PAGO (FORMA_PAGO) VALUES ('Conavi');
INSERT INTO TRLYISP_FORMA_PAGO (FORMA_PAGO) VALUES ('BBVA');
INSERT INTO TRLYISP_FORMA_PAGO (FORMA_PAGO) VALUES ('Bancolombia ACH');
INSERT INTO TRLYISP_FORMA_PAGO (FORMA_PAGO) VALUES ('Tarjeta de cr�dito');
INSERT INTO TRLYISP_FORMA_PAGO (FORMA_PAGO) VALUES ('Servientrega');


INSERT INTO TRLYISP_EMPRESAS_ISP (NOMBRE_EMPRESA) VALUES ('Ninguno');
INSERT INTO TRLYISP_EMPRESAS_ISP (NOMBRE_EMPRESA) VALUES ('Andinet');
INSERT INTO TRLYISP_EMPRESAS_ISP (NOMBRE_EMPRESA) VALUES ('Cable net');
INSERT INTO TRLYISP_EMPRESAS_ISP (NOMBRE_EMPRESA) VALUES ('EPM');
INSERT INTO TRLYISP_EMPRESAS_ISP (NOMBRE_EMPRESA) VALUES ('ETB');
INSERT INTO TRLYISP_EMPRESAS_ISP (NOMBRE_EMPRESA) VALUES ('Telecom');
INSERT INTO TRLYISP_EMPRESAS_ISP (NOMBRE_EMPRESA) VALUES ('Telesat');


INSERT INTO TRLYISP_OPERADOR_SOPORTE (NOMBRE_OPERADOR) VALUES ('JEIMY ANDREA');
INSERT INTO TRLYISP_OPERADOR_SOPORTE (NOMBRE_OPERADOR) VALUES ('ALEJANDRO');
INSERT INTO TRLYISP_OPERADOR_SOPORTE (NOMBRE_OPERADOR) VALUES ('JAVIER');
INSERT INTO TRLYISP_OPERADOR_SOPORTE (NOMBRE_OPERADOR) VALUES ('LEIDY CAROLINA');


INSERT INTO TRLYISP_OPERADOR (NOMBRE_OPERADOR) VALUES ('IVAN DARIO');
INSERT INTO TRLYISP_OPERADOR (NOMBRE_OPERADOR) VALUES ('RUBEN DARIO');
INSERT INTO TRLYISP_OPERADOR (NOMBRE_OPERADOR) VALUES ('DANIEL ERNESTO');
INSERT INTO TRLYISP_OPERADOR (NOMBRE_OPERADOR) VALUES ('DIANA CAROLINA');
INSERT INTO TRLYISP_OPERADOR (NOMBRE_OPERADOR) VALUES ('JEIMY ANDREA');
INSERT INTO TRLYISP_OPERADOR (NOMBRE_OPERADOR) VALUES ('ALEJANDRO');
INSERT INTO TRLYISP_OPERADOR (NOMBRE_OPERADOR) VALUES ('JAVIER');
INSERT INTO TRLYISP_OPERADOR (NOMBRE_OPERADOR) VALUES ('LEIDY CAROLINA');

CREATE TABLE TRLYISP_CUIDAD (
	ID INT (10) AUTO_INCREMENT NOT NULL,
	CIUDAD  VARCHAR (50) NOT NULL,
	PRIMARY KEY  (ID)
) TYPE=InnoDB;

INSERT INTO TRLYISP_CUIDAD (CIUDAD) VALUES ('Bogot�');
INSERT INTO TRLYISP_CUIDAD (CIUDAD) VALUES ('Cali');
INSERT INTO TRLYISP_CUIDAD (CIUDAD) VALUES ('Medell�n');
INSERT INTO TRLYISP_CUIDAD (CIUDAD) VALUES ('Bucaramanga');