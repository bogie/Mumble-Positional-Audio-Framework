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

import mpaf.Logger;
import mpaf.ServerConfig;
import Ice.InitializationData;
import Ice.Util;
import Murmur.InvalidCallbackException;
import Murmur.InvalidSecretException;
import Murmur.ServerBootedException;

public class IceController {
	private IceModel im;

	public IceController(IceModel im) {
		this.im = im;
		initialize();
	}

	private void initialize() {
		if (im == null) {
			Logger.fatal(this.getClass(), "Model was empty, returning...");
			return;
		}
		// Enable Ice.ImplicitContext, if not enabled getImplicitContext() will
		// return null
		InitializationData id = new InitializationData();
		id.properties = Util.createProperties();
		id.properties.setProperty("Ice.ImplicitContext", "Shared");

		// Initialize Ice interface
		im.setCommunicator(Ice.Util.initialize(id));

		// Save our secret into the implicitContext
		im.getCommunicator().getImplicitContext().put("secret", im.getSecret());

		Ice.ObjectPrx proxy = im.getCommunicator().stringToProxy(
				"Meta:tcp -h " + im.getServerIp() + " -p "+im.getIcePort());
		Logger.debug(this.getClass(), "Began connecting to Ice server.");

		// Fetch meta object from ice to access vservers
		im.setMeta(Murmur.MetaPrxHelper.checkedCast(proxy));

		// Initialize our own adapter so we can receive callbacks from vservers
		Ice.ObjectAdapter adapter = im.getCommunicator()
				.createObjectAdapterWithEndpoints("Callback.Client",
						"tcp -h " + im.getClientIp());
		if (adapter == null) {
			Logger.info(this.getClass(),
					"Failed to initialize adapter, will not receive callbacks.");
		}
		adapter.activate();
		Logger.info(this.getClass(), "Activated local adapter.");

		try {
			Murmur.ServerPrx[] servers = im.getMeta().getBootedServers();
			for (Murmur.ServerPrx server : servers) {
				ServerConfig sc = new ServerConfig(server, im.getMeta()
						.getDefaultConf());
				ServerCallbackI cb = new ServerCallbackI(server, im);
				sc.setCallback(cb);
				im.getServers().put(server.id(), sc);

				Murmur.ServerCallbackPrx cbPrx = Murmur.ServerCallbackPrxHelper
						.uncheckedCast(adapter.addWithUUID(cb));
				server.addCallback(cbPrx);
				Logger.debug(this.getClass(),"Added callback for server: "+server.id());
				im.getCallbacks().put(server.id(), cbPrx);
			}
		} catch (InvalidSecretException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCallbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerBootedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void releaseCallbacks() {
		Murmur.ServerPrx[] servers;
		try {
			servers = im.getMeta().getBootedServers();
			for (Murmur.ServerPrx server : servers) {
				Murmur.ServerCallbackPrx cb = im.getCallbacks().get(server.id());
				if(cb != null)
				{
					server.removeCallback(cb);
					Logger.debug(this.getClass(), "Removed ServerCallbackPrx: "+cb.toString());
				}
			}
		} catch (InvalidSecretException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCallbackException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServerBootedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
