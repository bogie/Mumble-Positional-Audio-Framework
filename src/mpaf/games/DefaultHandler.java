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


import java.util.Arrays;
import java.util.List;

import mpaf.json.ContextJson;
import mpaf.json.IdentityJson;
import Murmur.ACL;
import Murmur.Channel;
import Murmur.InvalidChannelException;
import Murmur.InvalidSecretException;
import Murmur.InvalidSessionException;
import Murmur.ServerBootedException;
import Murmur.Tree;
import Murmur.User;

public class DefaultHandler implements GameHandler {
	Murmur.ServerPrx server;
	int game_channel_id = 0;
	Tree gameTree = null;
	
	@Override
	public void handleUserState(User state) throws InvalidSecretException,
			ServerBootedException, InvalidChannelException,
			InvalidSessionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleChannelRemoved(Channel state) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean moveUser(User state, ContextJson context, IdentityJson ijson)
			throws InvalidSecretException, ServerBootedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Tree updateGameTree() throws InvalidSecretException,
			ServerBootedException {
		_findTree(server.getTree());
		return this.gameTree;
	}
	
	private void _findTree(Tree parent){
		for(Tree t : parent.children)
		{
			if(t.c.id == game_channel_id)
				this.gameTree = t;
			else 
				_findTree(t);
		}
	}

	@Override
	public boolean isUserInGameChannel(User state) throws InvalidSecretException, ServerBootedException {
		boolean status = false;
		_findUser(server.getTree(), state, status);
		return status;
	}
	
	private void _findUser(Tree parent, User state, boolean status){
		for(Tree t : parent.children)
		{
			List<User> users = Arrays.asList(t.users);
			if(users.contains(state))
				status = true;
			else
				_findUser(t, state, status);
		}
	}

	@Override
	public void addACLtoChannel(int channelid, ACL acl)
			throws InvalidSecretException, ServerBootedException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameChannel(int channelId) throws InvalidSecretException, ServerBootedException, InvalidChannelException {
		if(this.game_channel_id != 0) {
			server.removeChannel(game_channel_id);
		}
		this.game_channel_id = channelId;
		updateGameTree();
	}

}
