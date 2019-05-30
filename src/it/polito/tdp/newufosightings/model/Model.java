package it.polito.tdp.newufosightings.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;

public class Model {
	
	private SimpleWeightedGraph<State, DefaultWeightedEdge> grafo;
	private Map<String, State> idMap;
	private NewUfoSightingsDAO dao;
	
	public Model() {
		dao = new NewUfoSightingsDAO();
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		idMap = new HashMap<>();
		dao.loadAllStates(idMap);
	}

	public boolean isDigit(String anno) {
		if(!anno.matches("\\d{4}")) {
			return false;
		}
		int annoValido = Integer.parseInt(anno);
		return annoValido>=1910 && annoValido<=2014;
	}

	public List<String> getForma(String anno) {
		NewUfoSightingsDAO dao = new NewUfoSightingsDAO();
		return dao.getForma(anno);
	}

	public String stampaPesiGrafo(String anno, String shape) {
		String risultato="";
		List<Confini> confini = dao.getConfini(idMap);
		List<Pesi> pesi = dao.getPesiStati(idMap, anno, shape);
		Graphs.addAllVertices(grafo, idMap.values());
		for(Confini c: confini) {
			double peso1=0;
			double peso2=0;
			for(Pesi p: pesi) {
				if(p.getS()==c.getS1()) {
					peso1=p.getPeso();
				}
				else if(p.getS()==c.getS2()) {
					peso2=p.getPeso();
				}
			}
			DefaultWeightedEdge edge = grafo.getEdge(c.getS1(), c.getS2());
			if(edge==null) {
				Graphs.addEdgeWithVertices(grafo, c.getS1(), c.getS2(), (peso1+peso2));
			}
		}
		System.out.println("Vertici: "+grafo.vertexSet().size()+" Archi: "+grafo.edgeSet().size()+"\n");
		for(DefaultWeightedEdge edge: grafo.edgeSet()) {
			System.out.println(grafo.getEdgeWeight(edge)+"\n");
		}
		for(State s: grafo.vertexSet()) {
			List<State> vicini = Graphs.neighborListOf(grafo, s);
			double result=0;
			for(State s2: vicini) {
				DefaultWeightedEdge edge = grafo.getEdge(s, s2);
				result+=grafo.getEdgeWeight(edge);
				
			}
			risultato+="Stato: "+s.getId()+" Somma pesi archi adiacenti: "+result+"\n";
		}
		return risultato;
	}

}
