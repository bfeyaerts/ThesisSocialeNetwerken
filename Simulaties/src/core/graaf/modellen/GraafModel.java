package core.graaf.modellen;

import java.util.Collection;

import core.Model;
import core.graaf.Knoop;
import core.graaf.Tak;
import edu.uci.ics.jung.graph.AbstractGraph;
import util.Parameter;

public abstract class GraafModel extends Model {
	public Parameter[] getParameters() {
		return new Parameter[]{new Parameter("gericht", "Gerichte graaf", Boolean.class)};
	}
	
	public abstract AbstractGraph<Knoop, Tak> genereer(Object[] setup);
	public abstract AbstractGraph<Knoop, Tak> genereer(Object[] setup, AbstractGraph<Knoop, Tak> graph);
	public abstract AbstractGraph<Knoop, Tak> genereer(Object[] setup, Collection<Knoop> knopen, Collection<Tak> takken);
}
