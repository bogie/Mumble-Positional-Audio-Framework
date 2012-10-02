/*******************************************************************************
 * This file is part of MPAF.
 * 
 * MPAF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MPAF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with MPAF.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package mpaf.ice;

import java.util.HashMap;
import java.util.Map;

import mpaf.Logger;
import mpaf.games.Battlefield3Handler;
import mpaf.games.GameHandler;
import Ice.Current;
import Murmur.Channel;
import Murmur.InvalidChannelException;
import Murmur.InvalidSecretException;
import Murmur.InvalidSessionException;
import Murmur.ServerBootedException;
import Murmur.User;

public class ServerCallbackI extends Murmur._ServerCallbackDisp {
	private static final long serialVersionUID = -666110379922768625L;

	Murmur.ServerPrx server;

	public ServerCallbackI(Murmur.ServerPrx server) {
		this.server = server;
	}

	Map<String, GameHandler> handlers = new HashMap<String, GameHandler>();

	@Override
	public void userConnected(User state, Current __current) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userDisconnected(User state, Current __current) {
		// TODO Auto-generated method stub

	}

	@Override
	public void userStateChanged(User state, Current __current) {
		try {
			Logger.debug(this.getClass(), "User state has changed");
			if (state.context.length() < 1)
				return;
			// split context with null terminated character
			String[] splitcontext = state.context.split("\0");
			if (splitcontext.length < 1) {
				for (GameHandler handler : handlers.values()) {
					if (handler.isUserInGameChannel(state)) {
						state.channel = handler.getGameTree().c.id;
						server.setState(state);
						return;
					}
				}
				return;
			}

			// first part is the game name
			String gamename = splitcontext[0];
			Logger.debug(this.getClass(), gamename);
			// Get GameHandler from HashMap
			GameHandler handler = handlers.get(gamename);

			// Check if GameHandler exists
			if (handler == null) {
				// Create a new GameHandler for BF3
				Logger.debug(this.getClass(), "Creating new GameHandler");
				if (gamename.equalsIgnoreCase("Battlefield 3")) {
					Logger.debug(this.getClass(),
							"Creating new BattleField3Handler");
					Battlefield3Handler bf3handler = new Battlefield3Handler(
							this.server);
					Logger.debug(this.getClass(), "BF3 Handler generated is: "
							+ bf3handler);
					handlers.put(gamename, bf3handler);
					handler = bf3handler;
				} else {
					Logger.debug(this.getClass(),
							"tried to create GameHandler for unsupported game");
					return;
				}
			}
			Logger.debug(this.getClass(), "There are " + handlers.size()
					+ " handlers now.");
			// execute GameHandler.handle(User state)
			Logger.debug(this.getClass(), "started handling UserState");
			handler.handleUserState(state);
		} catch (InvalidSecretException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerBootedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidChannelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidSessionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void channelCreated(Channel state, Current __current) {
		// TODO Auto-generated method stub

	}

	@Override
	public void channelRemoved(Channel state, Current __current) {
		// TODO Auto-generated method stub

	}

	@Override
	public void channelStateChanged(Channel state, Current __current) {
		// TODO Auto-generated method stub

	}

}
