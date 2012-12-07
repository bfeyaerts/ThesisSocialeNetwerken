package core.infecties.evolutie;

import java.awt.Color;

import util.Parameter;
import core.graaf.Knoop;
import core.infecties.diagnose.Diagnose;

public class Terminaal extends Evolutie {
	static enum MyDiagnose implements Diagnose {
		GEZOND(Color.GREEN),
		BESMET(Color.RED);
		
		private final Color defaultColor;
		
		private MyDiagnose(Color defaultColor) {
			this.defaultColor = defaultColor;
		}
		@Override
		public Color getDefaultColor() {
			return defaultColor;
		}
		@Override
		public boolean isBesmettelijk() {
			return this.equals(BESMET);
		}
		@Override
		public boolean isBesmetbaar() {
			return this.equals(GEZOND);
		}
		
		@Override
		public String toString() {
			String name = name();
			name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
			return name;
		}
	}

	@Override
	public void besmet(Knoop knoop, Object[] settings) {
		knoop.setDiagnose(MyDiagnose.BESMET);
	}
	@Override
	protected void evolueer(Knoop knoop, Object[] settings) {
		// Geen evolutie mogelijk: eens besmet, altijd besmet
	}

	@Override
	public Parameter[] getParameters() {
		return new Parameter[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<Enum<? extends Diagnose>> possibleDiagnoses() {
		return (Class<Enum<? extends Diagnose>>) MyDiagnose.class.asSubclass(Enum.class);
	}
}
