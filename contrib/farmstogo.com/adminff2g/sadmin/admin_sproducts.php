<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("ADMIN SUBPRODUCTS");
      
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

<form>
<select name="pt" onchange="this.form.submit()">
            <option value="">SELECT A PRODUCT TYPE </option>
            <? SelecTable($db,'TIPO_PRODUCTOS','ID_TIPO_PRODUCTO','TIPO_PRODUCTO',"",$pt) ?> 
          </select>
</form>
<!-- CREAR -->

<? if($pt) { ?>

<table border="0" cellpadding=2 cellspacing=2>
  <tr bgcolor=ffffff> 
	<form name=fm action="./admin_sproducts2x.php" onsubmit="return Validar()">
	

    <!-- <td valign=top><b>CODE</b><br> 
      <input type="text" name="cod" size=5 maxlength=5>
    </td> -->
        <td valign=top><b>SubProduct</b><br> 
      <input type="text" name="nombre" size=25>
    </td>
   
        <td valign=top><b>Description</b><br> 
      <input type="text" name="descr" size=45>
    </td>
    	   
     <th valign=bottom> 
      <input type="image" src="../images/ingresar.gif" border=0>
      <input type=hidden name=pt value="<?echo $pt?>">
    </th>
  </tr>
  </form>
</table>

<hr noshade size=1 color=9966cc width=350>
<br>
<!--
<a href=""><b>Admin Domicilios / Oficinas</b><br>
-->


<!-- ADMIN -->

<br>
<table border="0" cellpadding=2 cellspacing=1>
  <tr class=htabla bgcolor="#6699cc"> 
  <form name=p action="./admin_sproducts2x.php" method=post>

    <th><font color=ffffff>CODE</font></th> 
    <th><font color=ffffff>SUBPRODUCTO</font></th>
    <th><font color=ffffff>DESC SUBPRODUCTO</font></th>    
    <th><font color=ffffff>Status</th>
    <th><font color=ffffff>Add-On</th>
    <th></th>
   <th><font color=ffffff>Delete</font></th>
  </tr>
		<input type=hidden name=rid value="">
		<input type=hidden name=rdesc value="">
		<input type=hidden name=rvalor value="">
		<input type=hidden name=pt value="<?echo $pt?>">
<?
 $st = mysql_db_query($db,"select * from SUB_TIPO_PRODUCTOS where ID_TIPO_PRODUCTO=$pt order by SUB_TIPO_PRODUCTO");
 echo mysql_error();
 $i=1;
 while ($rs = mysql_fetch_array($st)) {	
	if (($i%2)==0) {$bg="cecece"; }
	else { $bg = "ffffff"; }
	
	$sch = ""; $ach ="";
	if ($rs["STATUS"]) { $sch = " checked=on"; }
	if ($rs["ADDON"]) { $ach = " checked=on"; }
 ?>
  <tr bgcolor="<?echo $bg?>">
    <td  align=center><b><? echo $rs["ID_SUB_TIPO_PRODUCTO"]?></b></td> 
    <td><input type=text size=20 name="n<? echo $rs["ID_SUB_TIPO_PRODUCTO"]?>" value="<? echo $rs["SUB_TIPO_PRODUCTO"]?>"></td>
   <td><textarea cols=50 rows=2 name="x<? echo $rs["ID_SUB_TIPO_PRODUCTO"]?>"><? echo $rs["DESC_SUB_TIPO_PRODUCTO"]?></textarea></td>
   
   <td align=center><input <?echo $sch?> type="checkbox" name="s<? echo $rs["ID_SUB_TIPO_PRODUCTO"]?>" value="1"></td>
   <td align=center><input <?echo $ach?> type="checkbox" name="a<? echo $rs["ID_SUB_TIPO_PRODUCTO"]?>" value="1"></td>       
    
    
    <td><input type=button name=mgrupo value="mod" onclick="document.p.rdesc.value=document.p.x<? echo $rs["ID_SUB_TIPO_PRODUCTO"]?>.value;document.p.rvalor.value=document.p.n<? echo $rs["ID_SUB_TIPO_PRODUCTO"]?>.value;document.p.rid.value='<? echo $rs["ID_SUB_TIPO_PRODUCTO"]?>';document.p.submit;document.p.submit();" ></td>
   <td align=center><input type="checkbox" name="d<? echo $rs["ID_SUB_TIPO_PRODUCTO"]?>" value="1"></td>
  </tr>
<? 
	$i++;
}?>  
</table>





<br>
      <input type="button" value="Process" onclick="this.form.submit;this.form.submit();">
 	  <input type=hidden name=st value="<?echo $st?>">		
   
  </form>
  
<? } ?>  
<?include('../end_admin.php'); ?>

  