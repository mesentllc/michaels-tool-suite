CREATE VOLATILE TABLE  FXSP_PKG_TRAN_HUB_CONTAINER_W AS
(
      SELECT 
      MV.CNTNR_NM, 
      MV.TRIP_NBR, 
      MV.TRIP_STOP_NBR,
      MG.TRGT_CNTNR_NBR
      FROM CONTAINER_MV AS MV
      LEFT OUTER JOIN CONTAINER_MG AS MG
      ON MV.CNTNR_NM = MG.CNTNR_NM
)WITH DATA ON COMMIT PRESERVE ROWS
