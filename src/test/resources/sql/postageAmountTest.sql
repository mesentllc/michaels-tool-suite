select postage_amt as payment
from SPEVS_SCHEMA.postage_transaction
WHERE pkg_id = '61292701028129566590'
--JOIN SPEVS_SCHEMA.package p ON p.pkg_id = pt.pkg_id and p.pkg_id = '61292701028129566590' and p.mail_class = 'PS'