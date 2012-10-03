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
package mpaf.auth;

import java.util.Map;

import mpaf.sql.SqlHandler;
import Ice.Current;
import Ice.StringHolder;
import Murmur.GroupNameListHolder;
import Murmur.UserInfo;
import Murmur.UserInfoMapHolder;

public class ServerAuthenticatorI extends
		Murmur._ServerUpdatingAuthenticatorDisp {
	private static final long serialVersionUID = 1330413162128844146L;

	/**
	 * used for user authentication on mumble
	 * 
	 * @author bogie
	 * @version 1.0
	 */
	// private Murmur.ServerPrx server;
	// private Ice.ObjectAdapter adapter;
	// private SqlHandler sqlH;
	// private Connection conn;

	public ServerAuthenticatorI(Murmur.ServerPrx server,
			Ice.ObjectAdapter adapter, SqlHandler sqlH) {
		/**
		 * ServerAuthenticatorI is used for user authentication on murmur
		 * servers it holds a list of overloaded functions for management.
		 * 
		 * @param return 0 means fall back to internal use
		 */
		System.out.println("ServerAuthenticatorI: Object instanced!");
		// this.server = server;
		// this.adapter = adapter;
		// this.sqlH = sqlH;
		// try {
		// this.conn = sqlH.getConnection();
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
	}

	public int registerUser(Map<UserInfo, String> info, Current __current) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int unregisterUser(int id, Current __current) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Map<Integer, String> getRegisteredUsers(String filter,
			Current __current) {
		// TODO Auto-generated method stub
		return null;
	}

	public int setInfo(int id, Map<UserInfo, String> info, Current __current) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int setTexture(int id, byte[] tex, Current __current) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int authenticate(String name, String pw, byte[][] certificates,
			String certhash, boolean certstrong, StringHolder newname,
			GroupNameListHolder groups, Current __current) {
		/**
		 * authenticates users
		 * 
		 * @param name
		 *            name supplied by the user on connect, this should be a
		 *            skynet userid
		 * @param pw
		 *            password supplied by the user
		 * @param certificates
		 *            the users certificates
		 * @param newname
		 *            we can set this to the new name the user should have
		 * @param groups
		 *            set this to the groups the user should have
		 * 
		 */
		System.out.println("DEBUG: ServerAuthenticatorI authenticate: user: "
				+ name + " is trying to auth with certhash: " + certhash);

		System.out.println("user: " + name + " is trying to auth");
		return -1;
	}

	public boolean getInfo(int id, UserInfoMapHolder info, Current __current) {
		// TODO Auto-generated method stub
		return false;
	}

	public int nameToId(String name, Current __current) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String idToName(int id, Current __current) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] idToTexture(int id, Current __current) {
		// TODO Auto-generated method stub
		return null;
	}
}
