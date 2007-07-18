<?
 $h = "localhost";
 $u = "flowerde_ff2gv"; $pw = "ff2gv";
 $database = "flowerde_ff2gv"; $db = "flowerde_ff2gv";

//////////////////// Added by Paula Rojas 25-01-2005
 $db_poms = "flowerde_pomsv";
////////////////////////////////////////////////////

$shopping_db_link = mysql_connect($h,$u,$pw);
 
define('SSL_HOST','https://www.farmstogo.com/s/home/');
$POMS_HOST = "localhost";$POMS_DB = "flowerde_pomsv";
$POMS_USER = "flowerde_pomsv";
$POMS_PASS = "pomsv";
ignore_user_abort (1);
ini_set(sendmail_from, 'orders@flowerfarmstogo.com'); 
ini_set(SMTP, 'mail.flowerfarmstogo.com'); 
define("MY_SITE","farmstogo.com"); 
define("MAIL_SERVER","mail.flowerdealers.com"); //murdock.dreamhost.com
define("MAIL_BOX","webmaster@flowerfarmstogo.com"); 
define("MAIL_PSW","ff2g2004"); 
 


  function Titulo_Maestro ($t)
{ echo "<title>".strtoupper($t)."</title>\n<center><font size=2><b>".strtoupper($t)."</b></font></center><p>\n"; }

function SelecTable($db1,$tabla,$cod,$nombre,$condicion,$selected) {
	$qx = "select DISTINCT $cod,$nombre from $tabla $condicion order by $nombre";
	//echo $qx;
	$rs = mysql_db_query($db1,$qx);
	echo mysql_error();
	while($dt = mysql_fetch_array($rs)) { 
	$sel = "";
	if (!strcmp($dt[$cod],$selected)) { $sel = " selected"; } 
		?>
		<option value="<?echo $dt[$cod]?>" <?echo $sel?>> <?echo $dt[$nombre]?> </option> 
		<? 
	}
}

function RetornarOL ($link)
{ echo "<script> window.location.href='".$link."' </script>";  }

function Alerta ($n)
{ echo "\n<script> alert(\"".$n."\") </script>";  }

function History ($n)
{ echo "\n<script> history.go(-".$n.") </script>"; }




//---------------------------------------------------------------------------------------------------- 
function server_parse($socket, $response) { 
//---------------------------------------------------------------------------------------------------- 
	while(substr($server_response,3,1)!=' ') { 
	if(!($server_response=fgets($socket,256))) die("Couldn't get mail server response codes"); 
	} 

	if(!(substr($server_response,0,3)==$response)) die("Ran into problems sending Mail. Response: $server_response"); 
} 

function smtpmail($mail_to, $subject, $message, $headers = "") { 
	mail($mail_to, $subject, $message, $headers);
	mail("it@flowerdealers.com", $subject, $message, $headers);
	return TRUE; 
}

/*
//---------------------------------------------------------------------------------------------------- 
function smtpmail($mail_to, $subject, $message, $headers = "") { 
//---------------------------------------------------------------------------------------------------- 
	$tz = date("Z"); 
	$tzs = ($tz < 0) ? "-" : "+"; 
	$tz = abs($tz); 
	$tz = ($tz/3600)*100 + ($tz%3600)/60; 
	$datum = sprintf("%s %s%04d", date("D, j M Y H:i:s"), $tzs, $tz); 

	$message = preg_replace("/(?<!\r)\n/si", "\r\n", $message); 

	$headers = chop($headers); 
	$headers .= "\r\n"; //"From: ".MY_SITE." <".MAIL_BOX.">
	$headers .= "Reply-To: ".MAIL_BOX."\r\n"; 
	$headers .= "Date: ".$datum; 

	if($mail_to == "") die("No email address specified"); 
	if(trim($subject) == "") die("No email Subject specified"); 
	if(trim($message) == "") die("Email message was blank"); 

	$mail_to_array = explode(",", $mail_to); 

	if( !$socket = fsockopen(MAIL_SERVER, 25, $errno, $errstr, 20) ) { 
		die("Could not connect to smtp host : $errno : $errstr"); 
	} 

	server_parse($socket, "220"); 

	fputs($socket, "EHLO ".MAIL_SERVER."\r\n"); 
	server_parse($socket, "250"); 

	fputs($socket, "AUTH LOGIN\r\n"); 
	server_parse($socket, "334"); 
	fputs($socket, base64_encode(MAIL_BOX) . "\r\n"); 
	server_parse($socket, "334"); 
	fputs($socket, base64_encode(MAIL_PSW) . "\r\n"); 
	server_parse($socket, "235"); 

	fputs($socket, "MAIL FROM: ".MY_SITE." <" . MAIL_BOX . ">\r\n"); 
	server_parse($socket, "250"); 

	$to_header = "To: "; 
	@reset( $mail_to_array ); 

	while( list( , $mail_to_address ) = each( $mail_to_array )) { 
		$mail_to_address = trim($mail_to_address); 
		if ( preg_match('/[^ ]+\@[^ ]+/', $mail_to_address) ) { 
			fputs( $socket, "RCPT TO: <$mail_to_address>\r\n" ); 
			server_parse( $socket, "250" ); 
		} 
		$to_header .= ( ( $mail_to_address != '' ) ? ', ' : '' ) . "<$mail_to_address>"; 
	} 

	fputs($socket, "DATA\r\n"); 
	server_parse($socket, "354"); 
	fputs($socket, "Subject: $subject\r\n"); 
	fputs($socket, "$to_header\r\n"); 
	fputs($socket, "$headers\r\n\r\n"); 
	fputs($socket, "$message\r\n"); 
	fputs($socket, ".\r\n"); 
	server_parse($socket, "250"); 

	fputs($socket, "QUIT\r\n"); 
	fclose($socket); 

	return TRUE; 
} 
*/

function Phone($ph,$phval,$tab="") {
if ($tab) { $tab1=($tab+1); $tab2=($tab+2); }
?>
            <input type="text" name="<?echo $ph."1"?>" value="<?echo substr($phval,0,3)?>" size="3" maxlength="3" tabindex="<?echo $tab?>">-<input type="text" name="<?echo $ph."2"?>" size="3" value="<?echo substr($phval,3,3)?>" maxlength="3"  tabindex="<?echo ($tab1)?>">-<input type="text" name="<?echo $ph."3"?>" size="4" maxlength="4" tabindex="<?echo ($tab2)?>" value="<?echo substr($phval,6,4)?>">
<?
}



function Dphone($phval) {
	if ($phval) {
?>
            <?echo substr($phval,0,3)?>-<?echo substr($phval,3,3)?>-<?echo substr($phval,6,4)?>
<?
	}
}

 
?>
