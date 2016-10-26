UPDATE cstat.test AS v 
SET c1 = s.new

FROM cstat.codelist AS s,
  cstat.codelist2 AS s1
WHERE v.c1 = s.old

-- not join
select
  v.old
from dataset as v
  left join
  cstat.codelist2
    on v.old = cstat.codelist2.old
where
  cstat.codelist2.old is null