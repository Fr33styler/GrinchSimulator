package ro.fr33styler.grinch.handler;

import ro.fr33styler.grinch.Messages;

public enum GameState {
	
	WAITING(Messages.STATE_WAITING),
	IN_GAME(Messages.STATE_IN_GAME),
	END(Messages.STATE_ENDING);

	private Messages state;

	private GameState(Messages state) {
		this.state = state;
	}

	public String getState() {
		return state.toString();
	}

}