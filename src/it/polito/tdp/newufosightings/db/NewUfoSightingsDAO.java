package it.polito.tdp.newufosightings.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.newufosightings.model.Confini;
import it.polito.tdp.newufosightings.model.Pesi;
import it.polito.tdp.newufosightings.model.Sighting;
import it.polito.tdp.newufosightings.model.State;

public class NewUfoSightingsDAO {

	public List<Sighting> loadAllSightings() {
		String sql = "SELECT * FROM sighting";
		List<Sighting> list = new ArrayList<>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);	
			ResultSet res = st.executeQuery();

			while (res.next()) {
				list.add(new Sighting(res.getInt("id"), res.getTimestamp("datetime").toLocalDateTime(),
						res.getString("city"), res.getString("state"), res.getString("country"), res.getString("shape"),
						res.getInt("duration"), res.getString("duration_hm"), res.getString("comments"),
						res.getDate("date_posted").toLocalDate(), res.getDouble("latitude"),
						res.getDouble("longitude")));
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}

		return list;
	}

	public List<State> loadAllStates(Map<String, State> idMap) {
		String sql = "SELECT * FROM state";
		List<State> result = new ArrayList<State>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				if(!idMap.containsKey(rs.getString("id"))) {
					State state = new State(rs.getString("id"), rs.getString("Name"), rs.getString("Capital"),
							rs.getDouble("Lat"), rs.getDouble("Lng"), rs.getInt("Area"), rs.getInt("Population"),
							rs.getString("Neighbors"));
					idMap.put(rs.getString("id"), state);
					result.add(state);
				}else {
					result.add(idMap.get(rs.getString("id")));
				}
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<String> getForma(String anno) {
		final String sql =	"select distinct s.shape as sh " + 
							"from sighting as s " + 
							"where year(s.datetime)=? " + 
							"order by s.shape ";
		List<String> shapes = new LinkedList<>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				String shape = rs.getString("sh");
				shapes.add(shape);
			}

			conn.close();
			return shapes;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Pesi> getPesiStati(Map<String, State> idMap, String anno, String shape) {
		final String sql=	"select st.id as id, count(*) as peso1 " + 
							"from sighting as s, state as st " + 
							"where year(s.datetime)=? " + 
							"and s.shape=? " + 
							"and st.id=s.state " + 
							"group by id";
		List<Pesi> pesi = new LinkedList<>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, anno);
			st.setString(2, shape);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State s = idMap.get(rs.getString("id"));
				if(s!=null) {
					Pesi peso = new Pesi(s, rs.getDouble("peso1"));
					pesi.add(peso);
				}
			}

			conn.close();
			return pesi;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Confini> getConfini(Map<String, State> idMap) {
		final String sql=	"select * " + 
							"from neighbor as n " + 
							"where n.state1 > n.state2";
		List<Confini> confini = new LinkedList<>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State s1 = idMap.get(rs.getString("n.state1"));
				State s2 = idMap.get(rs.getString("n.state2"));
				if(s1!=null && s2!=null) {
					Confini confine = new Confini(s1, s2);
					confini.add(confine);
				}
			}
			conn.close();
			return confini;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}