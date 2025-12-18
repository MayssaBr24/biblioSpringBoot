package tn.essat.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
public class Emprunt {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Integer id;

	    @ManyToOne
	    @JoinColumn(name = "Livre_id")
	    private Livre livre;
	    @ManyToOne
	    @JoinColumn(name = "User_id")
	    private User user;
		private LocalDate dateEmprunt;
	    private LocalDate dateRetour;
	    private boolean rendu = false;
		public Emprunt() {
			super();
			// TODO Auto-generated constructor stub
		}
		public Emprunt(Integer id, Livre livre, User user, LocalDate dateEmprunt, LocalDate dateRetour, boolean rendu) {
			super();
			this.id = id;
			this.livre = livre;
			this.user = user;
			this.dateEmprunt = dateEmprunt;
			this.dateRetour = dateRetour;
			this.rendu = rendu;
		}
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public Livre getLivre() {
			return livre;
		}
		public void setLivre(Livre livre) {
			this.livre = livre;
		}
		public User getUser() {
			return user;
		}
		public void setUser(User user) {
			this.user = user;
		}
		public LocalDate getDateEmprunt() {
			return dateEmprunt;
		}
		public void setDateEmprunt(LocalDate dateEmprunt) {
			this.dateEmprunt = dateEmprunt;
		}
		public LocalDate getDateRetour() {
			return dateRetour;
		}
		public void setDateRetour(LocalDate dateRetour) {
			this.dateRetour = dateRetour;
		}
		public boolean isRendu() {
			return rendu;
		}
		public void setRendu(boolean rendu) {
			this.rendu = rendu;
		}
	    
}
