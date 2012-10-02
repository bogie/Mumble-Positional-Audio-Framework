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
package mpaf;

import java.util.Scanner;

import mpaf.ice.IceController;
import mpaf.ice.IceModel;

import org.eclipse.jetty.server.Server;

public class ConsoleParser implements Runnable {
	IceController iceC;
	IceModel iceM;
	Server server;

	public ConsoleParser(IceController iceC, IceModel iceM, Server server) {
		this.iceC = iceC;
		this.iceM = iceM;
		this.server = server;
	}

	@Override
	public void run() {
		Scanner in = new Scanner(System.in);
		while (in.hasNextLine()) {
			parseInput(in.nextLine());
		}
		in.close();
	}

	public void parseInput(String line) {
		String[] split = line.split(" ");
		if (split.length < 1)
			return;
		if (split[0].equalsIgnoreCase("derp")) {
			System.out.println("herp");
		} else if (split[0].equalsIgnoreCase("stop")) {
			System.exit(0);
		} else if (split[0].equalsIgnoreCase("ice")) {
			if (split.length < 2) {
				Logger.info(getClass(),
						"Valid commands for context \"ice\" are: ");
				Logger.info(getClass(), "servers");
				return;
			}
			if (split[1].equalsIgnoreCase("servers")) {
				if (split.length < 3) {
					Logger.info(getClass(),
							"Valid commands for context \"ice servers\" are: ");
					Logger.info(getClass(), "list");
					return;
				}

				if (split[2].equalsIgnoreCase("list")) {
					for (ServerConfig s : iceM.getServers().values()) {
						Logger.info(this.getClass(),
								s.getId() + " - " + s.getRegistername());
					}
				} else {
					Logger.info(getClass(),
							"Valid commands for context \"ice servers\" are: ");
					Logger.info(getClass(), "list");
				}
			}
		} else {
			Logger.info(getClass(), "Valid commands are: ");
			Logger.info(getClass(), "derp");
			Logger.info(getClass(), "stop");
			Logger.info(getClass(), "ice");
		}
	}
}
