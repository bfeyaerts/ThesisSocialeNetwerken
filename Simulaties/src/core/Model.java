package core;

import util.Parameter;

public abstract class Model {
	public String getName() {
		return getClass().getSimpleName();
	}
	public abstract Parameter[] getParameters();
	
	public boolean equals(Model model) {
		return model != null && getName().equals(model.getName());
	}
}
