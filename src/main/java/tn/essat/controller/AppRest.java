package tn.essat.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.nio.file.*;

import tn.essat.dao.*;

import tn.essat.model.*;


@RestController

@RequestMapping("biblio")
public class AppRest {
	@Autowired
	private ICategorie daoCat;
	@Autowired
	private ILivre daolivre;
	@Autowired
	private IEmprunt daoEmprunt;
	@Autowired
	private IUser daoUser;
    @Autowired
    private IRole roleDao;


	@GetMapping("/categories")
    public List<Categorie> getCategories() {
        return daoCat.findAll();
    }

  


    @GetMapping("/categories/{id}/livres")
    public List<Livre> getLivresByCategorie(@PathVariable int id) {
        return daolivre.findAllByCategorie_Id(id);
    }
    @PostMapping("/addcategories")
    public Categorie add(@RequestBody Categorie cat) {
        return daoCat.save(cat);
    }

    @PutMapping("/updatecat/{id}")
    public Categorie updateCat(@PathVariable("id") int id,
                               @RequestBody Categorie cat) {

        cat.setId(id);
        
        

        return daoCat.save(cat);
    }


    @DeleteMapping("/dellCategories/{id}")
    public void deleteCat(@PathVariable("id") int id) {
        daoCat.deleteById(id);
    }


    
    @GetMapping("/livres")
    public List<Livre> getLivres() {
        return daolivre.findAll();
    }


    @PostMapping(value = "/addlivres", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Livre addLivre(
            @RequestParam("titre") String titre,
            @RequestParam("auteur") String auteur,
            @RequestParam("descripition") String description,
            @RequestParam("categorie") Integer categorieId,
            @RequestParam("exemplaires") Integer exemplaires,
            @RequestParam("disponibles") Integer disponibles,
            @RequestParam(value = "image", required = false) MultipartFile imageFile
    ) throws IOException {
        Categorie cat = new Categorie();
        cat.setId(categorieId);

        String fileName = null;

        // Sauvegarder l'image seulement si elle est fournie
        if (imageFile != null && !imageFile.isEmpty()) {
            // Créer un nom de fichier unique pour éviter les conflits
            String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            fileName = System.currentTimeMillis() + "_" + originalFilename;

            // Chemin vers src/main/resources/uploads
            String uploadPath = "uploads";
            Path uploadDir = Paths.get(uploadPath);

            // Créer le dossier s'il n'existe pas
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Sauvegarder le fichier
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("Image sauvegardée : " + filePath.toAbsolutePath());
        }

        // Créer et sauvegarder le livre
        Livre lv = new Livre();
        lv.setTitre(titre);
        lv.setAuteur(auteur);
        lv.setDescription(description);
        lv.setCategorie(cat);
        lv.setExemplaires(exemplaires);
        lv.setDisponibles(disponibles);
        lv.setImage(fileName); // Peut être null si pas d'image

        return daolivre.save(lv);
    }

    @PutMapping("/updatelivres/{id}")
    public Livre updateLivre(@PathVariable Integer id,
                             @RequestBody Livre lv) {

     
        lv.setId(id);

        return daolivre.save(lv);
    }


    @DeleteMapping("/dellivres/{id}")
    public void deleteLivre(@PathVariable("id") int id) {
        daolivre.deleteById(id);
    }


  
    @GetMapping("/emprunt/livres/{id}")
    public Emprunt emprunter(@PathVariable Integer id) {
        Livre livre = daolivre.findById(id).get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();  // Récupérer le nom d'utilisateur
        User u = daoUser.findByUsername(username);  // Charger l'utilisateur depuis la base

// Vérifier que l'utilisateur existe
        if (u == null) {
            // Gérer l'erreur - utilisateur non trouvé
            throw new RuntimeException("Utilisateur non trouvé: " + username);
        }        LocalDate now = LocalDate.now();

        Emprunt emp = new Emprunt();
        emp.setUser(u);
        emp.setLivre(livre);
        emp.setDateEmprunt(now);
        emp.setRendu(false);      

        return daoEmprunt.save(emp);
    }



    @GetMapping("/emprunts/me")
    public List<Emprunt> getMesEmprunts(@RequestParam(required = false) Integer userId) {
        // Option A: Si userId est fourni en paramètre
        if (userId != null) {
            Optional<User> userOptional = daoUser.findById(userId);
            if (userOptional.isPresent()) {
                return daoEmprunt.findByUser(userOptional.get());
            }
            return Collections.emptyList();
        }

        // Option B: Via l'authentification (si Spring Security est actif)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String username = auth.getName();  // getPrincipal() retourne String
            User user = daoUser.findByUsername(username);
            if (user != null) {
                return daoEmprunt.findByUser(user);
            }
        }

        // Option C: Retourner tous les emprunts ou une liste vide
        return daoEmprunt.findAll();  // Ou return Collections.emptyList();
    }


    @GetMapping("/emprunts")
    public List<Emprunt> getAllEmprunts() {
        return daoEmprunt.findAll();
    }

    @DeleteMapping("/delemprunts/{id}")
    public void deleteEmprunt(@PathVariable("id") int id) {
        daoEmprunt.deleteById(id);
    }
  
    @PostMapping("/emprunts")
    public ResponseEntity<?> emprunter(
            @RequestParam Integer livreId,
            @RequestParam Integer lecteurId) {

        try {
         
            Livre livre = daolivre.findById(livreId)
                    .orElseThrow(() -> new Exception("Livre introuvable"));

        
            User lecteur = daoUser.findById(lecteurId)
                    .orElseThrow(() -> new Exception("Lecteur introuvable"));

        
            if (livre.getDisponibles() == null || livre.getDisponibles() <= 0) {
                throw new Exception("Aucun exemplaire disponible");
            }

       
            livre.setDisponibles(livre.getDisponibles() - 1);
            daolivre.save(livre);

     
            Emprunt emprunt = new Emprunt();
            emprunt.setLivre(livre);
            emprunt.setUser(lecteur);
            emprunt.setDateEmprunt(LocalDate.now());
            emprunt.setRendu(false);

         
            emprunt = daoEmprunt.save(emprunt);

            return ResponseEntity.ok(emprunt);

        } catch (Exception ex) {

            switch (ex.getMessage()) {
                case "Livre introuvable":
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
                case "Lecteur introuvable":
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
                case "Aucun exemplaire disponible":
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
                default:
                    return ResponseEntity.badRequest().body(ex.getMessage());
            }
        }
    }

   
    @PostMapping("/retourner/{empruntId}")
    public ResponseEntity<?> retourner(@PathVariable Integer empruntId) {
        try {
           
            Emprunt emprunt = daoEmprunt.findById(empruntId)
                    .orElseThrow(() -> new Exception("Emprunt introuvable"));

            
            if (emprunt.isRendu()) {
                throw new Exception("Déjà rendu");
            }

            
            emprunt.setRendu(true);
           
            emprunt.setDateRetour(LocalDate.now());

            Livre livre = emprunt.getLivre();
            livre.setDisponibles(livre.getDisponibles() + 1);
            daolivre.save(livre);

            daoEmprunt.save(emprunt);

            return ResponseEntity.ok(emprunt);

        } catch (Exception ex) {

            if (ex.getMessage().equals("Emprunt introuvable")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
            }

            if (ex.getMessage().equals("Déjà rendu")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
            }

            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    // Méthodes manquantes pour les livres
    @GetMapping("/livres/{id}")
    public ResponseEntity<Livre> getLivreById(@PathVariable Integer id) {
        Optional<Livre> livre = daolivre.findById(id);
        return livre.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/livres/search")
    public ResponseEntity<List<Livre>> searchLivres(@RequestParam String query) {
        List<Livre> livres = daolivre.findByTitreContainingOrAuteurContaining(query, query);
        return ResponseEntity.ok(livres);
    }

    // Méthodes manquantes pour les catégories
    @GetMapping("/categories/{id}")
    public ResponseEntity<Categorie> getCategorieById(@PathVariable Integer id) {
        Optional<Categorie> categorie = daoCat.findById(id);
        return categorie.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Méthodes manquantes pour les emprunts
    @GetMapping("/emprunts/user/{userId}")
    public ResponseEntity<List<Emprunt>> getEmpruntsByUser(@PathVariable Integer userId) {
        Optional<User> user = daoUser.findById(userId);
        if (!user.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<Emprunt> emprunts = daoEmprunt.findByUser(user.get());
        return ResponseEntity.ok(emprunts);
    }

    @GetMapping("/emprunts/livre/{livreId}")
    public ResponseEntity<List<Emprunt>> getEmpruntsByLivre(@PathVariable Integer livreId) {
        Optional<Livre> livre = daolivre.findById(livreId);
        if (!livre.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<Emprunt> emprunts = daoEmprunt.findByLivre(livre.get());
        return ResponseEntity.ok(emprunts);
    }

    // Méthode pour mettre à jour le rôle d'un utilisateur
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Integer id,
                                               @RequestParam Integer roleId) {
        try {
            Optional<User> userOptional = daoUser.findById(id);
            Optional<Role> roleOptional = roleDao.findById(roleId);

            if (!userOptional.isPresent() || !roleOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOptional.get();
            user.setRole(roleOptional.get());

            User updatedUser = daoUser.save(user);
            updatedUser.setPassword(null);

            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


   


	  
	  
}

