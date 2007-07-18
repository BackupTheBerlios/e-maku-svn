<html>
<head>
<title>Flower Farms To Go .com</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script language="JavaScript" src="/javascript/validaciones/addUser.js"></script>
<?
/************************************************************************/
/*       	FLOWERDEALERS															*/
/*______________________________________________________________________*/
/*			Investigación y Desarrollo									*/
/*                                                                      */
/*                    		FLOWERDEALERS  	                        	*/
/*        	Sistema de alimentación de FLOWERDEALERS					*/
/************************************************************************/

/************************************************************************/
/* Definición de variables globales y librerías requeridas.				*/
/*______________________________________________________________________*/

include('../head_admin.php');
require_once("../../forms/config/bd.conf"); 			// Get the connection variables
require_once("../../forms/lib/database.php"); 							// Get the DataBase extended functions
require_once("../../consultas/funcionesSQL.php"); 	
require_once("../../utilidades/sesiones.php"); 	

// +-----------------------------------------------------------------------+
// |  Incializa las plantillas y establece la conexión con la BD		   |
// +-----------------------------------------------------------------------+
$db = DB::connect($dsn, DB_CONNECT_MODE); // Creates a database connection object in $db 
										  // or, a database error object if it went wrong.
										  // Lista de categorias principales
$db->setFetchMode(DB_FETCHMODE_ASSOC);	

$user=findUsers ($id, $db);


?>
<table width="780" border="0" cellspacing="0" cellpadding="0" name="contenidos">
	<tr align="center" class="titulogrisprincipal">
    	<td>INSERT USER</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>
<form action="../../forms/process.php" method="post" name="frmupdateusr" onsubmit="return checkForm(this)">
<table width="300" border="0" cellspacing="0" cellpadding="0" name="contenidos" align="center" cellspacing="4" cellpadding="2" cellpadding="2">
	<tr class="titulogrisprincipal">
    	<td align="right">Full Name</td><td align="right"><input type="text" name="name" size="30" class="combos" ></td>
	</tr>
	<tr class="titulogrisprincipal">
    	<td align="right">E-mail</td><td align="right"><input type="text" name="email" size="30" class="combos" ></td>
	</tr>
	<tr class="titulogrisprincipal">
    	<td align="right">Password</td><td align="right"><input type="password" name="password" size="30" class="combos" ></td>
	</tr>
	
	<tr class="titulogrisprincipal">
    	<td align="right">Company</td>
		<td align="right">
			<select name="company" class="combos">
				<option value=-1>Select...
				<option value="Flowerdealers">Flowerdealers
				<option value="Coflexpo">Coflexpo
			<select>
		</td>
	</tr>
	<tr class="titulogrisprincipal">
    	<td align="right">User Type</td>
		<td align="right">
			<select name="type" class="combos">
				<option value=-1>Select...
				<option value="1">Administrator
				<option value="2">Shipping
				<option value="3">Salesman
			<select>
		</td>
	</tr>
	<tr class="titulogrisprincipal">
		<td align="right">&nbsp;</td>
		<td align="right">
		<input type="submit" value="Send">
		<input type="hidden" name="insertUser" value="2">
		</td>
	</tr>
</table>
</form>
<table width="300" border="0" cellspacing="0" cellpadding="0" align="center">
	<tr align="right" class="titulogrisprincipal">
    	<td ><img src="../../s/imgs/home.gif" border=0><a href="/adminff2g/reports.php" class="combos">Go Back to Menu</a></td>
	</tr>
</table>
