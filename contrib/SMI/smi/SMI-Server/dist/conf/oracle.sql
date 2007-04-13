SELECT 'CV'||H.CONTVTA_PRS_DOCUMENTO Login,H.ubcneg_trtrio_codigo Codigo_punto,
(SELECT nombre FROM territorios T
WHERE H.ubcneg_trtrio_codigo=T.codigo
AND T.negocio='S'
AND T.tpotrt_codigo=15) Nombre_Punto,
(SELECT P.nombres||' '||P.apellido1 FROM personas P
WHERE H.contvta_prs_documento=P.documento) Nombre_Colocador
FROM horariopersonas H
WHERE (H.fechafinal IS null OR  sysdate <= H.FECHAFINAL)
AND (SELECT nombre FROM territorios T
WHERE H.ubcneg_trtrio_codigo=T.codigo) NOT LIKE '%MANUAL%'
ORDER BY LOGIN
