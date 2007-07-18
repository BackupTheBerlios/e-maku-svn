<?php
require ('../../s/include/functions.php'); 
//require ('../../functions.php'); 
include('../head_admin.php');

define('IMAGES_DIR',"http://farmstogo.com/stp_images/");
define('IMAGES_DIR2',"../../stp_images/");
define('XINCLUDE','http://farmstogo.com/s/include');


?>

<center>
  <b><Big>Answer Form (Event Request)</big></b> 
</center>
<p>

<?

if ($flag2) {
//Envio del mail al cliente

	$rs = mysql_db_query($db,"SELECT reply from event_logs WHERE id=$flag2"); 
	$rs = mysql_fetch_row($rs);

	$subject =   "Your Up Coming Event";

	$headers = "FROM: design@farmstogo.com\r\n";
	$headers .= 'Content-Type: text/html; charset=  "iso-8859-1"'."\r\n".'Content-Transfer-Encoding: 7bit';

	# ENVIAMOS CORREO
		//smtpmail("orders@flowerfarmstogo.com",$subject,$rs[0], $headers);
		//smtpmail($email,$subject,$rs[0], $header);
//		smtpmail("jamurco@hotmail.com",$subject,$rs[0], $headers);
		smtpmail($email,$subject,$rs[0], $headers);
		smtpmail("events@farmstogo.com",$subject,$rs[0], $headers);
		Alerta("Message was sent !!!");
		mysql_db_query($db,"UPDATE  event_logs SET reply_date=NOW() WHERE id=$flag2");
		?>
			<script>
				window.close();
			</script>
		<?
			//echo $rs[0];

}
else if ($flag) {
//	$rsx = mysql_db_query("miguels_pomsv2","SELECT value from s_parameters WHERE param_cod='EVR'"); //miguels_pomsv2
//ARREGLAMOS DEPENDENCIA HACIA POMS
	$rsx = mysql_db_query($db,"SELECT value from s_parameters WHERE param_cod='EVR'");
	$rsx = mysql_fetch_row($rsx);


	$rs = mysql_db_query($db,"SELECT msg,image,email from event_logs WHERE id=$flag"); 
	$rs = mysql_fetch_array($rs);
	$rs["msg"] = str_replace('<img src= "http://www.farmstogo.com/s/imgs/11.gif">',"",$rs["msg"]);
	//if ($rs["image"]) { 
		//$rs["msg"] .= '<img src="http://www.flowerfarmstogo.com/s/events/'.$rs["image"].'">'; 
	//}

	$plantilla = str_replace("__NAME",$nombre,$rsx[0]);
	$xintro = $intro;
	$xbuy = $buy;
	$xproducts = $products;
	$xedt = $edt;

	$plantilla = str_replace("__INTRO",str_replace(chr(10),"<br><br>",$intro),$plantilla);
	$plantilla = str_replace("__EVENT",$rs["msg"],$plantilla);
	$plantilla = str_replace("__BUY",str_replace(chr(10),"<br><br>",$buy),$plantilla);
	$plantilla = str_replace("__PRODUCTS",str_replace(chr(10),"<br><br>",$products),$plantilla);
	$plantilla = str_replace("__EDT",$edt,$plantilla);
	$plantilla = str_replace("'","",$plantilla);


	?>
	<script>
	function OpenX(url,w,h,sb) {
		x = window.open(url,'Xr','menubar,toolbar=yes,left=0,top=0,scrollbars=yes,width='+w+',height='+h); //,toolbars=yes,resizable=yes, ,'OpenW','toolbars=yes,width='+w+',height='+h+',top=0,left=0');
		x.opener=self;
	}
	</script>
	
	  <?
$images = "<table bgcolor=cecece border=0 cellpadding=3 cellspacing=1>".
  "<tr><th colspan=4>Products Pictures</th></tr><tr bgcolor=ffffff>";

	
	 $c = mysql_db_query($db,"select * from event_images WHERE id_event='$flag'");
	 $dx = mysql_numrows($c);
	 while($ft = mysql_fetch_array($c)) {
			 if(($x%4)==0 && $x <> 0) { $images .= "</tr><tr bgcolor=ffffff>"; }
  
		$images .= "<td align=center valign=middle>".
		"<a href=\"".XINCLUDE."/popup_image.php?img=../../stp_images/".$ft["image"]."\">".
		"<img width=100 hspace=5 vspace=5 border=0 src=\"http://farmstogo.com/stp_images/".$ft["image"]."\"></a><br>".
		"<a href=javascript:OpenX('./delimg.php?idi=".$ft["id"]."','100','100')>Delete</a></td>";
	$x++;  
  } 
  $images .= "</tr></table>";

	if ($x > 0 ) {
		$plantilla = str_replace("__PICTURES",$images,$plantilla);
	}	else { 
		$plantilla = str_replace("__PICTURES","",$plantilla);
	}
	echo "</center>".$plantilla;
	mysql_db_query($db,"UPDATE  event_logs SET reply='".str_replace("Delete","",str_replace("'","",$plantilla))."' WHERE id=$flag");
	echo mysql_error();

	?>
		<form method="post">
		<input type="hidden" name="flag2" value="<?echo $flag?>">
		<input type="hidden" name="email" value="<?echo $rs["email"]?>">	
		
		<input type="button" value="Add Images" onclick="OpenX('./admin_files.php?id=<?echo $flag?>',400,400,1)">
		&nbsp;&nbsp;&nbsp;
					
		<input type="button" value=" << Edit " onclick="window.location.href='./reply.php?id=<?echo $flag?>'">
		&nbsp;&nbsp;&nbsp;<input type="submit" name="Submit" value="Confirm &gt;&gt;">
		</form>
	<?
	//Alerta("This is a Preview page, please Confirm when ready");
} else {

  if (!session_is_registered("xintro")) {	
	session_register("xintro");	
	session_register("xbuy");	
	session_register("xproducts");	
	session_register("xedt");	
 }

		$rs = mysql_db_query($db,"SELECT msg,image,name from event_logs WHERE id=$id"); 
		$rs = mysql_fetch_array($rs);
		?>
		</center>
		<SCRIPT language=JavaScript src="../morders/date.js"></SCRIPT>
		<form name=fm method="post">
		
  <p> <b> 
    <input type=hidden name=nombre value="<? echo $rs["name"]?>"></b>
		</p>
		
  <p><?
		echo str_replace('<img src="http://www.farmstogo.com/s/imgs/11.gif">',"",$rs["msg"]);
		if ($rs["image"]) { ?> 
  <p><img src="http://www.farmstogo.com/s/events/<?echo $rs["image"]?>"> 

		<?
		}
		?>
		</center>
		<p> 
		Intro<br>
		  
    <textarea name="intro" cols="80" rows="4"><?echo $xintro?></textarea>
		<p>The product that we suggest buying should be enough for:
		<br> 
		  <textarea name="products" cols="80" rows="4"><?echo $xproducts?></textarea>
		<p>Buy
		<br> 
		  <textarea name="buy" cols="80" rows="4"><?echo $xbuy?></textarea>
		<p>Suggested Delivery Date: <a href="javascript:show_calendar('fm.edt');"><img
		    src="../images/calendario.gif"
		    align=center border=0></a> 
		  <input type="text" name="edt" maxlength="10" size="10" value="<?echo $xedt?>">
		
  <p>
    <input type="hidden" name="flag" value="<?echo $id?>">
    <input type="submit" name="Submit" value="Continue &gt;&gt;">
    <br>
  </form>
<? }?>
<p><br>
</body>
</html>