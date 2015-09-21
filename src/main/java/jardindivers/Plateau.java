package jardindivers;

import java.util.Collection;
import java.util.HashSet;

public class Plateau
{
	protected int étage;
	protected int surfaceMax;
	protected Collection<Foyer> foyers = new HashSet<Foyer>();

	private int surfaceOccupéeMin;
	private int surfaceOccupéeMax;

	public Plateau(int étage, int surfaceMax) {
		this.étage = étage;
		this.surfaceMax = surfaceMax;
	}

	public Collection<Foyer> getFoyers() {
		return foyers;
	}

	public int getResidu() {
		return surfaceMax - surfaceOccupéeMax;
	}

	public int getEtage() {
		return étage;
	}

	/**
	 * Vérifie qu'on peut ajouter un foyer sur un plateau
	 * @param f {@link Foyer} à ajouter
	 * @param p paramètre globaux (surface minimal qui peut rester)
	 * @return
	 */
	public boolean accept(Foyer f, Params p) {
		if (surfaceOccupéeMin + f.getSurfaceMin() > surfaceMax) {
			// Pas assez d'espace
			return false;
		} else if (surfaceOccupéeMax + f.getSurfaceMax() < surfaceMax
				&& surfaceOccupéeMax + f.getSurfaceMax() > surfaceMax - p.getSurfaceMinBailleur()) {
			// L'espace restant est trop petit pour un autre foyer
			return false;
		} else {
			return true;
		}
	}

	public void add(Foyer f) {
		surfaceOccupéeMin += f.getSurfaceMin();
		surfaceOccupéeMax += f.getSurfaceMax();
		foyers.add(f);
	}

	public boolean remove(Foyer f) {
		surfaceOccupéeMax -= f.getSurfaceMax();
		surfaceOccupéeMin -= f.getSurfaceMin();
		return foyers.remove(f);
	}

	/**
	 * Minimum de surface restant disponible
	 * @return
	 */
	public int getMinAvailable() {
		return surfaceMax - surfaceOccupéeMax;
	}

	/**
	 * Minimum de surface restant disponible
	 * @return
	 */
	public int getMaxAvailable() {
		return surfaceMax - surfaceOccupéeMin;
	}

	/**
	 * Remplit l'espace restant du plateau avec du logement social
	 * @param p
	 */
	public void fillBailleur(Params p) {
		if (getMinAvailable()>0) {
			add(new Foyer(
					p.getNomLogementSociaux(),
					1,
					surfaceMax-surfaceOccupéeMax,
					surfaceMax-surfaceOccupéeMin,
					new int[0],
					true));
		}
	}

	public boolean hasBailleur() {
		for(Foyer f: foyers) {
			if (f.isBailleur())
				return true;
		}
		return false;
	}

	public Plateau clone() {
		Plateau p = new Plateau(étage, surfaceMax);
		if (isEmpty())
			throw new RuntimeException("Cloning empty plateau");
		p.foyers.addAll(foyers);
		p.surfaceOccupéeMax = surfaceOccupéeMax;
		p.surfaceOccupéeMin = surfaceOccupéeMin;
		return p;
	}

	public String toString() {
		int minRestant = getMinAvailable();
		/*
		int surfaceMaxProprio = 0;
		for(Foyer f: foyers) {
			if (!f.isBailleur()) {
				surfaceMaxProprio += f.getSurfaceMax();
			}
		}
		StringBuilder builder = new StringBuilder(128);
		*/
		return "Etage "+étage+": "+foyers+(minRestant>0 ? " ("+minRestant+"-"+getMaxAvailable()+")" : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((foyers == null) ? 0 : foyers.hashCode());
		result = prime * result + étage;
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
		Plateau other = (Plateau) obj;
		if (foyers == null) {
			if (other.foyers != null)
				return false;
		} else if (!foyers.equals(other.foyers))
			return false;
		if (étage != other.étage)
			return false;
		return true;
	}

	public boolean isEmpty() {
		return foyers.isEmpty();
	}
}
