package it.polito.tdp.newufosightings.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.newufosightings.db.NewUfoSightingsDAO;

public class Model {
	
	private Map<String, State> idMap;
	private SimpleWeightedGraph<State, DefaultWeightedEdge> grafo;
	
	public Model() {
		idMap = new HashMap<>();
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
	}

	public boolean isDigit(String anno) {
		if(anno.matches("\\d{4}")) {
			return true;
		}
		int annoValido = Integer.parseInt(anno);
		return annoValido>=1910 && annoValido<=2014;
	}

	public List<String> getShapes(String anno) {
		NewUfoSightingsDAO dao = new NewUfoSightingsDAO();
		return dao.getAllShapes(anno);
	}

	public String creaGrafo(String shape, String anno) {
		NewUfoSightingsDAO dao = new NewUfoSightingsDAO();
		String risultato="";
		dao.loadAllStates(idMap);
		List<Confini> confini = dao.getConfini(idMap);
		List<Avvistamenti> pesi = dao.getAvvistamenti(idMap, shape, anno);
		Graphs.addAllVertices(grafo, idMap.values());
		for(Confini c: confini) {
			double peso1=0;
			double peso2=0;
			for(Avvistamenti a: pesi) {
				if(a.getS().equals(c.getS1())) {
					peso1=a.getPeso();
				}
				if(a.getS().equals(c.getS2())) {
					peso2=a.getPeso();
				}
			}
			DefaultWeightedEdge edge = grafo.getEdge(c.getS1(), c.getS2());
			if(edge==null) {
				Graphs.addEdgeWithVertices(grafo, c.getS1(), c.getS2(), peso1+peso2);
			}
		}
		double somma=0;
		for(State s: grafo.vertexSet()) {
			List<State> vicini = Graphs.neighborListOf(grafo, s);
			for(State s1: vicini) {
				DefaultWeightedEdge edge = grafo.getEdge(s, s1);
				somma+=grafo.getEdgeWeight(edge);
			}
			risultato+=s.getId()+" Somma pesi archi adiacenti: "+somma+"\n";
		}
		return risultato;
	}

}
