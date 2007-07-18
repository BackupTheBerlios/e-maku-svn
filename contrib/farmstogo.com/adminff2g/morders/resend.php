<?require('../../functions.php');?>
<html>
<head>
<title>Resend Mail</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>

<style>

BODY
{
    COLOR: #000000;
    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;
    FONT-SIZE: 12px
}



TD
{
    COLOR: #000000;
    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;
    FONT-SIZE: 12px
}

TH
{
    COLOR: #000000;
    FONT-FAMILY: Arial,Helvetica,Univers,Zurich BT;
    FONT-SIZE: 12px;
    FONT-WEIGHT: Bold;
}
</style>

<? 
	if ($flag) { 
@ob_start();

include "http://www.farmstogo.com/adminff2g/morders/order_mail.php?ido=$flag";
$buffer = @ob_get_contents();
@ob_end_clean();
$message =$buffer;
$headers = "From: FarmsToGo.com <events@farmstogo.com> \r\n" .
       "MIME-Version: 1.0\r\n" .
       "Content-Type: text/html; charset=utf-8\r\n" .
       "Content-Transfer-Encoding: 8bit\r\n\r\n";
	$subject = "FlowerFarmsToGo.com - Order Accepted # $flag News";
	echo $subject;
	$tm = mysql_db_query($db,"SELECT invoice_mail FROM FACTURAS WHERE ID_FACTURA=".$flag);
	$tm = mysql_fetch_row($tm);
		if ($tm[0] && $mailx) {
			for ($i=0; $i < count($mailx); $i++) {	
				echo $mailx[$i]."<br>";
//smtpmail($mailx[$i], $subject, "<html><body bgcolor=ffffff>$comments\n".str_replace("</td>","</td>\n",$tm[0]), "From: Order Flower Dealers <<miguels@flowerdealers.com>\r\n".$mime_header);
				mail($mailx[$i], $subject, $comments.$message, $headers);

				
				
				//mail($mailx[$i], $subject, "<html><body bgcolor=ffffff>$comments\n".str_replace("</td>","</td>\n",$tm[0]), "From: Order Flower Dealers <miguels@flowerdealers.com>\n".$mime_header);
			}
		 Alerta("Mail was resent !!!");
		} else  {
			//////////////////////////////////////////////////////////
			if (!$tm[0]) {
			   include("create_email.php");
			   Alerta("Mail was resent !!!");
			} else {	
				Alerta("Mail with no data !!!");
			}
		}
	?>
		<script>
			//window.close();
		</script>

<? } ?>
<body bgcolor="#FFFFFF">
<form method=POST>
  <table cellpadding=3 cellspacing=1 border="0" bgcolor=cecece>
    <tr bgcolor="#9999FF"> 
    <td> 
      <div align="center"><b></b></div>    </td>
    <td> 
      <div align="center"><b>Email</b></div>    </td>
  </tr>
    <tr bgcolor="#FFFFFF"> 
      <td> 
        <input type="checkbox" name="mailx[]" value="orders@farmstogo.com">    </td>
      <td>orders@farmstogo.com</td>
  </tr>

    <tr bgcolor="#FFFFFF"> 
      <td> 
        <input type="checkbox" name="mailx[]" value="apayable@flowerdealers.com">    </td>
      <td>apayable@flowerdealers.com</td>
  </tr>



    <tr bgcolor="#FFFFFF"> 
      <td> 
        <input type="checkbox" name="mailx[]" value="postcosecha@flowerdealers.com">    </td>
      <td>postcosecha@flowerdealers.com</td>
  </tr>

  
    <tr bgcolor="#FFFFFF"> 
      <td> 
        <input type="checkbox" name="mailx[]" value="despachos@flowerdealers.com">    </td>
      <td>despachos@flowerdealers.com</td>
  </tr>

  
    <tr bgcolor="#FFFFFF"> 
      <td> 
        <input type="checkbox" name="mailx[]" value="mercadeo@flowerdealers.com">    </td>
      <td>mercadeo@flowerdealers.com</td>
  </tr>

    <tr bgcolor="#FFFFFF">
      <td><input type="checkbox" name="mailx[]" value="sales2@flowerfarmstogo.com"></td>
      <td>sales2@farmstogo.com</td>
    </tr>
    <tr bgcolor="#FFFFFF">
      <td><input type="checkbox" name="mailx[]" value="sales1@flowerfarmstogo.com"></td>
      <td>sales1@farmstogo.com</td>
    </tr>
    <tr bgcolor="#FFFFFF"> 
      <td> 
        <input type="checkbox" name="mailx[]" value="sales@flowerfarmstogo.com,events@farmstogo.com">    </td>
      <td>sales@farmstogo.com</td>
  </tr>
  
      <tr bgcolor="#FFFFFF"> 
      <td> 
        <input type="checkbox" name="mailx[]" value="compraflor@flowerdealers.com">    </td>
      <td>compraflor@flowerdealers.com</td>
  </tr>

      <tr bgcolor="#FFFFFF">
        <td><input type="checkbox" name="mailx[]" value="gerenciacoflexpo@flowerdealers.com"></td>
        <td>gerenciacoflexpo@flowerdealers.com</td>
      </tr>
    <tr bgcolor="#FFFFFF"> 
      <td> 
        <input type="checkbox" name="mailx[]" value="miguels@flowerfarmstogo.com">    </td>
      <td>Miguel Saavedra</td>
  </tr>
  
  
      <tr bgcolor="#FFFFFF"> 
      <td> 
        <input type="checkbox" name="mailx[]" value="it@flowerdealers.com">    </td>
      <td>IT flowerdealers </td>
  </tr>


    <tr bgcolor="#FFFFFF"> 
      <td> 
        <input type="checkbox" name="mailx[]" value="<?echo $c?>">    </td>
      <td><?echo $c?> (<b>Customer</b>)</td>
  </tr>
</table>
  <input type="hidden" name="flag" value="<?echo $id_factura_facturas?>">
  <p>
  <b>Comments</b><br>
  <textarea name="comments" cols=30 rows=3></textarea>
  <br>
  <input type="submit" name="Submit" value="Send &gt;&gt;">
</form>
</body>
</html>