SELECT CAST(TS.EVENT_TMSTP AS DATE) AS DATE_TENDERED, COUNT(DISTINCT C.UNVSL_PKG_NBR) AS PKGS_TENDERED,
       COUNT(DISTINCT USPS.UNVSL_PKG_NBR) AS SCAN_CNT
FROM SMARTPOST_EDS_PROD_VIEW_DB.FXSP_TRIP_STOP TS
JOIN FXSP_PKG_TRAN_HUB_UPN_W C ON C.TRIP_STOP_NBR = TS.TRIP_STOP_NBR
LEFT JOIN FXSP_POSTAL_DETAIL USPS ON USPS.UNVSL_PKG_NBR = C.UNVSL_PKG_NBR AND
		  USPS.PSTL_EVENT_CD IN ('01','02','04','05','06','09','14','21','22','23','24','25','26','28','29','51','52','53','54','55','56','57')
WHERE TS.EVENT_TMSTP > (CURRENT_DATE - INTERVAL '15' DAY) AND TS.EVENT_TYPE_CD = 'STPAR' AND
      C.UNVSL_PKG_NBR  IN(SELECT DISTINCT UNVSL_PKG_NBR FROM SMARTPOST_PROD_VIEW_DB.fxsp_alternate_pkg_key K WHERE
      (alter_pkg_key_cd = 'POSTALDC91' AND k.pkg_barcd_nbr LIKE '02%') OR (alter_pkg_key_cd = 'POSTALDC92' AND
      (k.pkg_barcd_nbr LIKE '748%' OR k.pkg_barcd_nbr LIKE '490%' OR k.pkg_barcd_nbr LIKE '269%' OR
      k.pkg_barcd_nbr LIKE '419%' OR k.pkg_barcd_nbr LIKE '612%')))
GROUP BY 1
ORDER BY 1