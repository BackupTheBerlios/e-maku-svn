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
	

		$list = file("../../files/export1.csv") ;

  		$stm="DELETE FROM TRYLOVIEDO_USUARIO";
		$result = $db->query($stm);		
		echo $stm;
	
		$count = count($list);
		for ($i=0; $i<=$count; $i++){
			$array = split(",", $list[$i]);
			$stm = "INSERT INTO LISTUSERS (EMAIL,FIRSTNAME, LASTNAME, MARITALSTATUS, ADDRESS, CITY, STATEORPROVINCE, ZIPORPOSTALCODE,
													COUNTRY, HOMEPHONE, OCCUPATION, COMPANYNAME, COMPANYSIZE, WORKPHONE, PRIMARYNETCONNECTION, INTERNETEXPERIENCE, AREYOUARESELLER, FLOWERSHOPS,
													FLORALDESIGNERS, INTERNETMERCHANT, INSTITUTION, INDIVIDUALS)
				VALUES ('$array[0]', '$array[1]', '$array[2]','$array[3]','$array[4]','$array[5]','$array[6]','$array[7]','$array[8]','$array[9]', '$array[10]','$array[11]',
						'$array[12]','$array[13]','$array[14]','$array[15]','$array[16]','$array[17]','$array[18]','$array[19]','$array[20]','$array[21]')";
			$result = $db->query($stm);		
			
		}
		echo "<script> alert('el archivo fue cargado!! ')</script>";
		echo "<script>
					location.href='../reports.php';
		 	 </script>";

	 


?>
