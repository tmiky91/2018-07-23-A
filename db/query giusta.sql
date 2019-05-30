select s11, pesoState1, s21, pesoState2, (pesoState1 + pesoState2) as pesoTot
from (select n.state1 s11, n.state2 s21, si.shape shape1, count(*) pesoState1
		from neighbor n, state s, sighting si
		where s.id=n.state1
		and s.id=si.state
		and shape='circle'
		and YEAR(si.datetime)=2000
		group by n.state1) as tab1,
		(select n.state1 s12, n.state2 s22, si.shape shape2, count(*) pesoState2
		from neighbor n, state s, sighting si
		where s.id=n.state1
		and s.id=si.state
		and shape='circle'
		and YEAR(si.datetime)=2000
		group by n.state2) as tab2
where s11=s12
and s21=s22