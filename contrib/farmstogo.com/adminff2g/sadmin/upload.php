<?
// +------------------------------------------------------------------------+
// |                       Llamado a las Bibliotecas requeridas				|
// +------------------------------------------------------------------------+
	
	require_once("../../forms/config/bd.conf"); 			// Get the connection variables
	require_once("../../forms/lib/database.php"); 							// Get the DataBase extended functions	
// +-----------------------------------------------------------------------+
// |  Incializa las plantillas y establece la conexión con la BD		   |
// +-----------------------------------------------------------------------+

	$db = DB::connect($dsn, DB_CONNECT_MODE); 
	$db->setFetchMode(DB_FETCHMODE_ASSOC);
	
include('../../includes/head.php');

Titulo_Maestro ("VenMaster Orders to FD Delivery");
	
if (!$flag) { ?>
	<form action="<? echo $PHP_SELF ?>" method="post" enctype="multipart/form-data">
	<table>
		<tr>
			<td><b><center>Aplicativo para subir archivos</b></center></td>
		</tr>
		<tr>
			<td>Archivo </td>
			<td><input type="file" name="arch" size=25> </td>
		</tr>
		<tr>
			<td>
				<input type="submit" name="send" value="Subir Archivo ">
				<input type="hidden" name="flag" value="1">
			</td>
		</tr>
	</table>
	</form>
<? }
	$ext = substr ($_FILES['arch']['name'], -4);
	if ($flag){
		$path ="../../files/";
		 	
			copy($_FILES['arch']['tmp_name'],$path.$_FILES['arch']['name']);
			echo "<script> alert('El archivo fue cargado con exito!!!')</script>";
			
	}
	
	
	