<?php
##################
#  CyberSource Hosted Order Page library.  Inserts fields into the
#  checkout form for posting data to the CyberSource Hosted Order
#  Page.


function php_hmacsha1($data, $key) {
  $klen = strlen($key);
  $blen = 64;
  $ipad = str_pad("", $blen, chr(0x36));
  $opad = str_pad("", $blen, chr(0x5c));

  if ($klen <= $blen) {
    while (strlen($key) < $blen) {
      $key .= "\0";
    }				#zero-fill to blocksize
  } else {
    $key = cybs_sha1($key);	#if longer, pre-hash key
  }
  $key = str_pad($key, strlen($ipad) + strlen($data), "\0");
  return cybs_sha1(($key ^ $opad) . cybs_sha1($key ^ $ipad . $data));
}

# calculates SHA-1 digest of the input string
# cleaned up from John Allen's "SHA in 8 lines of perl5"
# at http://www.cypherspace.org/~adam/rsa/sha.html
#
# returns the hash in a (binary) string

function cybs_sha1($in) {
  $indx = 0;
  $chunk = "";

  $A = array(1732584193, 4023233417, 2562383102,  271733878, 3285377520);
  $K = array(1518500249, 1859775393, 2400959708, 3395469782);
  $a = $b = $c = $d = $e = 0;
  $l = $p = $r = $t = 0;

  do{
    $chunk = substr($in, $l, 64);
    $r = strlen($chunk);
    $l += $r;

    if ($r<64 && !$p++) {
      $r++;
      $chunk .= "\x80";
    }
    $chunk .= "\0\0\0\0";
    while (strlen($chunk) % 4 > 0) { 
      $chunk .= "\0";
    }
    $len = strlen($chunk) / 4;
    if ($len > 16) $len = 16;
    $fmt = "N" . $len;
    $W = array_values(unpack($fmt, $chunk));
    if ($r < 57 ) { 
      while (count($W) < 15) {
	array_push($W, "\0");
      }
      $W[15] = $l*8;
    }

    for ($i = 16; $i <= 79; $i++) {
      $v1 = d($W, $i-3);
      $v2 = d($W, $i-8);
      $v3 = d($W, $i-14);
      $v4 = d($W, $i-16);
      array_push($W, L($v1 ^ $v2 ^ $v3 ^ $v4, 1));
    }

    list($a,$b,$c,$d,$e)=$A;

    for ($i = 0; $i<=79; $i++) {
      $t0 = 0;
      switch(intval($i/20)) {
	case 1:
	case 3:
	$t0 = F1($b, $c, $d);
	break;
	case 2:
	$t0 = F2($b, $c, $d);
	break;
      default:
	$t0 = F0($b, $c, $d);
	break;
      }
      $t = M($t0 + $e  + d($W, $i) + d($K, $i/20) + L($a, 5));
      $e = $d;
      $d = $c;
      $c = L($b,30);
      $b = $a;
      $a = $t;
    }

    $A[0] = M($A[0] + $a);
    $A[1] = M($A[1] + $b);
    $A[2] = M($A[2] + $c);
    $A[3] = M($A[3] + $d);
    $A[4] = M($A[4] + $e);

  }while ($r>56);
  $v = pack("N*", $A[0], $A[1], $A[2], $A[3], $A[4]);
  return pack ("H*", sha1 ($in)); 
  //return $v;
}

#### Ancillary routines used by sha1

function dd($x) {
  if (defined($x)) return $x;
  return 0;
}

function d($arr, $x) {
  if ($x < count($arr)) return $arr[$x];
  return 0;
}

function F0($b, $c, $d) {
  return $b & ($c ^ $d) ^ $d;
}

function F1($b, $c, $d) {
  return $b ^ $c ^ $d;
}

function F2($b, $c, $d) {
  return ($b | $c) & $d | $b & $c;
}

# ($num)
function M($x) {
  $m = 1+~0;
  if ($m == 0) return $x;
  return($x - $m * intval($x/$m));
}

# ($string, $count)
function L($x, $n) { 
  return ( ($x<<$n) | ((pow(2, $n) - 1) & ($x>>(32-$n))) );
}

####
#### end of HMAC SHA1 implementation #####




####
#### HOP functions
#### Copyright 2003, CyberSource Corporation.  All rights reserved.
####

function getmicrotime(){ 
  list($usec, $sec) = explode(" ",microtime());
  $usec = (int)((float)$usec * 1000);
  while (strlen($usec) < 3) { $usec = "0" . $usec; }
  return $sec . $usec;
}


function hopHash($data, $key) {
    return base64_encode(php_hmacsha1($data, $key));
}

function getMerchantID() { return  "v6513620"; }
function getPublicKey()  { return "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpQtCq8JtUbEWFnQZiP+mECrbnImdsk1uLhyZ2r1W20opPVaKMBNUOwkF84lfFqgDPLB69e+afSH+KhfU5uyCfUEzk6s8uNWUXVQbL2e87W9OLZqQpjDe7t7P4G8YF+JfO3GDQWuFyL4MiGRPnFLILDS6bL8ER3DPtreuCxlvWnQIDAQAB"; }
function getPrivateKey() { return "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKlC0Krwm1RsRYWdBmI/6YQKtuciZ2yTW4uHJnavVbbSik9VoowE1Q7CQXziV8WqAM8sHr175p9If4qF9Tm7IJ9QTOTqzy41ZRdVBsvZ7ztb04tmpCmMN7u3s/gbxgX4l87cYNBa4XIvgyIZE+cUsgsNLpsvwRHcM+2t64LGW9adAgMBAAECgYEAl8gLuQ5ULCt/WGbMP7SSYc7931zRo+Ksd/10938ZfP1l++iN/8Ohjz+RXDzYeq+NVDp7Qm3mDDYF8SFZGzD7+YvA9XJEdgHmIPhJq14q1d2VZQXhtwI5tuNt1soBVN0/5X8qLE0QJF1ctSUlmjI3gVfUXxdG2iiV1yXh/Ow3HaECQQDRTK0RPXF0QLo6WEvcYe2YcnTIT2Pgu94giP/yiv6+vItikVy/kXZrXUy2V/5cHvrJx5VUQrg1d7H3I7cIdK31AkEAzwcc5waGntMX+fgoX7FPhW5Phkl1wjOozsl0Q+gL2GDMjSXzpsIXpaIoMdEaozsusFJx/2D79+xSGvAwtIY1CQJAa66dZD9OfddHePkwnoQrr8FDYEOoSVme4PJbbLgidJOgyueq5ky6tmZcL3x6O38c3G+43o8tIyUgBNz1MH6HeQJAJU6g1LkGVWSw82jWzbfEHsVsK0TdpatthiYjf4E3cTywX9cw+yIK5Nw95gxKgpPNrBSPWnf9sAW0HgqenEl7EQJAKFKYfN3T6Sbdct6P1quiS4C4w5zcL2Kgr4nKYlMrY+5zz8iB2UJumjIARGVdklFxBT2vHMB8KZSK4UZxfD9hYw=="; }

#### HOP integration function
function InsertSignature($amount) {
  $merchantID = getMerchantID();
  $timestamp = getmicrotime();
  $data = $merchantID . $amount . $timestamp;
  $pub = getPublicKey();
  $pvt = getPrivateKey();
  $pub_digest = hopHash($data, $pub);
  $pvt_digest = hopHash($data, $pvt);

  echo('<input type="hidden" name="amount" value="' . $amount . '">' . "\n");
  echo('<input type="hidden" name="orderPage_timestamp" value="' . $timestamp . '">' . "\n");
  echo('<input type="hidden" name="merchantID" value="' . $merchantID . '">' . "\n");
  echo('<input type="hidden" name="orderPage_signaturePublic" value="' . $pub_digest . '">' . "\n");
  echo('<input type="hidden" name="orderPage_signaturePrivate" value="' . $pvt_digest . '">' . "\n");
}

function VerifySignature($data, $signature) {
    $pub = getPublicKey();
    $pub_digest = hopHash($data, $pub);
    return strcmp($pub_digest, $signature) == 0;
}

?>
