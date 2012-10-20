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
package mpaf.games;

import java.sql.Connection;

import mpaf.json.ContextJson;
import mpaf.json.IdentityJson;
import Murmur.Channel;
import Murmur.InvalidChannelException;
import Murmur.InvalidSecretException;
import Murmur.InvalidSessionException;
import Murmur.ServerBootedException;
import Murmur.Tree;
import Murmur.User;

public interface GameHandler {

	/**
	 * Generic initialization mainly for passing SQLite Connection and defining gameChannel
	 * @param Connection conn - SQLight connection
	 * @param int gameChannelId - channel this handler is responsible for
	 */
	public void init(Connection conn, int gameChannelId);

	/**
	 * Adds the GameHandler to the sqlite database
	 * note: requires you to init() first.
	 */
	public void addToDatabase();
	
	/**
	 * Handles every callback event, parses json data and calls moveUser()
	 * @param state User state from ICE callback
	 * @throws InvalidSessionException 
	 * @throws InvalidChannelException 
	 */
	public void handleUserState(User state) throws InvalidSecretException, ServerBootedException, InvalidChannelException, InvalidSessionException;
	
	/**
	 * not used yet
	 * @param state Channel state from callback
	 */
	public void handleChannelRemoved(Channel state);
	
	/**
	 * Loop that finds the appropriate channel for the user to be switched to. Will be automatically called multiple times if channels have to be created.
	 * @param state User state that comes from the ICE callback
	 * @param context parsed context json, extracted from user state, holds server ipport information
	 * @param ijson parsed identity json, extracted from user state, holds team/squad/squad leader information
	 * @return true if user was moved, false if user could not be moved(channel does not exists)
	 */
	public boolean moveUser(User state, ContextJson context, IdentityJson ijson) throws InvalidSecretException, ServerBootedException;
	
	/**
	 * Used to find the appropriate super channel for the games name
	 * @return Channel Tree for super channel game name or null if inexistant
	 */
	public Tree updateGameTree() throws InvalidSecretException, ServerBootedException;
	
	/**
	 * Used to define a game channel, e.g. via the web interface
	 * Updates the gameTree and deletes the old game channel(except channelId=0)
	 * @param channelId
	 * @throws ServerBootedException 
	 * @throws InvalidSecretException 
	 * @throws InvalidChannelException 
	 */
	public void setGameChannel(int channelId) throws InvalidSecretException, ServerBootedException, InvalidChannelException;
	
	/**
	 * Used to check if the user is in the game channel, or in one of the sub channels(server/team/squad)
	 * @return true if user is in the game channel or below
	 * @return false if not
	 * @throws ServerBootedException 
	 * @throws InvalidSecretException 
	 */
	public boolean isUserInGameChannel(User state) throws InvalidSecretException, ServerBootedException;
	
	/**
	 * Used to add an ACL to an existing acl list by channel id
	 * @param channelid Channel ID for acl to add
	 * @param acl ACL that will be appended
	 */
	public void addACLtoChannel(int channelid, Murmur.ACL acl) throws InvalidSecretException, ServerBootedException;
}
