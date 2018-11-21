CREATE VOLATILE TABLE CONTAINER_MV
AS 
( 
      SELECT 
      CNTNR_NM AS CNTNR_NM, 
      C.TRIP_NBR AS TRIP_NBR, 
      C.TRIP_STOP_NBR AS TRIP_STOP_NBR
      FROM   SMARTPOST_EDS_PROD_VIEW_DB.FXSP_CONTAINER C
      JOIN FXSP_TRIP_STOP TS ON TS.TRIP_STOP_NBR = C.TRIP_STOP_NBR
      JOIN SMARTPOST_PROD_VIEW_DB.FXSP_LOCATION LOC ON LOC.LOC_NBR = TS.TRIP_STOP_LOC_NBR
      WHERE  CNTNR_EVENT_TYPE_CD ='CNTMV' 
      AND C.TRIP_NBR <> 0
      AND C.TRIP_STOP_NBR <> 0 
      AND TS.EVENT_TMSTP > (CURRENT_DATE - INTERVAL '15' DAY) 
	  AND TS.EVENT_TYPE_CD = 'STPAR'
      AND LOC.LOC_CLASS_NM LIKE '%USPS%'

      QUALIFY ROW_NUMBER() OVER(PARTITION BY CNTNR_NM ORDER BY CNTNR_EVENT_TMSTP DESC)=1     
)  
WITH DATA ON COMMIT PRESERVE ROWS