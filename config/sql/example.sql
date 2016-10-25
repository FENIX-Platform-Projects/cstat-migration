UPDATE cstat.test AS v 
SET c1 = s.new,

FROM cstat.codelist AS s,
  cstat.codelist2 AS s1
WHERE v.c1 = s.old 