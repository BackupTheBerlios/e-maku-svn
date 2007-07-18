<? include ('head_florist.php'); ?>
<script>
	function OpenX(page) {
		window.open(page,'CS','toolbars=no,resizing=no,width=500,height=350')
	}	
</script>
 <script type="text/javascript">

 function AC_AddExtension(src, ext)

{

  if (src.indexOf('?') != -1)

    return src.replace(/\?/, ext+'?'); 

  else

    return src + ext;

}



function AC_Generateobj(objAttrs, params, embedAttrs) 

{ 

  var str = '<object ';

  for (var i in objAttrs)

    str += i + '="' + objAttrs[i] + '" ';

  str += '>';

  for (var i in params)

    str += '<param name="' + i + '" value="' + params[i] + '" /> ';

  str += '<embed ';

  for (var i in embedAttrs)

    str += i + '="' + embedAttrs[i] + '" ';

  str += ' ></embed></object>';



  document.write(str);

}



function AC_FL_RunContent(){

  var ret = 

    AC_GetArgs

    (  arguments, ".swf", "movie", "clsid:d27cdb6e-ae6d-11cf-96b8-444553540000"

     , "application/x-shockwave-flash"

    );

  AC_Generateobj(ret.objAttrs, ret.params, ret.embedAttrs);

}



function AC_SW_RunContent(){

  var ret = 

    AC_GetArgs

    (  arguments, ".dcr", "src", "clsid:166B1BCA-3F9C-11CF-8075-444553540000"

     , null

    );

  AC_Generateobj(ret.objAttrs, ret.params, ret.embedAttrs);

}



function AC_GetArgs(args, ext, srcParamName, classid, mimeType){

  var ret = new Object();

  ret.embedAttrs = new Object();

  ret.params = new Object();

  ret.objAttrs = new Object();

  for (var i=0; i < args.length; i=i+2){

    var currArg = args[i].toLowerCase();    



    switch (currArg){	

      case "classid":

        break;

      case "pluginspage":

        ret.embedAttrs[args[i]] = args[i+1];

        break;

      case "src":

      case "movie":	

        args[i+1] = AC_AddExtension(args[i+1], ext);

        ret.embedAttrs["src"] = args[i+1];

        ret.params[srcParamName] = args[i+1];

        break;

      case "onafterupdate":

      case "onbeforeupdate":

      case "onblur":

      case "oncellchange":

      case "onclick":

      case "ondblClick":

      case "ondrag":

      case "ondragend":

      case "ondragenter":

      case "ondragleave":

      case "ondragover":

      case "ondrop":

      case "onfinish":

      case "onfocus":

      case "onhelp":

      case "onmousedown":

      case "onmouseup":

      case "onmouseover":

      case "onmousemove":

      case "onmouseout":

      case "onkeypress":

      case "onkeydown":

      case "onkeyup":

      case "onload":

      case "onlosecapture":

      case "onpropertychange":

      case "onreadystatechange":

      case "onrowsdelete":

      case "onrowenter":

      case "onrowexit":

      case "onrowsinserted":

      case "onstart":

      case "onscroll":

      case "onbeforeeditfocus":

      case "onactivate":

      case "onbeforedeactivate":

      case "ondeactivate":

      case "type":

      case "codebase":

        ret.objAttrs[args[i]] = args[i+1];

        break;

      case "width":

      case "height":

      case "align":

      case "vspace": 

      case "hspace":

      case "class":

      case "title":

      case "accesskey":

      case "name":

      case "id":

      case "tabindex":

        ret.embedAttrs[args[i]] = ret.objAttrs[args[i]] = args[i+1];

        break;

      default:

        ret.embedAttrs[args[i]] = ret.params[args[i]] = args[i+1];

    }

  }

  ret.objAttrs["classid"] = classid;

  if (mimeType) ret.embedAttrs["type"] = mimeType;

  return ret;

}

 </script>
	<!-- layaout -->
	<script >
	function toggle(){
var theTable = (document.getElementById('orders'));
var theTB = theTable;
if ((theTB.style.display=="")||(theTB.style.display=="block"))
theTB.style.display="none";
else
theTB.style.display="block";
}
</script>
	
		<br>
		<img src="../s/imgs/px.gif" width="1" height="10" border="0" align="top"><br>
		<!-- /layaout -->
     
	    <table height="800" border="0"  align="center" cellpadding="0" cellspacing="0">
          <tr>
            <td colspan="3" valign="top" >&nbsp;</td>
          </tr>
          <tr>
            <td colspan="3" valign="top" >&nbsp;</td>
          </tr>
          <tr>
            <td colspan="3" valign="top" > <center>
			    <img src="../s/imgs/h_welcome.gif" width="360" height="128" alt="Wholesale Flowers" border="0" /><br />
		 Wholesale Flowers. Direct from the farm 24 hour <font color=red>Free Delivery </font><br />
		  Wedding Flowers Specialists
		    </center></td>
          </tr>
          <tr>
            <td colspan="3" valign="top" ><div align="justify"><strong><br>
            Save hundreds</strong> of dollars buying from on-line wholesale florist <strong>farmstogo.com</strong>. All our flowers are shipped direct form our farms in South America and arrive absolutely fresh. We have the lowest wholesale prices available in the market guaranteed. Product is delivered directly to your door via FedEx Overnight Priority at no extra charge</div>
              <br />
<center>
                <font size=2><a href="../s/home/packing.php"><b>See what you will get</b></a>
            </center></td>
          </tr>
          <tr>
            <td colspan="3" valign="top" ><div align="center" style="font-size:10px";> <b><br>
            </b></div>
              <br />

<center>
</center></td>
          </tr>
          <tr>
            <td valign="top" >
              <br />
			
              <table border="0" cellpadding="0" cellspacing="0" width="184">
                <tr>
                  <td><img src="../s/imgs/c11.gif" alt="1" width="184" height="16" border="0" /></td>
                </tr>
                <tr>
                  <td class="cab" valign="top"><div align="center"><a target="other" href="http://www.federalexpress.com/us/tracking/"><img src="../s/imgs/express_logo.gif" alt="LOGO" width="136" height="49" border="0" /></a></div>
              <div style="text-align:justify; font-size:10px";> <b>FedEx International Priority </b>delivers before 10:30 am. Contact 1-800-GOFEDEX to 
                
                confirm service availability to your area.
                <!-- <b><a target=other href="http://www.federalexpress.com/us/tracking/">Click 

              to Track Your Order </a></b> -->
                  <center>
                  <table bgcolor="ffff77">
                    <tr>
                      <form method="get" action="../s/home/tracking.php">
                        <td align="center"><b>Track you order by Order No.<br />
                            <input type="text" name="po_no" size="10" />
                            <input type="submit" name="Track" value="Track" />
                            <br />
                          &nbsp;</td>
                      </form>
                    </tr>
                  </table>
              </div></td>
                </tr>
                <tr>
                  <td><img src="../s/imgs/c12.gif" alt="" width="184" height="16" border="0" /></td>
                </tr>
              </table>
              <br><center>
			  <font size=2>Order Online<br />
              Or Call 1800 3834959 <br />
              </font></center>			  </td>
            <td width="10"><img src="../s/imgs/px.gif" alt="" width="10" height="10" border="0" /><br />			</td>
            <td valign="top">
<div id="contenido">Review Our Seasson Offers</div>
              <br />
                <!-- MAIN TEXTO -->                </td>
          </tr>
          <tr>
            <td colspan="3" valign="top" ></td>
          </tr>
          <tr>
            <td colspan="3" valign="top" ><div id="preloader" ></div></td>
          </tr>	
        </table>
	

		<?php require('end.php');?>
	