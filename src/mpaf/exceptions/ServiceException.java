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
package mpaf.exceptions;

import mpaf.servlets.ErrorCode;

public class ServiceException extends Exception {

	private static final long serialVersionUID = -8681089527783025365L;
	private ErrorCode code;

	public ServiceException(ErrorCode code) {
		super();
		this.code = code;
	}

	public ErrorCode getError() {
		return this.code;
	}
}
