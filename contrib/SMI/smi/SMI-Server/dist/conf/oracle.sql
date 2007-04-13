select 'CV'||H.CONTVTA_PRS_DOCUMENTO Login,H.ubcneg_trtrio_codigo Codigo_punto,
       (select nombre from territorios T
         where H.ubcneg_trtrio_codigo=T.codigo
           and T.negocio='S'
           and T.tpotrt_codigo=15) Nombre_Punto,
       (select P.nombres||' '||P.apellido1 from personas P
         where H.contvta_prs_documento=P.documento) Nombre_Colocador
from horariopersonas H
where (H.fechafinal is null OR  sysdate <= H.FECHAFINAL)
  and (select nombre from territorios T
        where H.ubcneg_trtrio_codigo=T.codigo) NOT LIKE '%MANUAL%'
ORDER BY LOGIN
