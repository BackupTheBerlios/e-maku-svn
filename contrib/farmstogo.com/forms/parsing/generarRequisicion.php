<?php
/************************************************************************/
/*       	ISP															*/
/*______________________________________________________________________*/
/*			Investigación y Desarrollo									*/
/*                                                                      */
/*                    	TERRA LYCOS COLOMBIA                          	*/
/*        	Sistema de alimentación de TERRA COLOMBIA					*/
/************************************************************************/

/************************************************************************/
/* Definición de variables globales y librerías requeridas.				*/
/*______________________________________________________________________*/
/* Librerías:			1. 	errorManager.php							*/
/*						2. 	consultas_publico.php						*/
/*						3. 	FastTemplate.php							*/
/*							Para el manejo de plantillas				*/
/* Variables Globales:	1.	$tpl										*/
/*							Plantilla que se está proces&&o actualmente*/
/************************************************************************/
 Header("Content-Disposition: inline; filename=registros" . date("F_j_Y_g-i-a") . ".xls");
 Header("Content-Description: PHP3 Generated Data");
 Header("Content-type: application/vnd.ms-excel; name='My_Excel'");
 flush;
 
 require_once("../config/general.conf");		// Get the global variables
require_once("../config/bd.conf"); 			// Get the connection variables
require_once("../lib/database.php"); 							// Get the DataBase extended functions
require_once("../../consultas/funcionesSQL.php"); 	


// +-----------------------------------------------------------------------+
// |  Incializa las plantillas y establece la conexión con la BD		   |
// +-----------------------------------------------------------------------+
$db = DB::connect($dsn, DB_CONNECT_MODE); // Creates a database connection object in $db 
										  // or, a database error object if it went wrong.
										  // Lista de categorias principales
$db->setFetchMode(DB_FETCHMODE_ASSOC);	

$consultarRequisicion=verRequisicion($id, $db);


	

?>

<html>
<head>
<title>Requisición de materiales - Horizonte impresores</title>
</head>

<body>
<table border="1">
	<tr bgcolor="silver">
		<td>Requisión Numero</td>
		<td>Fecha</td>
    	<td>Solicitante</td>
    	<td>Cantidad</td>
    	<td>Unidad Medida</td>
		<td>Descipcion</td>
    	<td>Trabajo</td>
    	<td>OT</td>
    	<td>Proveedor</td>
    	<td>Precio</td>
	</tr>
	<?

	for ($k=0 ; $k < count($consultarRequisicion) ; $k++)
		{
	?>
	<tr>
		<td><?=$consultarRequisicion[$k]["ID"]?></td>
    	<td><?=$consultarRequisicion[$k]["FECHA"]?></td>
		<td><?=$consultarRequisicion[$k]["SOLICITANTE"]?></td>
		<td><?=$consultarRequisicion[$k]["CANTIDAD1"]?></td>
		<td><?=$consultarRequisicion[$k]["UNIDADMEDIDA1"]?></td>
		<td><?=$consultarRequisicion[$k]["DESCRIPCION1"]?></td>
		<td><?=$consultarRequisicion[$k]["TRABAJO1"]?></td>
		<td><?=$consultarRequisicion[$k]["OT1"]?></td>
		<td><?=$consultarRequisicion[$k]["PROVEEDOR1"]?></td>
		<td><?=$consultarRequisicion[$k]["PRECIO1"]?></td>
	</tr>
	<tr>
		<td><?=$consultarRequisicion[$k]["ID"]?></td>
    	<td><?=$consultarRequisicion[$k]["FECHA"]?></td>
		<td><?=$consultarRequisicion[$k]["SOLICITANTE"]?></td>
		<td><?=$consultarRequisicion[$k]["CANTIDAD2"]?></td>
		<td><?=$consultarRequisicion[$k]["UNIDADMEDIDA2"]?></td>
		<td><?=$consultarRequisicion[$k]["DESCRIPCION2"]?></td>
		<td><?=$consultarRequisicion[$k]["TRABAJO2"]?></td>
		<td><?=$consultarRequisicion[$k]["OT2"]?></td>
		<td><?=$consultarRequisicion[$k]["PROVEEDOR2"]?></td>
		<td><?=$consultarRequisicion[$k]["PRECIO2"]?></td>
	</tr>
	<tr>
		<td><?=$consultarRequisicion[$k]["ID"]?></td>
    	<td><?=$consultarRequisicion[$k]["FECHA"]?></td>
		<td><?=$consultarRequisicion[$k]["SOLICITANTE"]?></td>
		<td><?=$consultarRequisicion[$k]["CANTIDAD3"]?></td>
		<td><?=$consultarRequisicion[$k]["UNIDADMEDIDA3"]?></td>
		<td><?=$consultarRequisicion[$k]["DESCRIPCION3"]?></td>
		<td><?=$consultarRequisicion[$k]["TRABAJO3"]?></td>
		<td><?=$consultarRequisicion[$k]["OT3"]?></td>
		<td><?=$consultarRequisicion[$k]["PROVEEDOR3"]?></td>
		<td><?=$consultarRequisicion[$k]["PRECIO3"]?></td>
	</tr>
	<tr>
		<td><?=$consultarRequisicion[$k]["ID"]?></td>
    	<td><?=$consultarRequisicion[$k]["FECHA"]?></td>
		<td><?=$consultarRequisicion[$k]["SOLICITANTE"]?></td>
		<td><?=$consultarRequisicion[$k]["CANTIDAD4"]?></td>
		<td><?=$consultarRequisicion[$k]["UNIDADMEDIDA4"]?></td>
		<td><?=$consultarRequisicion[$k]["DESCRIPCION4"]?></td>
		<td><?=$consultarRequisicion[$k]["TRABAJO4"]?></td>
		<td><?=$consultarRequisicion[$k]["OT4"]?></td>
		<td><?=$consultarRequisicion[$k]["PROVEEDOR4"]?></td>
		<td><?=$consultarRequisicion[$k]["PRECIO4"]?></td>
	</tr>
		<tr>
		<td><?=$consultarRequisicion[$k]["ID"]?></td>
    	<td><?=$consultarRequisicion[$k]["FECHA"]?></td>
		<td><?=$consultarRequisicion[$k]["SOLICITANTE"]?></td>
		<td><?=$consultarRequisicion[$k]["CANTIDAD5"]?></td>
		<td><?=$consultarRequisicion[$k]["UNIDADMEDIDA5"]?></td>
		<td><?=$consultarRequisicion[$k]["DESCRIPCION5"]?></td>
		<td><?=$consultarRequisicion[$k]["TRABAJO5"]?></td>
		<td><?=$consultarRequisicion[$k]["OT5"]?></td>
		<td><?=$consultarRequisicion[$k]["PROVEEDOR5"]?></td>
		<td><?=$consultarRequisicion[$k]["PRECIO5"]?></td>
	</tr>
		<tr>
		<td><?=$consultarRequisicion[$k]["ID"]?></td>
    	<td><?=$consultarRequisicion[$k]["FECHA"]?></td>
		<td><?=$consultarRequisicion[$k]["SOLICITANTE"]?></td>
		<td><?=$consultarRequisicion[$k]["CANTIDAD6"]?></td>
		<td><?=$consultarRequisicion[$k]["UNIDADMEDIDA6"]?></td>
		<td><?=$consultarRequisicion[$k]["DESCRIPCION6"]?></td>
		<td><?=$consultarRequisicion[$k]["TRABAJO6"]?></td>
		<td><?=$consultarRequisicion[$k]["OT6"]?></td>
		<td><?=$consultarRequisicion[$k]["PROVEEDOR6"]?></td>
		<td><?=$consultarRequisicion[$k]["PRECIO6"]?></td>
	</tr>
		<tr>
		<td><?=$consultarRequisicion[$k]["ID"]?></td>
    	<td><?=$consultarRequisicion[$k]["FECHA"]?></td>
		<td><?=$consultarRequisicion[$k]["SOLICITANTE"]?></td>
		<td><?=$consultarRequisicion[$k]["CANTIDAD7"]?></td>
		<td><?=$consultarRequisicion[$k]["UNIDADMEDIDA7"]?></td>
		<td><?=$consultarRequisicion[$k]["DESCRIPCION7"]?></td>
		<td><?=$consultarRequisicion[$k]["TRABAJO7"]?></td>
		<td><?=$consultarRequisicion[$k]["OT7"]?></td>
		<td><?=$consultarRequisicion[$k]["PROVEEDOR7"]?></td>
		<td><?=$consultarRequisicion[$k]["PRECIO7"]?></td>
	</tr>
		<tr>
		<td><?=$consultarRequisicion[$k]["ID"]?></td>
    	<td><?=$consultarRequisicion[$k]["FECHA"]?></td>
		<td><?=$consultarRequisicion[$k]["SOLICITANTE"]?></td>
		<td><?=$consultarRequisicion[$k]["CANTIDAD8"]?></td>
		<td><?=$consultarRequisicion[$k]["UNIDADMEDIDA8"]?></td>
		<td><?=$consultarRequisicion[$k]["DESCRIPCION8"]?></td>
		<td><?=$consultarRequisicion[$k]["TRABAJO8"]?></td>
		<td><?=$consultarRequisicion[$k]["OT8"]?></td>
		<td><?=$consultarRequisicion[$k]["PROVEEDOR8"]?></td>
		<td><?=$consultarRequisicion[$k]["PRECIO8"]?></td>
	</tr>
		<tr>
		<td><?=$consultarRequisicion[$k]["ID"]?></td>
    	<td><?=$consultarRequisicion[$k]["FECHA"]?></td>
		<td><?=$consultarRequisicion[$k]["SOLICITANTE"]?></td>
		<td><?=$consultarRequisicion[$k]["CANTIDAD9"]?></td>
		<td><?=$consultarRequisicion[$k]["UNIDADMEDIDA9"]?></td>
		<td><?=$consultarRequisicion[$k]["DESCRIPCION9"]?></td>
		<td><?=$consultarRequisicion[$k]["TRABAJO9"]?></td>
		<td><?=$consultarRequisicion[$k]["OT9"]?></td>
		<td><?=$consultarRequisicion[$k]["PROVEEDOR9"]?></td>
		<td><?=$consultarRequisicion[$k]["PRECIO9"]?></td>
	</tr>
	<?
	}
	?>
</table>
</body>
</html>
 