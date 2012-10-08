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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import mpaf.Logger;
import mpaf.json.ContextJson;
import mpaf.json.IdentityJson;
import mpaf.shared.StringUtils;
import Ice.BooleanHolder;
import Murmur.ACL;
import Murmur.ACLListHolder;
import Murmur.Channel;
import Murmur.GroupListHolder;
import Murmur.InvalidChannelException;
import Murmur.InvalidSecretException;
import Murmur.InvalidSessionException;
import Murmur.ServerBootedException;
import Murmur.Tree;
import Murmur.User;

import com.google.gson.Gson;

public class Battlefield3Handler extends DefaultHandler {
	HashMap<Integer, String> squadNames = new HashMap<Integer, String>();

	public Battlefield3Handler(Murmur.ServerPrx server)
			throws InvalidSecretException, ServerBootedException {
		this.server = server;
		this.gameTree = updateGameTree();

		// BF3 uses NATO Alphabet

		squadNames.put(0, "No Squad");
		squadNames.put(1, "Alpha");
		squadNames.put(2, "Bravo");
		squadNames.put(3, "Charlie");
		squadNames.put(4, "Delta");
		squadNames.put(5, "Echo");
		squadNames.put(6, "Foxtrot");
		squadNames.put(7, "Golf");
		squadNames.put(8, "Hotel");
		squadNames.put(9, "India");
		squadNames.put(10, "Juliet");
		squadNames.put(11, "Kilo");
		squadNames.put(12, "Lima");
		squadNames.put(13, "Mike");
		squadNames.put(14, "November");
		squadNames.put(15, "Oscar");
		squadNames.put(16, "Papa");
		squadNames.put(17, "Quebec");
		squadNames.put(18, "Romeo");
		squadNames.put(19, "Sierra");
		squadNames.put(20, "Tango");
		squadNames.put(21, "Uniform");
		squadNames.put(22, "Victor");
		squadNames.put(23, "Whiskey");
		squadNames.put(24, "X-Ray");
		squadNames.put(25, "Yankee");
		squadNames.put(26, "Zulu");

	}

	@Override
	public void handleUserState(User state) throws InvalidSecretException,
			ServerBootedException, InvalidChannelException,
			InvalidSessionException {
		if(this.active == false)
		{
			Logger.debug(this.getClass(),"handleUserState: Battlefield 3 handler is deactivated.");
			return;
		}
		Logger.debug(this.getClass(), "Handling UserStateChanged for BF3");
		Logger.debug(this.getClass(), state.name);

		this.gameTree = updateGameTree();
		if(this.gameTree == null) {
			Logger.debug(this.getClass(), "Game channel could not be found, please create and define a game channel via the web interface.");
			return;
		}

		if (!isUserInGameChannel(state)) {
			Logger.debug(this.getClass(),
					"User is not in game channel or sub channel, returning");
			return;
		}
		String[] splitcontext = state.context.split("\0");
		if (splitcontext.length < 2) {
			Logger.debug(this.getClass(),
					"Context state is missing ipport, returning");

			if (state.channel != this.gameTree.c.id) {
				state.channel = this.gameTree.c.id;
				server.setState(state);
			}
		} else {
			Gson gson = new Gson();
			Logger.debug(this.getClass(), "splitcontext[0]:" + splitcontext[0]);
			Logger.debug(this.getClass(), "splitcontext[1]:" + splitcontext[1]);
			ContextJson context = gson.fromJson(splitcontext[1],
					ContextJson.class);
			IdentityJson ijson = gson.fromJson(state.identity,
					IdentityJson.class);

			if (!context.getIpport().contains(":"))
				return;

			moveUser(state, context, ijson);
		}
	}

	@Override
	public void handleChannelRemoved(Channel state) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean moveUser(User state, ContextJson context, IdentityJson ijson)
			throws InvalidSecretException, ServerBootedException {
		try {
			this.gameTree = updateGameTree();
			for (Tree t : this.gameTree.children) {
				Logger.debug(
						this.getClass(),
						"Comparing context channel: "
								+ StringUtils.getHexString(t.c.description
										.getBytes())
								+ " with "
								+ StringUtils.getHexString(context.getIpport()
										.getBytes()));
				if (t.c.description.equalsIgnoreCase(context.getIpport())) {
					Logger.debug(this.getClass(),
							"Found context channel for context: ");
					for (Tree tc : t.children) {
						Logger.debug(this.getClass(), "checking tc.c.name: "
								+ tc.c.name);
						if (tc.c.name.equalsIgnoreCase(ijson.getTeam())) {
							Logger.debug(
									this.getClass(),
									"Found team channel for team: "
											+ ijson.getTeam());
							String squadname = "";
							if (ijson.getSquad() >= 0 && ijson.getSquad() <= 26)
								squadname = squadNames.get(ijson.getSquad());
							else {
								Logger.debug(
										getClass(),
										"Got invalid squad id: "
												+ ijson.getSquad());
								squadname = "No Squad";
							}
							for (Tree tct : tc.children) {
								if (tct.c.name.equalsIgnoreCase(squadname)) {
									Logger.debug(this.getClass(),
											"Found squad channel for squad: "
													+ squadname);
									if (state.channel == tct.c.id)
										return true;
									state.channel = tct.c.id;
									server.setState(state);
									return true;
								}
							}
							Logger.debug(this.getClass(),
									"Could not find squad channel for squad: "
											+ squadname);

							// create a new channel for squad: squadname
							int squadcid = server
									.addChannel(squadname, tc.c.id);

							// retrieve channel state for further use
							Channel cstate = server.getChannelState(squadcid);

							// we link all squad channels to the team
							// channel(parent)
							int links[] = new int[1];
							links[0] = cstate.parent;
							cstate.links = links;
							server.setChannelState(cstate);
							Murmur.ACL speak = new Murmur.ACL(true, true, true,
									-1, "~out", 0, Murmur.PermissionSpeak.value);
							addACLtoChannel(squadcid, speak);
							// switch the user to his squad channel
							state.channel = squadcid;
							server.setState(state);
							return true;
						}
					}
					Logger.debug(
							this.getClass(),
							"Could not find team channel for team: "
									+ ijson.getTeam());

					// create a new channel for team: ijson.getTeam()
					@SuppressWarnings("unused")
					int teamcid = server.addChannel(ijson.getTeam(), t.c.id);

					// restart the loop
					moveUser(state, context, ijson);
					return false;
				}
			}
			Logger.debug(
					this.getClass(),
					"Could not find context channel for context: "
							+ context.getIpport());
			// TODO: make a list of servers with context reference

			// create a new channel for server(context): context.getIpport()
			// TODO: find a pointer for the server name
			int contextcid = server.addChannel(context.getIpport(),
					game_channel_id);

			// retrieve channel state
			Channel c = server.getChannelState(contextcid);

			// set the description to the ipport string, we search for this
			c.description = context.getIpport();

			// set channel state
			server.setChannelState(c);

			// set the necessary ACL, so people can't move,link etc. themselves
			// 1252 = ENTER | MOVE | MAKE | LINK | MAKE_T
			int mask = Murmur.PermissionEnter.value;
			mask += Murmur.PermissionLinkChannel.value;
			mask += Murmur.PermissionMakeChannel.value;
			mask += Murmur.PermissionMakeTempChannel.value;
			mask += Murmur.PermissionMove.value;
			Murmur.ACL acl = new Murmur.ACL(true, true, true, -1, "all",
					Murmur.PermissionSpeak.value, mask);
			addACLtoChannel(contextcid, acl);

			// restart the loop
			moveUser(state, context, ijson);
			return false;
		} catch (InvalidChannelException e) {
			e.printStackTrace();
		} catch (InvalidSessionException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public void addACLtoChannel(int channelid, ACL acl)
			throws InvalidSecretException, ServerBootedException {
		try {
			// create variable holders
			ACLListHolder acls = new ACLListHolder();
			GroupListHolder groups = new GroupListHolder();
			BooleanHolder inherited = new BooleanHolder();

			// fill variable holders
			server.getACL(channelid, acls, groups, inherited);

			// append ACL
			ArrayList<Murmur.ACL> aclList = new ArrayList<Murmur.ACL>();
			for (Murmur.ACL a : acls.value) {
				aclList.add(a);
			}
			aclList.add(acl);
			server.setACL(channelid, aclList.toArray(new ACL[aclList.size()]),
					groups.value, inherited.value);
		} catch (InvalidChannelException e) {
			e.printStackTrace();
		}

	}

}
