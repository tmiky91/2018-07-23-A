package it.polito.tdp.newufosightings.model;

public class Pesi {
	
	private State s;
	private double peso;
	public Pesi(State s, double peso) {
		super();
		this.s = s;
		this.peso = peso;
	}
	public State getS() {
		return s;
	}
	public void setS(State s) {
		this.s = s;
	}
	public double getPeso() {
		return peso;
	}
	public void setPeso(double peso) {
		this.peso = peso;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((s == null) ? 0 : s.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pesi other = (Pesi) obj;
		if (s == null) {
			if (other.s != null)
				return false;
		} else if (!s.equals(other.s))
			return false;
		return true;
	}
	
	

}
