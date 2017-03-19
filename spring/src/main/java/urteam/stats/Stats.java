package urteam.stats;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Stats {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	
	private long userId;
	
	private String sportName;
	private double totalSesionTime;
	private String date;
	
	public Stats(){}
	
	
	public Stats(long userId, String sport, double totalSesionTime, String date) {
		super();
		this.userId = userId;
		this.sportName = sport;
		this.totalSesionTime = totalSesionTime;
		this.date = date;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getSport() {
		return sportName;
	}
	public void setSport(String sport) {
		this.sportName = sport;
	}
	public double getTotalSesionTime() {
		return totalSesionTime;
	}
	public void setTotalSesionTime(double totalSesionTime) {
		this.totalSesionTime = totalSesionTime;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	

}
