package it.polito.tdp.newufosightings.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.newufosightings.model.Avvistamenti;
import it.polito.tdp.newufosightings.model.Confini;
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
					result.add(state);
					idMap.put(rs.getString("id"), state);
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

	public List<String> getAllShapes(String anno) {
		final String sql=	"select distinct s.shape as forma " + 
							"from sighting as s " + 
							"where year(s.datetime)=? " + 
							"order by s.shape";
		List<String> shapes = new LinkedList<>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, Integer.parseInt(anno));
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				String shape = rs.getString("forma");
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

	public List<Confini> getConfini(Map<String, State> idMap) {
		final String sql= 	"select n.state1 as n1, n.state2 as n2 " + 
							"from neighbor as n " + 
							"where n.state1 > n.state2";
		List<Confini> confini = new LinkedList<>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State s1 = idMap.get(rs.getString("n1"));
				State s2 = idMap.get(rs.getString("n2"));
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

	public List<Avvistamenti> getAvvistamenti(Map<String, State> idMap, String shape, String anno) {
		final String sql =	"select st.id as id, count(*) as cnt " + 
							"from sighting as s, state as st " + 
							"where s.state=st.id " + 
							"and year(s.datetime)=? " + 
							"and s.shape=? " + 
							"group by id";
		List<Avvistamenti> avvistamenti = new LinkedList<>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, Integer.parseInt(anno));
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				State s = idMap.get(rs.getString("id"));
				if(s!=null) {
					Avvistamenti avvistamento = new Avvistamenti(s, rs.getDouble("cnt"));
					avvistamenti.add(avvistamento);
				}
			}

			conn.close();
			return avvistamenti;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

}
