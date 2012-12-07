package core.infecties.evolutie;

import core.Model;
import core.graaf.Graaf;
import core.graaf.Knoop;
import core.infecties.diagnose.Diagnose;

public abstract class Evolutie extends Model {
	public abstract Class<Enum<? extends Diagnose>> possibleDiagnoses();
	
	public void next(Knoop knoop) {
		@SuppressWarnings("unchecked")
		Enum<? extends Diagnose> diagnose = (Enum<? extends Diagnose>) knoop.getDiagnose();
		Enum<? extends Diagnose>[] diagnoses = possibleDiagnoses().getEnumConstants();
		
		knoop.setDiagnose((Diagnose) diagnoses[(diagnose.ordinal() + 1) % diagnoses.length]);
	}
	
	public abstract void besmet(Knoop knoop, Object[] settings);
	
	public void evolueer(Graaf graaf, Object[] settings) {
		for (Knoop knoop: graaf.getKnopen())
			evolueer(knoop, settings);
	}
	protected abstract void evolueer(Knoop knoop, Object[] settings);
}
