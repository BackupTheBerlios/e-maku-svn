/* set "display:none" classes (lets non-javascript users see content) */
var css = '.collapsed * { display: none; /* cursor: pointer */ }';
document.write('<style type="text/css" media="screen"><!--' + css + '--></style>');

/******************************************************************************
 * EVENTX: CASCADING EVENT EXTENSIONS (ADD RULES TO ruleSets LIST)
 *****************************************************************************/
var ruleSets=[

'A.offsite { onclick: return confirm("You are leaving this site. Do you want to continue?") !important }',
'A.popup { onclick: window.open(this.href); return false }',
'A.external { oninit: this.appendChild(document.createTextNode(" (external)")) }',
'A { onmouseover: this.className += " hover" }',
'A { onmouseout: this.className = this.className.replace(/[ ]?\\bhover\\b/, "") }',

'TR { onmouseover: this.className += " hover" }',
'TR { onmouseout: this.className = this.className.replace(/[ ]?\\bhover\\b/, "") }',
//'TR { oninit: this.style.cursor="default" }', // change cursor

'IMG.roll { oninit: loadRollovers(this, "1", "2") }',
'IMG.roll { onmouseover: rollover(this) }',
'IMG.roll { onmouseout: rollout(this) }',

'DIV.collapsed { onclick: removeClass(this, "collapsed") }',
//'DIV.collapsed * { onmouseover: addClass(this, "hover") }',
//'DIV.collapsed * { onmouseout: removeClass(this, "hover") }',

'UL.folding >   LI { oninit: addClass(this, "folded") }',
'UL.folding > LI { onclick: toggleClass(this, "folded", "unfolded") }',
'UL.folding > LI { oninit: this.style.cursor="pointer" }',
'UL.folding > LI * { oninit: this.style.cursor="auto" }',

//'#last_modified { oninit: appendLastModified(this) }', // buggy on Mac/IE
'#last_modified { oninit: this.appendChild(document.createTextNode(document.lastModified)) }', // works on Mac/IE?

'#menu { oninit: insertMenu(this) }',

'FORM INPUT.full_name { onkeyup: validate(this, /^\\w+[,]?([ .]+\\w+)+[ ]*$/) }',
'FORM INPUT.email { onkeyup: validate(this, /^[\\w_.\-]+([+][\\w_.\-]+)?@([\\w\-]+[.])+[\\w]{2,}$/) }',
'FORM INPUT.zip_code { onkeyup: validate(this, /(^\\d{5}(-\\d{4})?$)|(^$)/) }',

/* the other demo fields are simply "required" */
'FORM .required { onkeyup: validate(this, /\\w{3,}/) }', // onkeyup for input & textarea
'FORM .required { onchange: this.onkeyup() }', // map to onkeyup (for cut-paste & file)
'FORM .required { onclick: this.onkeyup() }', // map to onkeyup (for checkbox & radio)
// careful - defining TEXTAREA's onlick can cause a text-select quirk in Opera

'FORM SELECT.required { onclick: validate(this, /^\\w.*/)}',
'FORM SELECT.required { onblur: this.onclick() }', // map to onclick (for kbd-only use)

'FORM INPUT { oninit: if (this.onkeyup) this.onkeyup(); !important }',
'FORM TEXTAREA { oninit: if (this.onkeyup) this.onkeyup(); !important }',
'FORM SELECT { oninit: if (this.onclick) this.onclick(); !important }',

'FORM { onsubmit: return checkForm(this) }'
// note: last rule not followed by comma

];


/******************************************************************************
 * ADD CUSTOM FUNCTIONS BELOW OR TO SEPARATE JS FILE (FOR USE IN ABOVE RULES)
 *****************************************************************************/

function addClass(node, value) {
	node.className += ' ' + value
}
function removeClass(node, value) {
	value = new RegExp('[ ]*\\b' + value + '\\b', 'g');
	node.className = node.className.replace(value, '')
}
function toggleClass(node, value1, value2) {
	var pattern = new RegExp('\\s*\\b' + value1 + '\\b', 'g');
	if (value1 && node.className.match(pattern)) {
		node.className = node.className.replace(pattern, ' ' + value2)
	} else {
		pattern = new RegExp('\\s*\\b' + value2 + '\\b', 'g');
		if (value2 && node.className.match(pattern)) {
			node.className = node.className.replace(pattern, ' ' + value1)
		}
	}
}
function hasClass(node, cname) {
	cname = new RegExp('\\b' + cname + '\\b');
	return cname.test(node)
}

/* image rollover functions v1.0 (by Shawn Brown, www.shawnbrown.com) */
function loadRollovers(x, origSuffix, overSuffix) {
	x._out = new Image();
	x._out.src = x.src;
	x._over = new Image();
	var temp = new RegExp('^(.*)' + origSuffix + '([.][^.]*)$');
	temp = x.src.replace(temp, '$1{?}$2');
	x._over.src = temp.replace('{?}', overSuffix)
	// '{?}' used because '$1'+overSuffix+'$2' is unsafe when overSuffix is number
}
function rollover(x) { if (x._over.complete) x.src = x._over.src }
function rollout(x) { if (x._out.complete) x.src = x._out.src }

/* gets last modified date and appends text to given node */
function appendLastModified(node) {
	var x = document.lastModified;
	x = x.match(/^.*\d{4}/); // use up to 4 dgt year
	x = 'Last modified ' + x[0];
	node.appendChild(document.createTextNode(x))
}

/* uses .innerHTML property to insert markup */
function insertMenu(node) {
	var markup = '<ul>'
		+ '<li><a href="http://someaddress1">Choice 1</a></li>'
		+ '<li><a href="http://someaddress2">Choice 2</a></li>'
		+ '<li><a href="http://someaddress3">Choice 3</a></li>'
		+ '<ul>';
	node.innerHTML = ''; // prevent IE Mac glitch
	node.innerHTML = markup
}

/* validate() v0.8b (by Shawn Brown, www.shawnbrown.com) */
function validate(x, pattern) { // x is form element, pattern is regexp literal
	if (!x.type || (pattern && !pattern.test)) return null;
	var valid = false;
	switch (x.type) {
		case 'text':
		case 'textarea':
		case 'password':
		case 'file': if (pattern.test(x.value)) valid = true; break;
		case 'checkbox': if (x.checked) valid = true; break;
		case 'select-one':
			if (pattern.test(x.options[x.selectedIndex].text)) valid = true;
			break;
		case 'radio':
			var xArr = x.form[x.name]; // get radiobutton's group
			for (var i=0,iMax=xArr.length; i<iMax; i++) {
				if (xArr[i].checked) valid = true;
				if (!xArr[i].onclick)
					xArr[i].onclick = function() { validate(this) };
			}
			break;
		default: return null;
	}
	if (!xArr) var xArr = new Array(x);
	for (var i=0,iMax=xArr.length; i<iMax; i++) { // set/remove "invalid" class
		var invalid = new RegExp('\\s*\\b(invalid|alert)\\b', 'g'); 
		if (valid) xArr[i].className = xArr[i].className.replace(invalid, '');
		else if (!invalid.test(xArr[i].className))
			xArr[i].className += ' invalid';
	}
}

/* checkForm() v0.8b (by Shawn Brown, www.shawnbrown.com) */
function checkForm(form) {
	form = (form.all) ? form.all : form.getElementsByTagName('*');
	for (var i=0,iMax=form.length; i<iMax; i++) {
		var x = form[i];
		var pattern = new RegExp('\\binvalid\\b');
		if (pattern.test(x.className)) { // look for "invalid" className
			/* set focus to error (caret-to-end method based on code by Martin Honnen) */
			if (x.createTextRange
			        && (x.type == 'text' || x.type == 'textarea' || x.type == 'password')) {
				var end = x.createTextRange();
				end.collapse(false);
				end.select()
			} else if (x.setSelectionRange
			        && (x.type == 'text' || x.type == 'textarea' || x.type == 'password')) {
				x.focus(); // FFox throws exception on text types but works fine, the error msg
				           // is a bug in firefox itself and can't be caught within the script -
				           // https://bugzilla.mozilla.org/show_bug.cgi?id=236791

				var end = x.value.length;
				x.setSelectionRange(end, end)
			} else {
				x.focus()
			}
			alert('Please correct the highlighted area.');
			x.className += ' alert'; // set "alert" className
			return false
		}
	}
	for (var i=0,iMax=form.length; i<iMax; i++) {
		if (form[i].type && form[i].type == 'submit') {
			form[i].disabled = true // prevent multiple submissions
		}
	}
	return true
}

/* popup (height & width optional) - not used in demo */
function pop(link, width, height) {
	var optns = 'resizable=yes,'
	          + 'scrollbars=yes,'
	          + 'toolbar=no,'
	          + 'status=no';
	if (!isNaN(width) && !isNaN(height)) {
		optns = optns + ','
		        + 'top=' + ((screen.height - height)/2) + ','
		        + 'left=' + ((screen.width - width)/2) + ','
		        + 'width=' + width + ','
		        + 'height=' + height;
	}
	window.open(link, '', optns);
	return false;
}


/******************************************************************************
 * BEGIN EventX v0.9 (build 1, rel 2) - TO EDIT, SEE EVENTX_FULL_COMMENT.TXT
 * Copyright 2005 Shawn Brown - http://shawnbrown.com/contact
 * License - http://creativecommons.org/licenses/by/2.5/
 * Manner of Attribution - don't remove the copyright notice or contact info
 *****************************************************************************/
var autoRun = true; // if true, eventxMain() runs at onload
var syntaxAlert = false; // if true, alert box displays rule with error

function eventxMain(){if(!document.getElementsByTagName)return null;for(
var i=0,aq=ruleSets.length;i<aq;i++){var al=ruleSets[i].match(av);if(al)af(
al[1],al[2],'function(){'+al[4]+'}',document);else if(syntaxAlert)alert(
'Syntax error in following EventX rule:\n\n'+ruleSets[i])}if(
navigator.userAgent.toLowerCase().indexOf('msie')>-1){if(typeof
window.onunload!='function'){window.onunload=ah}else{var as=window.onunload;
window.onunload=function(){as();ah()}}}}function ah(){for(var i=
ruleSets.length;i;i--){var al=ruleSets[i-1].match(av);if(al)af(al[1],al[2],
'null',document)}}try{var av=new RegExp().compile('^[ ]*([\\w\\d*#+>. ]+)[ ]'
+'*\\{[ ]*([\\w]+)[ ]*:[ ]*(["\']?)(.*)[ ]*\\3[ ]*\\}[ ]*$')}catch(e){var av
=/^[ ]*([\w\d*#+>. ]+)[ ]*\{[ ]*([\w]+)[ ]*:[ ]*(["\']?)(.*)[ ]*\3[ ]*\}[ ]*$/
}function af(an,ap,ab,ak){if(an.lastIndexOf('#')>-1){an=an.substring(
an.lastIndexOf('#'),an.length)}var ac,au;if(an.indexOf(' ')>-1){ac=
an.substring(0,an.indexOf(' '));au=an.substring(an.indexOf(' ')+1,an.length)}
else{ac=an;au=''}if(ac.indexOf('.')>-1){var ad=ac.substring(ac.indexOf('.')
+1,ac.length);ac=ac.substring(0,ac.indexOf('.'))}var aw=[];switch(
ac.substring(0,1)){case'#':ac=ac.substring(1,ac.length);aw[0]=
ak.getElementById(ac);if(!aw[0])return null;break;case'>':ac=ac.substring(
1,ac.length);aw=ak.childNodes;var ax=[];for(var i=0,aq=aw.length;i<aq;i++){
if(aw[i].tagName==ac||ac=='*'){ax[ax.length]=aw[i]}}aw=ax;break;case'+':ac=
ac.substring(1,ac.length);while(ak.nextSibling&&ak.nextSibling.nodeType!=1){
ak=ak.nextSibling}if(ak.nextSibling&&(ak.nextSibling.tagName==ac||ac=='*')){
aw[0]=ak.nextSibling}break;default:if(ac=='*'||(ad&&!ac)){aw=ak.all?ak.all:
ak.getElementsByTagName('*')}else{aw=ak.getElementsByTagName(ac)}}if(ad){var
ax=[];var at=new RegExp('\\b'+ad+'\\b');for(var i=0,aq=aw.length;i<aq;i++){
if(at.test(aw[i].className)){ax[ax.length]=aw[i]}}aw=ax}if(au){for(var i=0,
aq=aw.length;i<aq;i++){af(au,ap,ab,aw[i])}}else{if(ap!='oninit'){for(var i
=0,aq=aw.length;i<aq;i++){eval('aw[i].'+ap+'='+ab)}}
// IF ERROR ON THIS LINE, CHECK YOUR RULE BEHAVIOUR CODE (IN ruleSets[] ARRAY)
else{for(var i=0,aq=aw.length;i<aq;i++){eval('aw[i].aa='+ab);aw[i].aa()}}}}
ag:{try{var ao=new RegExp().compile('[ ][ ]+','g');var aj=new
RegExp().compile('[ ]?>[ ]?','g');var am=new RegExp().compile('[ ]?\\+[ ]?',
'g');var bc=new RegExp().compile('#','g');var az=new RegExp().compile('[.]',
'g');var ba=new RegExp().compile('(^|[ ])[\\w+>*]','g');var bb=new
RegExp().compile('[ ]*[!][ ]*(important|first)[ ]*}$')}catch(e){var ao
=/[ ][ ]+/g;var aj=/[ ]?>[ ]?/g;var am=/[ ]?\+[ ]?/g;var bc=/#/g;var az
=/[.]/g;var ba=/(^|[ ])[\w+>*]/g;var bb=/[ ]*[!][ ]*(important|first)[ ]*}$/
}function ar(ay){return ay?(ay.length<9?ay.length.toString():'9'):'0'}
for(var i=0,aq=ruleSets.length;i<aq;i++){var al=ruleSets[i];var an
=al.substring(0,al.indexOf('{'));var ae=al.substring(al.indexOf('{'),
al.length);an=an.replace(ao,' ');an=an.replace(aj,' >');an=an.replace(am,' +')
;var ai=ar(an.match(bc));ai+=ar(an.match(az));ai+=ar(an.match(ba));ai+='-'+
'00000'.substring(0,5-i.toString().length)+i.toString()+'~';var at=ae.match(
bb);if(at){ai=((at[1]=='important')?'X':'%')+ai;ae=ae.substring(0,
ae.lastIndexOf('!'))+'}'}ruleSets[i]=ai+an+ae}ruleSets.sort();for(var i
=ruleSets.length;i;i--){var al=ruleSets[i-1];ruleSets[i-1]=al.substring(
al.indexOf('~')+1,al.length)}}
/******************************************************************************
 * If autorun is true, add eventxMain() to onload.
 * The following code adapted from Simon Willison's addLoadEvent() function -
 * http://simon.incutio.com/archive/2004/05/26/addLoadEvent
 *****************************************************************************/
if (autoRun === true) {
	if (typeof window.onload != 'function') {
		window.onload = eventxMain
	} else {
		var oldonload = window.onload;
		window.onload = function() { oldonload(); eventxMain() }
	}
}
