<? require ('../../functions.php');   
   #CREACION DE REGISTRO detectando la variable nombre

	
	#Metiendo the product supplier
	mysql_db_query($db,"DELETE FROM j_ctypes_products where id_ctype='$farm'");
	echo mysql_error();
	
		for ($i=0;$i<count($pr);$i++) {
			mysql_db_query($db,"INSERT INTO j_ctypes_products (id_product,id_ctype) VALUES ('$pr[$i]','$farm')");
			
		}	
	RetornarOL("p_ct_products.php?farm=$farm&pt=$pt");

?>