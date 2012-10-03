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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mpaf.sql.SqlHandler;
import Murmur.InvalidCallbackException;
import Murmur.InvalidSecretException;
import Murmur.ServerBootedException;

public class Authenticator extends Thread {
	/**
	 * initiates all required Ice functions initiates database connection sets
	 * the ServerAuthenticator for our murmur server to the one supplied by
	 * {@link mpaf.auth.ServerAuthenticatorI ServerAuthenticatorI }
	 * 
	 * @author bogie
	 * @version 1.0
	 */
	private Murmur.ServerPrx server = null;
	private String servername = "Morsus Mihi Mumble";
	private ServerAuthenticatorI authenticator;
	private SqlHandler sqlH;
	// private Connection conn;
	private Murmur.MetaPrx meta;
	private Ice.Communicator ic;

	private boolean prepared = false;

	public boolean running = false;

	public void prepare(SqlHandler sqlH) {
		this.sqlH = sqlH;
		this.prepared = true;
	}

	public void run() {
		this.running = true;
		if (!this.prepared) {
			System.out
					.println("Authenticator: Tried to start unprepared thread. Call prepare() first. Exiting.");
			return;
		}
		try {
			if (!this.init()) {
				System.out.println("Authenticator: Init failed");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean init() throws SQLException {

		// this.conn = this.sqlH.getConnection();

		// Basic ice operations
		ic = null;
		try {
			ic = Ice.Util.initialize();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		// This is the actual connection to the mumble ice interface(we are
		// using a password)
		Map<String, String> secret = new HashMap<String, String>();
		secret.put("secret", "AeJue1oa");
		// ic.getImplicitContext().put("secret", "AeJue1oa");
		// Ice.ObjectPrx proxy =
		// ic.stringToProxy("Meta:tcp -h localhost -p 6502").ice_context(secret);
		Ice.ObjectPrx proxy = ic
				.stringToProxy("Meta:tcp -h 192.168.122.101 -p 6502");
		System.out.println("Authenticator: Connecting via ICE");

		// meta object to access servers
		this.meta = Murmur.MetaPrxHelper.checkedCast(proxy);
		// this adapter is used for the ServerAuthenticatorI
		Ice.ObjectAdapter adapter = ic.createObjectAdapterWithEndpoints(
				"Callback.Client", "tcp -h 10.8.0.4");
		if (adapter.equals(null))
			System.out.println("could not initiate adapter: "
					+ adapter.getName());
		adapter.activate();
		System.out.println("Authenticator: Activated local Adapter");

		try {
			System.out.println("Authenticator: Inside try.");
			// these are all running servers
			Murmur.ServerPrx[] servers = this.meta.getBootedServers();
			Map<String, String> defaultConf = this.meta.getDefaultConf();
			for (Entry<String, String> conf : defaultConf.entrySet()) {
				System.out.println(conf.getKey() + " | " + conf.getValue());
			}
			System.out.println("Authenticator: Got all Servers. "
					+ servers.length);
			for (int i = 0; i < servers.length; i++) {
				if (servers[i].getConf("registerName").equals(servername)) {
					this.server = servers[i];
				}
			}
		} catch (InvalidSecretException e) {
			e.printStackTrace();
			return false;
		}
		if (this.server == null) {
			System.out.println("Could not find server with name: "
					+ this.servername);
			return false;
		}
		// starting the authenticator
		this.authenticator = new ServerAuthenticatorI(this.server, adapter,
				this.sqlH);
		System.out.println("Authenticator: Setting Authenticator via ICE");
		Murmur.ServerUpdatingAuthenticatorPrx auth = Murmur.ServerUpdatingAuthenticatorPrxHelper
				.uncheckedCast(adapter.addWithUUID(this.authenticator));
		try {
			this.server.setAuthenticator(auth);
		} catch (InvalidCallbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidSecretException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerBootedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

	public boolean setAuthenticator() {
		// this adapter is used for the ServerAuthenticatorI
		Ice.ObjectAdapter adapter = ic.createObjectAdapterWithEndpoints(
				"Callback.Client", "tcp -h 127.0.0.1");
		if (adapter.equals(null))
			System.out.println("could not initiate adapter: "
					+ adapter.getName());
		adapter.activate();
		System.out.println("Authenticator: Activated local Adapter");

		try {
			System.out.println("Authenticator: Inside try.");
			// these are all running servers
			Murmur.ServerPrx[] servers = this.meta.getBootedServers();
			Map<String, String> defaultConf = this.meta.getDefaultConf();
			for (Entry<String, String> conf : defaultConf.entrySet()) {
				System.out.println(conf.getKey() + " | " + conf.getValue());
			}
			System.out.println("Authenticator: Got all Servers. "
					+ servers.length);
			for (int i = 0; i < servers.length; i++) {
				Map<String, String> confs = servers[i].getAllConf();
				for (String key : confs.keySet()) {
					System.out.println("Key: " + key + " is " + confs.get(key));
				}
				if (servers[i].getConf("registerName").equals(servername)) {
					this.server = servers[i];
				}
			}
		} catch (InvalidSecretException e) {
			e.printStackTrace();
			return false;
		}
		if (this.server == null) {
			System.out.println("Could not find server with name: "
					+ this.servername);
			return false;
		}
		// starting the authenticator
		this.authenticator = new ServerAuthenticatorI(this.server, adapter,
				this.sqlH);
		System.out.println("Authenticator: Setting Authenticator via ICE");
		Murmur.ServerUpdatingAuthenticatorPrx auth = Murmur.ServerUpdatingAuthenticatorPrxHelper
				.uncheckedCast(adapter.addWithUUID(this.authenticator));
		try {
			this.server.setAuthenticator(auth);
		} catch (InvalidCallbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidSecretException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerBootedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public String getServername() {
		return servername;
	}

	public void setServername(String servername) {
		this.servername = servername;
	}
}
