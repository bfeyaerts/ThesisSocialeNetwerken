package core.infecties.diagnose;

import java.awt.Color;

public interface Diagnose {
	public String name();
	public Color getDefaultColor();
	
	public boolean isBesmetbaar();
	public boolean isBesmettelijk();
}
