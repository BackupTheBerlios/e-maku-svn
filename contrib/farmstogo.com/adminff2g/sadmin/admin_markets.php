<?
require ('../../functions.php');
include('../head_admin.php');
Titulo_Maestro("ADMIN MARKETS");

?>
<center>
<!-- CONTENIDO -->

<script>
	function Validar()
	{
	if (document.fm.nombre.value=="")
	 { alert ('All data are required'); return false; }
	}
</script>

<center>

<!-- CREAR -->

<table border="0" cellpadding=2 cellspacing=2>
  <tr bgcolor=ffffff>
	<form name=fm action="admin_markets2.php" onsubmit="return Validar()" method=post>

        <td valign=bottom><b>Market</b><br>
      <input type="text" name="nombre" size=25>
    </td>

	<td valign=bottom>Start Date<br>(yyyy-mm-dd)<br>
	<input type=text size=10 maxlength=10 name=sd value="<?echo $rs["START_DATE"]?>" onchange="this.form.ad.value=this.value"></td>

    <td valign=bottom>End Date<br>(yyyy-mm-dd)<br>
    <input type=text size=10 maxlength=10 name=ed value="<?echo $rs["END_DATE"]?>" onchange="this.form.dd.value=this.value"></td>

    <td valign=bottom>Activate Date<br>(yyyy-mm-dd)<br>(Display in the site)<br>
    <input type=text size=10 maxlength=10 name=ad value="<?echo $rs["ACTIVATE_DATE"]?>"></td>

    <td valign=bottom>Desactivate Date<br>(yyyy-mm-dd)<br>(Display in the site)<br>
    <input type=text size=10 maxlength=10 name=dd value="<?echo $rs["DESACTIVATE_DATE"]?>"></td>

    <td valign=bottom>Public
    <input type="checkbox" name="public_market" value="1"></td>

     <th valign=bottom>
      <input type="image" src="../images/ingresar.gif" border=0>
    </th>
  </tr>
  </form>
</table>

<hr noshade size=1 color=9966cc width=350>
<br>



<!-- ADMIN -->

<br>
<table border="0" cellpadding=2 cellspacing=1>
  <tr class=htabla bgcolor="#6699cc">
  <form name=p action="admin_markets2.php" method=post>
    		<input type=hidden name=ct value="<?echo $ct?>">

    <th><font color=ffffff>CODE</font></th>
    <th><font color=ffffff>MARKET</font></th>
    <th><font color=ffffff>START DATE</font></th>
    <th><font color=ffffff>END DATE</font></th>
    <th><font color=ffffff>ACTIVATE DATE</font></th>
    <th><font color=ffffff>DESACTIVATE DATE</font></th>
    <th><font color=ffffff>PUBLIC</font></th>

    <th></th>
   <th><font color=ffffff>Delete</font></th>
  </tr>
		<input type=hidden name=rid value="">
		<input type=hidden name=rvalor value="">
		<input type=hidden name=sd value="">
		<input type=hidden name=ed value="">
		<input type=hidden name=ad value="">
		<input type=hidden name=dd value="">
		<input type=hidden name=public_market value="">

<?
 $st = mysql_db_query($db,"select * from MARKETS order by ID_MARKET");
 echo mysql_error();
 $i=1;
 while ($rs = mysql_fetch_array($st)) {
	if (($i%2)==0) {$bg="cecece"; }
	else { $bg = "ffffff"; }
 ?>
  <tr bgcolor="<?echo $bg?>">
    <td  align=center><b><? echo $rs["ID_MARKET"]?></b></td>
    <td><input type=text name="n<? echo $rs["ID_MARKET"]?>" value="<? echo $rs["MARKET"]?>" size=25></td>
	<td><input type=text name="sd<? echo $rs["ID_MARKET"]?>" size=10 maxlength=10 value="<?echo $rs["START_DATE"]?>"></td>
    <td><input type=text name="ed<? echo $rs["ID_MARKET"]?>" size=10 maxlength=10 value="<?echo $rs["END_DATE"]?>"></td>
    <td><input type=text name="ad<? echo $rs["ID_MARKET"]?>" size=10 maxlength=10 value="<?echo $rs["ACTIVATE_DATE"]?>"></td>
    <td><input type=text name="dd<? echo $rs["ID_MARKET"]?>" size=10 maxlength=10 value="<?echo $rs["DESACTIVATE_DATE"]?>"></td>
    <td><input type="checkbox" name="public_market<? echo $rs["ID_MARKET"]?>" value="1" <? if ($rs["public"]==1) echo "checked";?>></td>

    <td><input type=button name=mgrupo value="mod" onclick="document.p.sd.value=document.p.sd<? echo $rs["ID_MARKET"]?>.value;document.p.ed.value=document.p.ed<? echo $rs["ID_MARKET"]?>.value;document.p.ad.value=document.p.ad<? echo $rs["ID_MARKET"]?>.value;document.p.dd.value=document.p.dd<? echo $rs["ID_MARKET"]?>.value;document.p.rvalor.value=document.p.n<? echo $rs["ID_MARKET"]?>.value;document.p.rid.value='<? echo $rs["ID_MARKET"]?>';if(document.p.public_market<? echo $rs["ID_MARKET"]?>.checked) { document.p.public_market.value=1;}document.p.submit;document.p.submit();" ></td>


   <td align=center><input type="checkbox" name="d<? echo $rs["ID_MARKET"]?>" value="1"></td>
  </tr>
<?
	$i++;
}?>
</table>





<br>
      <input type="button" value="Process" onclick="this.form.submit;this.form.submit();">
 	  <input type=hidden name=st value="<?echo $st?>">

  </form>


<?include('../end_admin.php'); ?>

