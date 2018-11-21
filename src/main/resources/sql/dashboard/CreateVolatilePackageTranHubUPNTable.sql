CREATE VOLATILE TABLE FXSP_PKG_TRAN_HUB_UPN_W AS
(
SELECT
LAST_CNT.UNVSL_PKG_NBR,
LAST_CNT.SORT_SCAN_TMSTP,
LAST_CNT.ORIG_CNTNR_NM,
LAST_CNT.TRIP_NBR,
LAST_CNT.TRIP_STOP_NBR
FROM  FXSP_PKG_TRAN_HUB_CONTAINER_W  AS HUB_CONTAINER
INNER JOIN
(
      SELECT
      T.UNVSL_PKG_NBR,
      T.ORIG_CNTNR_NM ,
      T.SORT_SCAN_TMSTP,
      T.TRIP_NBR,
      T.TRIP_STOP_NBR
      FROM
      (
            SELECT 
            PD3.UNVSL_PKG_NBR, 
            PD3.CNTNR_NM AS LAST_CNTNR_NM,
            SORT_SCAN.ORIG_CNTNR_NM,
            SORT_SCAN.TRGT_CNTNR_NM,
            SORT_SCAN.SORT_SCAN_TMSTP,
            SORT_SCAN.TRIP_NBR,
            SORT_SCAN.TRIP_STOP_NBR
            FROM  SMARTPOST_EDS_PROD_VIEW_DB.FXSP_PACKAGE_DETAIL AS PD3    
            INNER JOIN
            (
                  SELECT
                  PD2.UNVSL_PKG_NBR,
                  PD2.PKG_EVENT_TMSTP AS SORT_SCAN_TMSTP,
                  UPN.ORIG_CNTNR_NM,
                  UPN.TRGT_CNTNR_NM,
                  UPN.TRIP_NBR,
                  UPN.TRIP_STOP_NBR
                  FROM SMARTPOST_EDS_PROD_VIEW_DB.FXSP_PACKAGE_DETAIL AS PD2 
                  INNER JOIN  
                  (    
                        SELECT 
                        UNVSL_PKG_NBR,
                        PKG_EVENT_TMSTP,
                        ORIG_CNTNR_NM,
                        TRGT_CNTNR_NM,
                        TRIP_NBR,
                        TRIP_STOP_NBR
                        FROM
                        (
                        SELECT 
                        PD1.UNVSL_PKG_NBR           AS UNVSL_PKG_NBR,
                        PD1.PKG_EVENT_TMSTP  AS PKG_EVENT_TMSTP,
                        HUB_CONTAINER.CNTNR_NM      AS ORIG_CNTNR_NM,
                        HUB_CONTAINER.CNTNR_NM      AS TRGT_CNTNR_NM,
                        HUB_CONTAINER.TRIP_NBR,
                        HUB_CONTAINER.TRIP_STOP_NBR
                        FROM       FXSP_PKG_TRAN_HUB_CONTAINER_W AS HUB_CONTAINER                         
                        INNER JOIN SMARTPOST_EDS_PROD_VIEW_DB.FXSP_PACKAGE_DETAIL AS PD1 
                        ON         PD1.CNTNR_NM   =  HUB_CONTAINER.CNTNR_NM 
                        AND        PD1.PKG_EVENT_TYPE_CD IN ('SRTSC','CNTCL')      
                        GROUP BY 1,2,3,4,5,6
                                
                        UNION
                                
                        SELECT 
                        PD1.UNVSL_PKG_NBR            AS UNVSL_PKG_NBR,
                        PD1.PKG_EVENT_TMSTP  AS PKG_EVENT_TMSTP,
                        HUB_CONTAINER.CNTNR_NM       AS ORIG_CNTNR_NM,
                        HUB_CONTAINER.TRGT_CNTNR_NBR AS TRGT_CNTNR_NM,
                        HUB_CONTAINER.TRIP_NBR,
                        HUB_CONTAINER.TRIP_STOP_NBR
                        FROM       FXSP_PKG_TRAN_HUB_CONTAINER_W AS HUB_CONTAINER                         
                        INNER JOIN SMARTPOST_EDS_PROD_VIEW_DB.FXSP_PACKAGE_DETAIL AS PD1 
                        ON         PD1.CNTNR_NM   =  HUB_CONTAINER.TRGT_CNTNR_NBR 
                        AND        PD1.PKG_EVENT_TYPE_CD IN ('SRTSC','CNTCL')      
                        GROUP BY 1,2,3,4,5,6
                        ) AS T1
                        QUALIFY ROW_NUMBER() OVER(PARTITION BY T1.UNVSL_PKG_NBR ORDER BY T1.PKG_EVENT_TMSTP DESC)=1
                        
                  ) AS UPN
                  ON    PD2.UNVSL_PKG_NBR  =  UPN.UNVSL_PKG_NBR
                  AND   PD2.PKG_EVENT_TYPE_CD  IN ('SRTSC','CNTCL')
                  QUALIFY ROW_NUMBER() OVER(PARTITION BY PD2.UNVSL_PKG_NBR ORDER BY PD2.PKG_EVENT_TMSTP ASC) =1
            ) AS SORT_SCAN
            ON   PD3.UNVSL_PKG_NBR = SORT_SCAN.UNVSL_PKG_NBR
            AND  PD3.PKG_EVENT_TYPE_CD IN ('SRTSC','CNTCL')
            QUALIFY ROW_NUMBER() OVER(PARTITION BY PD3.UNVSL_PKG_NBR ORDER BY PD3.PKG_EVENT_TMSTP DESC) =1    
      ) AS T
      WHERE T.LAST_CNTNR_NM = T.TRGT_CNTNR_NM
) AS LAST_CNT
ON LAST_CNT.ORIG_CNTNR_NM= HUB_CONTAINER.CNTNR_NM
GROUP BY 1,2,3,4,5
)
WITH DATA ON COMMIT PRESERVE ROWS