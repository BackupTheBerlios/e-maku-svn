/************************************************************************/
/*						 FLOWERDEALERS						  			*/
/*		(Definición de las tablas de la base de datos)					*/
/*		Motor base de datos: Mysql										*/
/*		Fecha: Julio 12 de 2005											*/
/*		Autor: Javier Andrés Muñoz Hernandez							*/
/*______________________________________________________________________*/
/*																		*/
/*         														        */
/*        	                                  							*/
/************************************************************************/
/*  SCRIPT MSYQL PARA LA DEFINICIÓN DE LOS OBJETOS DE LA BASE DE DATOS	*/
/************************************************************************/

/************************************************************************/
/*			    		TABLA USERS										*/
/************************************************************************/

/************************************************************************/
/*EN ESTE ARCHIVO SE CREAN TODAS LAS TABLAS QUE SE VAN A NECESITAR		*/
/*______________________________________________________________________*/
/************************************************************************/
/*			    CREACION DE TABLAS										*/
/************************************************************************/
CREATE TABLE USERLOGIN (
  ID_USER int(11) NOT NULL auto_increment,
  REG_DATE DATE,
  EMAIL VARCHAR(100),
  PASSWD VARCHAR(10),
  FULL_NAME VARCHAR(150),
  COMPANY VARCHAR (50),
  TYPE_USER INT(2),
  PRIMARY KEY  (ID_USER)
) TYPE=InnoDB;

CREATE TABLE LISTUSERS (
  ID_USER INT(11) NOT NULL AUTO_INCREMENT,
  EMAIL VARCHAR(100),
  FIRSTNAME VARCHAR(80),
  LASTNAME VARCHAR(150),
  MARITALSTATUS VARCHAR(50),
  ADDRESS VARCHAR(150),
  CITY VARCHAR(80),
  STATEORPROVINCE VARCHAR(80),
  ZIPORPOSTALCODE VARCHAR(20),
  COUNTRY VARCHAR(30),
  HOMEPHONE VARCHAR(50),
  OCCUPATION VARCHAR(50),
  COMPANYNAME VARCHAR(50),
  COMPANYSIZE VARCHAR(50),
  WORKPHONE VARCHAR(50),
  PRIMARYNETCONNECTION VARCHAR(50),
  INTERNETEXPERIENCE VARCHAR(50),
  AREYOUARESELLER VARCHAR(50),
  FLOWERSHOPS VARCHAR(50),
  FLORALDESIGNERS VARCHAR(50),
  INTERNETMERCHANT VARCHAR(50),
  INSTITUTION VARCHAR(50),
  INDIVIDUALS VARCHAR(50),
  PRIMARY KEY  (ID_USER)
) TYPE=INNODB;


CREATE TABLE CENTROCOSTO (
  ID_CENTROCOSTO int(11) NOT NULL auto_increment,
  CODIGORAMO VARCHAR(100),
  CENTROCOSTO VARCHAR(100),
  NOMBRE VARCHAR(100),
  TIPO VARCHAR(100),
  VARIEDAD VARCHAR(100),
  COLOR VARCHAR(100),
  CLASIFICADORA VARCHAR(100),
  GRADO VARCHAR(100),
  FECHAENTRADA DATE,
  FECHACLASIFICACION DATE,
  PRIMARY KEY  (ID_CENTROCOSTO)
) TYPE=InnoDB;


  