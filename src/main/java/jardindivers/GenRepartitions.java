package jardindivers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenRepartitions
{
    private static final Logger log = LogManager.getLogger("GenRepartitions");

	Collection<Foyer> foyers;
	Params params;
	Collection<Repartition> history;

	public static final Level VERBOSE = Level.getLevel("VERBOSE");

	boolean useHistory = true;

	public static void main(String args[]) {
		ParamsImpl params = new ParamsImpl();
		params.nbEtages = 5;
		params.étageMinBailleur = 3;
		params.surfaceMinBailleur = 30;
		params.surfacePlateauMax = 150;
		params.nomLogementSociaux = "Bailleur";
		Collection<Foyer> foyers = new ArrayList<Foyer>();
		foyers.add(new Foyer("Laurent & Coralie",   1, 65,  80, new int[] {3,4}));
		foyers.add(new Foyer("Perinne & Sébastien", 1, 65,  80, new int[] {3,4,5}));
		foyers.add(new Foyer("Emilie & Aurélien",   2, 65,  80, new int[] {1,2,3,4}));
		foyers.add(new Foyer("Sarah & Guillaume",   1, 80,  90, new int[] {5}));
		foyers.add(new Foyer("Agathe & Tristan",    1, 55,  80, new int[] {3,4,5}));
		foyers.add(new Foyer("Yann",                1, 45,  60, new int[] {1,2,3,4,5}));
		foyers.add(new Foyer("Odette",              1, 55,  60, new int[] {3,4,5}));
		foyers.add(new Foyer("Christian",           1, 25,  30, new int[] {1,2,3,4,5}));
		foyers.add(new Foyer("Christophe",          1, 70,  75, new int[] {3,4,5}));
		
		GenRepartitions g = new GenRepartitions(foyers,params);
		g.generate();
	}

	public GenRepartitions(Collection<Foyer> foyers, Params params) {
		this.foyers = foyers;
		this.params = params;
	}

	public Collection<Repartition> generate() {
		long start = System.currentTimeMillis();
		HashSet<Repartition> result = new HashSet<Repartition>();
		history = new HashSet<Repartition>();
		recurse(result, null, foyers);
		long end = System.currentTimeMillis();
		System.out.println("FIN ("+(end-start)+"ms)");
		return result;
	}

	/**
	 * Validation supplémentaire des répartitions
	 * @param r
	 * @return
	 */
	public boolean validate(Repartition r)
	{
		// 1. On accepte pas un étage entier de logements sociaux
		for(Plateau p : r.getPlateaux()) {
			if (!validate(p))
				return false;
		}
		if (r.getEtageMaxBailleur() < params.getEtageMinBailleur())
			return false;
		return true;
	}

	/**
	 * Validation supplémentaire des plateaux
	 * 
	 */
	public boolean validate(Plateau p) {
		// 1. On accepte pas un étage entier de logements sociaux
		if (p.getFoyers().size()==1
				&& p.getFoyers().iterator().next().getNom().equals(params.getNomLogementSociaux()))
			return false;
		
		return true;
	}

	/**
	 * 
	 * @param r repartition courante
	 * @param aPlacer foyers restant à placer
	 */
	protected void recurse(Collection<Repartition> result, Repartition r, Collection<Foyer> aPlacer) {
		if (r!=null && r.containsEmptyPlateau()) {
			throw new RuntimeException("Empty plateau");
		}
		if (aPlacer.isEmpty()) {
			Repartition solution = r.clone();
			solution.fillBailleur();
			if (result.contains(solution)) {
				log.log(VERBOSE,"Répartition déjà générée:\n{}",solution);
			} else if (!validate(solution)) {
				log.trace("Répartition non valide:\n{}",solution);
			} else {
				result.add(solution);
				System.out.println("Répartition possible n°"+result.size()+":");
				System.out.println(solution);
			}
		} else if (useHistory && history.contains(r)) {
			log.trace("Répartion déjà dans l'historique:\n{}",r);
			return;
		} else {
			if (r==null)
				r = new Repartition(params);
			for (Foyer f : aPlacer) {
				boolean ok = false;
				boolean fail = false;
				for (int étage : f.getEtages()) {
					if (r.add(f, étage)) {
						ok = true;
						ArrayList<Foyer> aPlacerNew = new ArrayList<Foyer>(aPlacer);
						aPlacerNew.remove(f);
						log.info("Placement de {} à l'étage {}",f.getNom(),étage);
						if (r.containsEmptyPlateau()) {
							throw new RuntimeException("Empty plateau");
						}
						Repartition save = r.clone();
						if (save.containsEmptyPlateau()) {
							throw new RuntimeException("Empty plateau");
						}
						recurse(result, r, aPlacerNew);
						if (!r.equals(save)) {
							log.error("Répartition corrompue:\n{}",r);
							log.error("Répartition attendue:\n{}",save);
							throw new RuntimeException();
						}
						if (useHistory) {
							if (history.add(r.clone()))
								log.trace("Ajout dans l'historique:\n{}",r);
						}
						log.info("Suppression de {} de l'étage {}",f.getNom(),étage);
						r.remove(f,étage);
						if (r.containsEmptyPlateau()) {
							throw new RuntimeException("Empty plateau");
						}
					} else {
						if (!fail) {
							log.log(VERBOSE,"Répartition courante:\n{}",r);
							fail = true;
						}
						log.trace("Impossible de placer {} à l'étage {}: {}",f,étage);
					}
				}
				if (!ok) {
					log.debug("Impossible de placer {}",f.getNom());
					log.trace("Répartition courante:\n{}",r);
					return;
				}
			}
		}
	}
}
