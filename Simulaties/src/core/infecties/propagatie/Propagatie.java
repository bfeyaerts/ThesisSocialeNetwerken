package core.infecties.propagatie;

import core.Model;
import core.graaf.Graaf;
import core.graaf.Knoop;

public abstract class Propagatie extends Model {
	public abstract Knoop[] propageer(Graaf graaf, Object[] settings);
}
