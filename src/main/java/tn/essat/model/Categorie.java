package tn.essat.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Categorie {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	    private Integer id;
	    private String nom;
	    private String descripition ;
		public Categorie() {
			super();
			// TODO Auto-generated constructor stub
		}
		public Categorie(Integer id, String type ,String descripition ) {
			super();
			this.id = id;
			this.nom = nom;
			this.descripition = descripition;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getDescripition() {
			return descripition;
		}
		public void setDescripition(String descripition) {
			this.descripition = descripition;
		}
		
}
