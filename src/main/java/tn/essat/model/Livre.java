package tn.essat.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
public class Livre {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	  private Integer id;
	    private String titre;
	    private String auteur;
	    private String description;
	    private String image;

	    @ManyToOne
	    @JoinColumn(name = "categorie_id")
        @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
        @JsonIdentityReference(alwaysAsId = true) // Sérialise seulement l'ID
	    private Categorie categorie;

	    private Integer exemplaires ;
	    private Integer disponibles ;
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getTitre() {
			return titre;
		}
		public void setTitre(String titre) {
			this.titre = titre;
		}
		public String getAuteur() {
			return auteur;
		}
		public void setAuteur(String auteur) {
			this.auteur = auteur;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getImage() {
			return image;
		}
		public void setImage(String image) {
			this.image = image;
		}
		public Categorie getCategorie() {
			return categorie;
		}
		public void setCategorie(Categorie categorie) {
			this.categorie = categorie;
		}
		
		public Integer getExemplaires() {
			return exemplaires;
		}
		public void setExemplaires(Integer exemplaires) {
			this.exemplaires = exemplaires;
		}
		public Integer getDisponibles() {
			return disponibles;
		}
		public void setDisponibles(Integer disponibles) {
			this.disponibles = disponibles;
		}
		public Livre(Integer id, String titre, String auteur, String description, String image, Categorie categorie,
				int exemplaires, int disponibles) {
			super();
			this.id = id;
			this.titre = titre;
			this.auteur = auteur;
			this.description = description;
			this.image = image;
			this.categorie = categorie;
			this.exemplaires = exemplaires;
			this.disponibles = disponibles;
		}
		public Livre() {
			super();
			// TODO Auto-generated constructor stub
		}

}
