select n11, n22, (peso1+peso2) as peso
from 
(select s.id as s1, n.state1 as n11, n.state2 as n12, count(*) as peso1
	from sighting as si, neighbor as n, state as s
	where si.state=s.id
	and s.id=n.state1
	and si.shape='circle'
	and year(si.datetime)=2000
	group by n.state1) as tab1, 
		(select s2.id as s2, n2.state1 as n21, n2.state2 as n22, count(*) as peso2
		from sighting as si2, neighbor as n2, state as s2
		where si2.state=s2.id
		and s2.id=n2.state2
		and si2.shape='circle'
		and year(si2.datetime)=2000
		group by n2.state2) as tab2
		where n11=n21
		and n12=n22