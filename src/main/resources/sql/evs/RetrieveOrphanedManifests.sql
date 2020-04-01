select e.evs_manifest_seq from SPEVS_SCHEMA.evs_manifest e
where e.tran_id = :tran_id and e.evs_manifest_seq not in
  (select em.evs_manifest_seq from SPEVS_SCHEMA.evs_manifest em
   join SPEVS_SCHEMA.package p on p.evs_manifest_seq = em.evs_manifest_seq and em.tran_id = :tran_id)