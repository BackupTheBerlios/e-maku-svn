<?

/************************************************************************/
/* Definición de variables globales y librerías requeridas.				*/
/*______________________________________________________________________*/

//require_once("../config/general.conf");		// Get the global variables
//require_once("../config/bd.conf"); 			// Get the connection variables
//require_once("../lib/database.php"); 							// Get the DataBase extended functions
require_once("../../consultas/funcionesSQL.php"); 	
require_once("../../utilidades/sesiones.php"); 	

// +-----------------------------------------------------------------------+
// |  Incializa las plantillas y establece la conexión con la BD		   |
// +-----------------------------------------------------------------------+
//$db = DB::connect($dsn, DB_CONNECT_MODE); // Creates a database connection object in $db 
										  // or, a database error object if it went wrong.
										  // Lista de categorias principales
//$db->setFetchMode(DB_FETCHMODE_ASSOC);	
$email=$_POST['email'];
$password=$_POST['password'];
$password="md5('$password')";

$ff2gComm = mysql_connect('localhost', 'flowerde_dbrf2go', 'rosefarms') 
	or 
		die("Error al conectarse a la DB");
$ff2gDB = mysql_select_db('flowerde_ff2gv', $ff2gComm) 
	or die("Error al seleccionar la BD");
	
$query = "select * from USERLOGIN WHERE EMAIL='".$email."' AND PASSWD=".$password." limit 1";
$result = mysql_query($query,$ff2gComm);
$fila = mysql_fetch_array($result);
do{
$email=$fila['EMAIL'];
$typeUser=$fila['TYPE_USER'];
$company=$fila['COMPANY'];
$name = $fila['FULL_NAME'];
} while ($fila = mysql_fetch_array($result));
// MODIFY BY: FELIPE GONZALEZ GONZALEZ
// DATE: 2006.01.20
// EMAIL: curramba83@gmail.com

// Get the id user and name of the vendor

$pomsComm = mysql_connect('localhost', 'flowerde_pomsv', 'pomsv') or die("Error al conectarse a la DB");
$pomsDB = mysql_select_db('flowerde_pomsv', $pomsComm) or die("Error al seleccionar la BD");
$query = "SELECT id_user, full_name from s_users WHERE email='".$email."'";
$result = mysql_query($query);

if (!$result) {
 $message  = 'Invalid query: ' . mysql_error() . "\n";
$message .= 'Whole query: ' . $query;
die($message);
}

while($fila = mysql_fetch_array($result)) 
{
  	$idPoms = $fila['id_user'];
	$namePoms = $fila['full_name'];
} 
//iniciarSesion($email, $name, $typeUser, $company);
iniciarSesion($email, $name, $typeUser, $company, $idPoms, $namePoms);
?>

<script language="Javascript">
	window.location="/adminff2g/reports.php";
</script> 