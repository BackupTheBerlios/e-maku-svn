<?php
mysql_select_db($db,$shopping_db_link) or die('Can not connect the database');
$query_sterms="SELECT `value` from s_parameters where `param_cod`='STERMS' ";
$sterms=mysql_query($query_sterms,$shopping_db_link) or die (mysql_error());
$row_sterms=mysql_fetch_assoc($sterms);
?>
<script>

	function OpenY(page) {

		window.open(page,'CS','toolbars=no,scrollbars=yes,resizing=no,width=500,height=400')

	}	

</script>



<!--

-->
<div align="center"> <?php echo $row_sterms["value"]; ?> </div>


<div align="center" class="fot"><br />


<img src="../s/imgs/px_g.gif" width="470" height="1" border="0" vspace="10" align="top"><br>

<table border="0" cellpadding="0" cellspacing="0" align="center">

<tr><td><img src="../s/imgs/ico_visa.gif" width="39" height="26" border="0" hspace="7"></td>

<td><img src="../s/imgs/ico_mc.gif" width="37" height="26" border="0" hspace="7"></td>

<td><img src="../s/imgs/ico_ae.gif" width="27" height="26" border="0" hspace="7"></td>

<td><img src="../s/imgs/ico_saf.gif" width="65" height="22" border="0" hspace="7"></td>

<td><img src="../s/imgs/geotrust_logo.gif" alt="Geotrust" width="93" height="22" /></td>

<td><img src="../s/imgs/ico_bofa.gif" width="59" height="22" border="0" hspace="7"></td>

<td>&nbsp;</td>

<td>&nbsp;</td>

</tr></table>

<img src="../s/imgs/px_g.gif" width="470" height="1" border="0" vspace="10" align="top">

<br>

<a href="../s/home/home.php">HOME</a> &nbsp;&middot;&nbsp; 

<a href="../s/home/shopping_cart.php">SHOPPING CART</a> &nbsp;&middot;&nbsp; 

<a href="../s/home/view_promotions.php">PROMOTIONS</a> &nbsp;&middot;&nbsp; 

<a href="../s/home/policies.php">POLICIES</a> &nbsp;&middot;&nbsp; 

<a href="../s/home/faqs.php">FAQ'S</a> &nbsp;&middot;&nbsp; 

<a href="../s/home/cu.php">CONTACT US</a> &nbsp;&middot;&nbsp;&nbsp;<a href="../s/home/links.php">LINKS</a> &nbsp;&middot;&nbsp; <a href="javascript:OpenY('../home/help.html')">HELP</a><br>



<a href="../s/so/standing_orders.php">Standing Orders</a> &nbsp;&middot;&nbsp; 

<a href="../s/home/testimonials.php">Testimonials</a> &nbsp;&middot;&nbsp; 

<a href="javascript:OpenY('http://server.iad.liveperson.net/hc/30218384/?cmd=file&amp;file=visitorWantsToChat&amp;site=30218384&amp;AEPARAMS&amp;referrer=')">Online Customer Service</a> &nbsp;&middot;&nbsp; 

&nbsp;&middot;&nbsp; 

<a href="javascript:OpenY('tell_friend.php')">Tell a Friend</a><br>

<br>
<br />



<?if (!$ssl) { ?>



<!-- START CODE - bCentral Subscribers -->

<FORM ACTION="http://lb.bcentral.com/ex/manage/subscriberprefs.aspx" METHOD="POST">

<FONT SIZE="-1" FACE="arial, helvetica">

<STRONG>Register for FlowerFarmsToGo.com email updates</STRONG>

</FONT>

<BR>

<INPUT TYPE="TEXT" NAME="email" />

<INPUT TYPE="HIDDEN" NAME="customerid" value="49640" />

<INPUT TYPE="SUBMIT" value="Sign up" style="background-color:white;color:green;font-weight:bold" />

</FORM>

<!-- END CODE - bCentral Subscribers -->



<br>

<center>

<a  href="#" onclick="javascript:window.open('http://www.directcatering.com/ap_start.jsp?aff_id=30465','DirectCatering','scrollbars=yes,width=300,height=550')" ></a>

</center>



<br>

<? } ?>



&copy; 2007 FarmsToGo.com</div>



	</td>

	<td width="20" background="../s/imgs/22.gif" style="background:url(../s/imgs/22.gif)"><img src="../s/imgs/22.gif" width="20" height="10" border="0"></td></tr>

<tr><td></td>

	<td><img src="../s/imgs/31.gif" width="20" height="14" border="0"></td>

	<td background="../s/imgs/32.gif" style="background:url(../s/imgs/32.gif)"><img src="../s/imgs/32.gif" width="2" height="14" border="0"></td>

	<td><img src="../s/imgs/33.gif" width="20" height="14" border="0"></td></tr>

</table><br>

<?if (!$ssl) { ?>

<!-- Google Conversion Code PAGE VIEWED-->

<script language="JavaScript">

<!--

google_conversion_id = 1072374508;

google_conversion_language = "en_US";

google_conversion_format = "1";

google_conversion_color = "FFFFFF";

if (1.0) {

  google_conversion_value = 1.0;

}

google_conversion_label = "PageView";

-->

</script>

<script language="JavaScript" src="http://www.googleadservices.com/pagead/conversion.js"></script>

<!-- End Google Conversion Code PAGE VIEWED-->

<!-- BEGIN FASTCOUNTER PRO CODE --> 

<script language="JavaScript" src="http://fcstats.bcentral.com/js/13718/0" type="text/javascript"></script>

<!-- END FASTCOUNTER PRO CODE -->

<!-- BEGIN HumanTag Monitor. DO NOT MOVE! MUST BE PLACED JUST BEFORE THE /BODY TAG -->

<script language='javascript' src='http://server.iad.liveperson.net/hc/30218384/x.js?cmd=file&file=chatScript3&site=30218384&&category=en;woman;3'></script>

<!-- END HumanTag Monitor. DO NOT MOVE! MUST BE PLACED JUST BEFORE THE /BODY TAG -->

<? } else { ?>

<!-- Google Conversion Code PAGE VIEWED-->

<script language="JavaScript">

<!--

google_conversion_id = 1072374508;

google_conversion_language = "en_US";

google_conversion_format = "1";

google_conversion_color = "FFFFFF";

if (1.0) {

  google_conversion_value = 1.0;

}

google_conversion_label = "PageView";

-->

</script>

<script language="JavaScript" src="https://www.googleadservices.com/pagead/conversion.js"></script>

<!-- End Google Conversion Code PAGE VIEWED-->

<!-- BEGIN FASTCOUNTER PRO CODE --> 

<script language="JavaScript" src="https://fcstats.bcentral.com/js/13718/0" type="text/javascript"></script>

<!-- END FASTCOUNTER PRO CODE -->

<!-- BEGIN HumanTag Monitor. DO NOT MOVE! MUST BE PLACED JUST BEFORE THE /BODY TAG -->

<script language='javascript' src='https://server.iad.liveperson.net/hc/30218384/x.js?cmd=file&file=chatScript3&site=30218384&&category=en;woman;1'></script>

<!-- END HumanTag Monitor. DO NOT MOVE! MUST BE PLACED JUST BEFORE THE /BODY TAG -->

<? } ?>

</body>

</html>

<?php @mysql_close($shopping_db_link); ?>