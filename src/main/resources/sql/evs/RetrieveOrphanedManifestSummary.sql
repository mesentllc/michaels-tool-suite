select sum(sent_pkg_cnt) as count, sum(sent_tot_postage_amt) as postage
from SPEVS_SCHEMA.evs_manifest
where evs_manifest_seq in (:orphans)