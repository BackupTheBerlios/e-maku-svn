/************************************************************************/
/*       	FLOWERDEALERS												*/
/*			M�dulo de validaci�n de formas html							*/
/*			Lenguaje	:JavaScript										*/
/*______________________________________________________________________*/
/*			Investigaci�n y Desarrollo									*/
/*                                                                      */
/*                    		FLOWERDEALERS      	                    	*/
/*       																*/
/************************************************************************/

/************************************************************************/
/* Nombre:		checkForm												*/
/* Objetivo:	Valida la forma que llega como par�metro de entrada.	*/
/*				________________________________________________________*/
/*				Investigaci�n y Desarrollo								*/
/*                                                                      */
/*                    		FLOWERDEALERS    	                     	*/
/************************************************************************/

function checkForm(form)
{
var reEmail = /^.+\@.+\..+$/;
var isOk = true;	


	if (form.name.value == "")
	{
		alert ("Please enter name.");
		form.name.focus();
		isOk = false;
	}else if (form.email.value == ""){
		alert ("Please enter e-mail");
		form.email.focus();	
		isOk = false;
	}else if (form.password.value == ""){
		alert ("Please enter password");
		form.password.focus();	
		isOk = false;
	}else if (form.company.value == "-1"){
		alert ("Please select company"); 
		form.company.focus();
		isOk = false;
	}else if (form.type.value == "-1"){
		alert ("Please select user type"); 
		form.type.focus();
		isOk = false;
	}else if (!reEmail.test(form.email.value)){
	    alert ("Error: E-Mail");
        form.email.focus();
        isOk = false;
	}
	return isOk;
}
