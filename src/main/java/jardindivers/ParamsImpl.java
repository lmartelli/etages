package jardindivers;

public class ParamsImpl implements Params
{
	int surfaceMinBailleur;
	int nbEtages;
	int étageMinBailleur;
	int surfacePlateauMax;
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
}
