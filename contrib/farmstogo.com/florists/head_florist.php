<?php
//initialize the session
if (!isset($_SESSION)) {
  session_start();
}
// ** Logout the current user. **
$logoutAction = $_SERVER['PHP_SELF']."?doLogout=true";
if ((isset($_SERVER['QUERY_STRING'])) && ($_SERVER['QUERY_STRING'] != "")){
  $logoutAction .="&". htmlentities($_SERVER['QUERY_STRING']);
}

if ((isset($_GET['doLogout'])) &&($_GET['doLogout']=="true")){
  //to fully log out a visitor we need to clear the session varialbles
  $_SESSION['S_USER'] = NULL;
  $_SESSION['S_USER_NAME'] = NULL;
  $_SESSION['S_USER_EMAIL'] = NULL;
  unset($_SESSION['S_USER']);
  unset($_SESSION['S_USER_NAME']);
  unset($_SESSION['S_USER_EMAIL']);
	
  $logoutGoTo = "http://www.farmstogo.com/";
  if ($logoutGoTo) {
    header("Location: $logoutGoTo");
    exit;
  }
}
?>
<?

include ('../s/include/functions.php');

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Welcome !!!, Enjoy FarmsToGo.com</title>



<meta name="robots" content="all">

<meta name="description" content="Flowerfarmstogo.Com Direct from the

farms fresh cut flower wholesaler, open to the trade and public. Fedex next

day delivery included in our prices. We specialize in weddings and special

events.">



<META NAME="keywords" CONTENT="florist, wholesale roses, wholesale flowers, roses, flowers, 

discount roses, discount flowers, bulk roses, buy flower online, online flower shop, 

wedding flowers, floral designer, wedding plannersbuy wholesale fresh flowers, 

wholesale fresh flowers, fresh flowers wholesale, fresh wholesale flowers, 

fresh cut flowers in bulk, bulk fresh cut flowers, wholesale flower supply,

flower supply wholesale, wholesale wedding flowers, event flowers, wholesale event flowers, 

special day flowers, wholesale special day flowers, wedding flowers, wholesale flowers,

bulk flowers, flowers wholesale, wholesale, flowers, fresh flowers, cut flowers, fresh cut, 

wholesaler, distributor, buyer, florist, flower shop, grower, rose, long stem roses, red roses,  

carnations, mums, tropical, anthuriums, orchids, funerals, valentines day, easter, mothers day, 

christmas, gay weddings, gay ceremonies, gay flowers, gay weddings, gay, gay weddings, hotels, restaurants, caterers, 

parties, party, special event, gifts ,rose, roses, florist, weddings, presents, grower, bulk, bulk flowers,

bulk wedding, wholesale, wholesale, wholesale, rose, rose, wholesale, wedding, 

wedding, weddings, bride, brides, delivery, stephanotis, daisies, daisies, wholesale bulk roses, 

flowers wedding, flower, baby, shower, plants, gardening, carnations, decorations, party, surprise,love, 

valentines, mother's day, thanksgiving, tropical flowers from Sommer Flowers, fall, color, floral, horticulture,

groom, party, supplies, showers, bridal, surprise, corsages, instructions, planners, grower, farm, present, 

gift, rose, bloom, petals, red, white, pink, yellow, gowns, reception, bridesmaid, ballons, fedex, fresh, wild rose,

black rose, white rose, red rose, online florist, wedding gowns, bridal showers, bridal flowers, direct from grower, 

farm direct, colombia, colombian roses, callas, calla, callas, florist online,

gift idea, birthday gift idea, wedding gift idea, anniversary gift idea, unique

gift idea, christmas gift idea, romantic gift idea, wedding anniversary gift idea, graduation gift idea, baby gift idea,

corporate gift idea, teacher gift idea, unique wedding gift idea ">



<link rel="stylesheet" href="../s/library/estilos.css" type="text/css">

	<link rel="STYLESHEET" type="text/css" href="../s/include/style.css">
	<link href="../s/include/estilo_ajax.css" rel="stylesheet" type="text/css" />
<script language="javascript" type="text/javascript">
function NuevoAjax(){
var xmlhttp=false;
try{
	xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
}catch(e){
	try{
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
 	}catch(E){
		xmlhttp = false;
	}
}

if(!xmlhttp && typeof XMLHttpRequest!='undefined'){
	xmlhttp = new XMLHttpRequest();
}
return xmlhttp;
}
function Cargar(url){
var contenido, preloader;
contenido = document.getElementById('contenido');
preloader = document.getElementById('preloader');
ajax=NuevoAjax();
ajax.open("GET", url,true);
ajax.onreadystatechange=function(){
	if(ajax.readyState==1){
		preloader.innerHTML = "Loading...";
		preloader.style.background = "url('loading.gif') no-repeat";
	}else if(ajax.readyState==4){
		if(ajax.status==200){
			contenido.innerHTML = ajax.responseText;
			preloader.innerHTML = "Done.";
			preloader.style.background = "url('loaded.gif') no-repeat";
		}else if(ajax.status==404){
			preloader.innerHTML = "La página no existe";
		}else{
			preloader.innerHTML = "Error:".ajax.status;
		}
	}
}
ajax.send(null);
}
</script>
<script type="text/javascript">
<!--
var promo = new Array()
promo[0] = 255;
promo[1] = 68;
promo[2] = 103;

i_banner=2;

function rotar_banner(){
  if(document.getElementById('contenido')){
   Cargar("../s/home/d_promo.php?st="+promo[i_banner]);
   window.setTimeout("Cargar('../s/home/d_promo.php?st="+promo[i_banner]+"');",6000)
   i_banner ++;
   i_banner = i_banner % promo.length;
   window.setTimeout("Cargar('../s/home/d_promo.php?st="+promo[i_banner]+"');",6000)
   setTimeout("rotar_banner()",6000);}
} 

// -->
</script>

	<script language="JavaScript" src="crt.js"></script>
	<script language=JavaScript src="menu/menu.js"></script>
	<link href="menu/menu.css" rel=stylesheet>
	<script type="text/JavaScript">
                 function ROUNDED()
                    {
                      settings = {
                        tl: { radius: 20 },
                        tr: { radius: 20 },
                        bl: { radius: 20 },
                        br: { radius: 20 },
                        antiAlias: true,
                        autoPad: false
                      }
                      var divObj = document.getElementById("menu");
                      var cornersObj = new curvyCorners(settings, divObj);
                      cornersObj.applyCornersToAll();
                    }
                
	</script>
	
</head>



<body  topmargin="0" marginheight=0 marginwidth=0 onLoad="rotar_banner();" >

<?
 include('menu/menu_florist.php');
?>


<table border="0" cellpadding="0" cellspacing="0" width="759" align="center" >
  <tr>
    <td colspan="4"><script>ar();</script></td>
  </tr>
  <tr>
    <td width="130" valign=top>
	<p style=" width:133PX; margin-top:8px; height:50px; " align="center" >Customer Care Center<br>
  <a href="http://server.iad.liveperson.net/hc/30218384/?cmd=file&amp;file=visitorWantsToChat&amp;site=30218384&amp;byhref=1" target="chat30218384" onClick="javascript:window.open('http://server.iad.liveperson.net/hc/30218384/?cmd=file&file=visitorWantsToChat&site=30218384&referrer='+document.location,'chat30218384','width=472,height=320');return false;"><img src="../s/imgs/liveh.gif" alt="Customer Care Center" border="1"></a></p><br />
<div style="position:absolute; width:127px; margin-top:10px; height:<? echo $foot."px"; ?>; "   >
	
	<script language="JavaScript">
	
	var nombre = navigator.appName;
var width=127;
 if (nombre == "Microsoft Internet Explorer"){
width=117;
}

<!--
new menu (header, [
{
	'height':  18,
	'width' : 127,
	'firstX' :0,
	'firstY' :5,
	'nextX' : 1,
	'hideAfter' : 200,
	'css'   : 'header',
	'trace' : true
}]);
new menu (menuHierarchy, [
{
	'height':  18,
	'width' : width,
	'firstX' :0,
	'firstY' :22,
	'nextY' : 1,
	'hideAfter' : 200,
	'css'   : 'gurtl0',
	'trace' : true
},
{
	'width' : 150,
	'firstY' :  0,
	'firstX' :128,
	'nextY' : 1,
	'css' : 'gurtl1'
}]);
new menu (footer, [
{
	'height':  18,
	'width' : 127,
	'firstX' :0,
	'firstY' :<?php echo ($foot)+35; ?>,
	'nextX' : 1,
	'hideAfter' : 200,
	'css'   : 'footer',
	'trace' : true
}]);
//-->
</script>
</div>	
	</td>
	<td width="20"  style="background:url(../s/imgs/21.gif) repeat-y;"><img src="../s/imgs/21.gif" width="20" height="10" border="0"></td>
    <td bgcolor="#FFFFFF" valign="top">
<div style="margin-top:5px;">	
<script>sm();</script>
</div>

