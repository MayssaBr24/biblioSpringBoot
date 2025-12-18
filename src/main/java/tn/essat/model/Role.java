package tn.essat.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Role {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Integer id;
 private String role ;
public Role() {
	super();
	// TODO Auto-generated constructor stub
}
public Role(Integer id, String role) {
	super();
	this.id = id;
	this.role = role;
}
public Integer getId() {
	return id;
}
public void setId(Integer id) {
	this.id = id;
}
public String getRole() {
	return role;
}
public void setRole(String role) {
	this.role = role;
} 
}
