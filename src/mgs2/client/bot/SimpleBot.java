package mgs2.client.bot;

import mgs2.client.ClientWindow;
import mgs2.shared.GameStage;
import mgs2.shared.Statics;
import ssmith.lang.NumberFunctions;

public class SimpleBot extends AbstractBot {

	private static final long INTERVAL = 1000;

	private long time_to_change;

	public SimpleBot(ClientWindow _window) {
		super(_window);
	}

	@Override
	public void process(long interpol) {
		if (window.main.game_data != null && window.main.game_data.game_stage == GameStage.STARTED) {
			time_to_change -= interpol;
			if (time_to_change < 0) {
				time_to_change = INTERVAL;

				window.keys[Statics.KEY_UP] = false;
				window.keys[Statics.KEY_DOWN] = false;
				window.keys[Statics.KEY_LEFT] = false;
				window.keys[Statics.KEY_RIGHT] = false;
				int d = NumberFunctions.rnd(1, 4);
				switch (d) {
				case 1:
					window.keys[Statics.KEY_UP] = true;
					break;
				case 2:
					window.keys[Statics.KEY_DOWN] = true;
					break;
				case 3:
					window.keys[Statics.KEY_LEFT] = true;
					break;
				case 4:
					window.keys[Statics.KEY_RIGHT] = true;
					break;
				}
			}
		}
	}

}
