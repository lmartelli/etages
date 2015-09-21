package jardindivers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Repartition
{
    private static final Logger log = LogManager.getLogger("GenRepartitions");

    // etage => plateau
	protected Map<Integer, Plateau> plateaux = new HashMap<Integer, Plateau>();

	protected Params params;

	public Repartition(Params params) {
		this.params = params;
	}

	/**
	 * Tente de placer un foyer à un étage
	 * @param f {@link Foyer} à placer
	 * @param étage numéro de l'étage sur lequel placer le foyer
	 * @return true si le foyer a pu être placé, false sinon
	 */
	public boolean add(Foyer f, int étage) {
		boolean ok = true;
		for (int i=0; i<f.getNbNiveaux(); i++) {
			if (!getPlateau(étage+i).accept(f,params)) {
				ok = false;
				break;
			}
		}
		if (ok) {
			for (int i=0; i<f.getNbNiveaux(); i++) {
				getPlateau(étage+i).add(f);
			}
			return true;
		} else {
			removeEmptyPlateaux();
			return false;
		}
	}

	public void remove(Foyer f, int étage) {
		for (int i=0; i<f.getNbNiveaux(); i++) {
			Plateau p = getPlateau(étage+i);
			if (!p.remove(f)) {
				throw new RuntimeException("Impossible de supprimer le foyer "+f+" de l'étage "+(étage+i));
			} else {
				if (p.isEmpty()) {
					log.debug("Suppression d'un plateau vide à l'étage {} ({})",étage+i,f);
					plateaux.remove(étage+i);
				}
			}
		}
		if (containsEmptyPlateau())
			throw new RuntimeException("containsEmptyPlateau after remove "+f+" étage "+étage);
	}

	public Collection<Plateau> getPlateaux() {
		return plateaux.values();
	}

	public Plateau getPlateau(int étage) {
		Plateau p = plateaux.get(étage);
		if (p==null) {
			p = new Plateau(étage,params.getSurfacePlateauMax());
			log.trace("Création d'un plateau vide à l'étage {}",étage);
			plateaux.put(étage,p);
		}
		return p;
	}

	public boolean containsEmptyPlateau() {
		for (Plateau p: getPlateaux()) {
			if (p.isEmpty())
				return true;
		}
		return false;
	}

	public void removeEmptyPlateaux() {
		Iterator<Plateau> it = plateaux.values().iterator();
		while(it.hasNext()) {
			Plateau p = it.next();
			if (p.isEmpty())
				it.remove();
		}
	}

	/**
	 * Remplit les surface restantes avec du logement social
	 */
	public void fillBailleur() {
		for (int étage=1; étage<=params.getNbEtages();étage++) {
			getPlateau(étage).fillBailleur(params);
		}
	}

	public int getEtageMaxBailleur() {
		for (int étage=params.getNbEtages(); étage>0; étage--) {
			Plateau p = plateaux.get(étage);
			if (p!=null && p.hasBailleur())
				return étage;
		}
		return 0;
	}

	public Repartition clone() {
		Repartition r = new Repartition(params);
		for(Plateau p: plateaux.values()) {
			r.plateaux.put(p.getEtage(), p.clone());
		}
		return r;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int étage=1; étage<=params.getNbEtages(); étage++) {
			Plateau p = plateaux.get(étage);
			if (p!=null)
				builder.append(p.toString()+"\n");
		}
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((plateaux == null) ? 0 : plateaux.hashCode());
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
		Repartition other = (Repartition) obj;
		if (plateaux == null) {
			if (other.plateaux != null)
				return false;
		} else if (!plateaux.equals(other.plateaux))
			return false;
		return true;
	}
}
