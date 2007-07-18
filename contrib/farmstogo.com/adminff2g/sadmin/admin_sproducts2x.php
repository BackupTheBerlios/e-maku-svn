<? 
require ('../../functions.php'); 
include('../head_admin.php');
   
    #CREACION DE REGISTRO detectando la variable nombre
if (trim($nombre)) {
	$CQuery = "insert into SUB_TIPO_PRODUCTOS (ID_TIPO_PRODUCTO,SUB_TIPO_PRODUCTO,DESC_SUB_TIPO_PRODUCTO) values ('$pt','$nombre','$descr')";
}

#MODIFICACION y ELIMINACION 
else if (trim($rid)) {
	$CQuery =  "update SUB_TIPO_PRODUCTOS set SUB_TIPO_PRODUCTO='$rvalor', DESC_SUB_TIPO_PRODUCTO='$rdesc' where ID_SUB_TIPO_PRODUCTO='$rid'";
}	
else {
	$st = mysql_db_query($db,"select p.ID_SUB_TIPO_PRODUCTO from SUB_TIPO_PRODUCTOS p left join PRODUCTOS s on p.ID_SUB_TIPO_PRODUCTO=s.ID_SUB_TIPO_PRODUCTO where s.ID_SUB_TIPO_PRODUCTO is NULL and p.ID_SUB_TIPO_PRODUCTO <> '' ");
	echo mysql_error();
	while ($rs = mysql_fetch_array($st)) {
	
		if (trim(${"d".$rs["ID_SUB_TIPO_PRODUCTO"]})) {
			mysql_db_query($db,"delete from SUB_TIPO_PRODUCTOS where ID_SUB_TIPO_PRODUCTO='".$rs["ID_SUB_TIPO_PRODUCTO"]."'");
		}	
	
	}	
	
	
	$st = mysql_db_query($db,"select p.ID_SUB_TIPO_PRODUCTO FROM SUB_TIPO_PRODUCTOS p WHERE p.ID_TIPO_PRODUCTO=$pt ");
	echo mysql_error();
	while ($rs = mysql_fetch_array($st)) {
	
	
		if (trim(${"s".$rs["ID_SUB_TIPO_PRODUCTO"]})) {
			mysql_db_query($db,"update SUB_TIPO_PRODUCTOS SET STATUS=1 where ID_SUB_TIPO_PRODUCTO='".$rs["ID_SUB_TIPO_PRODUCTO"]."'");
		} else {
			mysql_db_query($db,"update SUB_TIPO_PRODUCTOS SET STATUS=0 where ID_SUB_TIPO_PRODUCTO='".$rs["ID_SUB_TIPO_PRODUCTO"]."'");		
		}
	
	
		if (trim(${"a".$rs["ID_SUB_TIPO_PRODUCTO"]})) {
			mysql_db_query($db,"update SUB_TIPO_PRODUCTOS SET ADDON=1 where ID_SUB_TIPO_PRODUCTO='".$rs["ID_SUB_TIPO_PRODUCTO"]."'");
		} else {
			mysql_db_query($db,"update SUB_TIPO_PRODUCTOS SET ADDON=0 where ID_SUB_TIPO_PRODUCTO='".$rs["ID_SUB_TIPO_PRODUCTO"]."'");		
		}
	}

}

if (trim($CQuery)) {
	mysql_db_query($db,$CQuery);
	if (mysql_error()) { 
		$error=mysql_error(); 
		Alerta($error); }
	else { Alerta("Operación Realizada con Éxito"); }
}
	RetornarOL("./admin_sproducts.php?pt=$pt");
?>