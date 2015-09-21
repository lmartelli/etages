package jardindivers;

public class ParamsImpl implements Params
{
	int nbEtages;
	int surfacePlateauMax;
	int nbFoyerPlateauMax;

	int surfaceMinBailleur;
	int étageMinBailleur;

	String nomLogementSociaux;

	public int getNbEtages() {
		return nbEtages;
	}

	public int getSurfaceMinBailleur() {
		return surfaceMinBailleur;
	}

	public int getSurfacePlateauMax() {
		return surfacePlateauMax;
	}

	public String getNomLogementSociaux() {
		return nomLogementSociaux;
	}

	public int getEtageMinBailleur() {
		return étageMinBailleur;
	}

	public int getNbFoyersPlateauMax() {
		return nbFoyerPlateauMax;
	}
}
