<? 
include('../../functions.php'); 
   
    #CREACION DE REGISTRO detectando la variable nombre
if (trim($nombre)) {
	$CQuery = "insert into m_ctypes (ctype_name,ctype_desc,status) values ('$nombre','$desc',1)";
}

#MODIFICACION y ELIMINACION 
else if (trim($rid)) {
	$CQuery =  "update m_ctypes set ctype_name='$rdesc',ctype_desc='$ctds' where id_ctype='$rid'";
}	
else {
	$st = mysql_db_query($db,"select p.id_ctype from m_ctypes p left join j_ctypes_products s on p.id_ctype=s.id_ctype where s.id_ctype is NULL and p.id_ctype <> '' ");
	echo mysql_error();
	while ($rs = mysql_fetch_array($st)) {
	
		if (trim(${"d".$rs["id_ctype"]})) {
			mysql_db_query($db,"delete from m_ctypes where id_ctype='".$rs["id_ctype"]."'");
		}	
	
	}	
	
	
	$st = mysql_db_query($db,"select id_ctype FROM m_ctypes");
	echo mysql_error();
	while ($rs = mysql_fetch_array($st)) {
		
		if (trim(${"s".$rs["id_ctype"]})) {
			mysql_db_query($db,"update m_ctypes SET status=1 where id_ctype='".$rs["id_ctype"]."'");
		} else {
			mysql_db_query($db,"update m_ctypes SET status=0 where id_ctype='".$rs["id_ctype"]."'");		
		}

	}

}

if (trim($CQuery)) {
	mysql_db_query($db,$CQuery);
	if (mysql_error()) { 
		$error=mysql_error(); 
		Alerta($error); }
	//else { Alerta("Operación Realizada con Éxito"); }
}
	RetornarOL("admin_ctypes.php");
?>