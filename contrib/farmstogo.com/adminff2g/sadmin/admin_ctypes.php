
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

<? 
require ('../../functions.php'); 
include('../head_admin.php');
Titulo_Maestro("ADMIN CUSTOMER TYPES");
      
?>
<table border="0" cellpadding=2 cellspacing=2>
  <tr bgcolor=ffffff> 
	<form name=fm action="admin_ctypes2.php" onsubmit="return Validar()" method=post>
	
        <td valign=top><b>Customer Type</b><br> 
      <input type="text" name="nombre" size=25>
    </td>

        <td valign=top><b>Description</b><br> 
      <input type="text" name="desc" size=45>
    </td>

     <th valign=bottom> 
      <input type="image" src="../images/ingresar.gif" border=0>
    </th>
  </tr>
  </form>
</table>

<hr noshade size=1 color=9966cc width=350>
<br>

<table border="0" cellpadding=2 cellspacing=1>
  <tr class=htabla bgcolor="#6699cc"> 
  <form name=p action="admin_ctypes2.php" method=post>

	 <th><font color=ffffff>CODE</font></th> 
    <th><font color=ffffff>NAME</font></th>
    <th><font color=ffffff>DESC</font></th>
    <th><font color=ffffff>Status</th>
	<th></th>
   <th><font color=ffffff>Delete</font></th>
  </tr>
		<input type=hidden name=rid value="">
		<input type=hidden name=rdesc value="">
		<input type=hidden name=ctds value="">
<?
 $st = mysql_db_query($db,"select * from m_ctypes order by ctype_name");
 echo mysql_error();
 $i=1;
 while ($rs = mysql_fetch_array($st)) {	
	if (($i%2)==0) {$bg="cecece"; }
	else { $bg = "ffffff"; }
	
	$sch = "";
	if ($rs["status"]) { $sch = " checked=on"; }
 ?>
  <tr bgcolor="<?echo $bg?>">
   <td  align=center><b><? echo $rs["id_ctype"]?></b></td> 
    <td><input type=text size=20 name="n<? echo $rs["id_ctype"]?>" value="<? echo $rs["ctype_name"]?>"></td>
    <td><textarea cols=40 rows=2 name="d<? echo $rs["id_ctype"]?>"><? echo $rs["ctype_desc"]?></textarea></td>   
   <td align=center><input <?echo $sch?> type="checkbox" name="s<? echo $rs["id_ctype"]?>" value="1"></td>    
    
    <td><input type=button name=mgrupo value="mod" onclick="document.p.ctds.value=document.p.d<? echo $rs["id_ctype"]?>.value;document.p.rdesc.value=document.p.n<? echo $rs["id_ctype"]?>.value;document.p.rid.value='<? echo $rs["id_ctype"]?>';document.p.submit;document.p.submit();" ></td>
   <td align=center><input type="checkbox" name="d<? echo $rs["id_ctype"]?>" value="1"></td>
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

  