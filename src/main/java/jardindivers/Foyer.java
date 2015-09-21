package jardindivers;


public class Foyer
{
	String nom;
	/**
	 * Surafce minimum par niveau
	 */
	int surfaceMin;
	/**
	 * Surface maximum par niveau
	 */
	int surfaceMax;
	/**
	 * Nb de niveaux (1=normal, 2=>Duplex)
	 */
	int nbNiveaux;

	boolean bailleur;

	/**
	 * Etages possibles
	 */
	int[] étages;

	/**
	 * 
	 * @param nom
	 * @param nbNiveaux nombre de niveaux souhaités (2=duplex)
	 * @param surfaceMin surface totale minimum
	 * @param surfaceMax surface totale maximum
	 * @param étages étages souhaités
	 * @param bailleur
	 */
	public Foyer(String nom, int nbNiveaux, int surfaceMin, int surfaceMax, int[] étages, boolean bailleur) {
		super();
		this.nom = nom;
		this.nbNiveaux = nbNiveaux;
		this.surfaceMin = surfaceMin/nbNiveaux;
		this.surfaceMax = surfaceMax/nbNiveaux;
		this.étages = étages;
		this.bailleur = bailleur;
	}
	public Foyer(String nom, int nbNiveaux, int surfaceMin, int surfaceMax, int[] étages) {
		this(nom,nbNiveaux,surfaceMin,surfaceMax,étages,false);
	}

	public String getNom() {
		return nom;
	}
	public int getNbNiveaux() {
		return nbNiveaux;
	}
	public int getSurfaceMin() {
		return surfaceMin;
	}
	public int getSurfaceMax() {
		return surfaceMax;
	}
	public int[] getEtages() {
		return étages;
	}

	public boolean isBailleur() {
		return bailleur;
	}

	public String toString() {
		return nom+"["+surfaceMin+"-"+surfaceMax+"]"+(nbNiveaux>1 ? "*"+nbNiveaux : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
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
		Foyer other = (Foyer) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}
}
