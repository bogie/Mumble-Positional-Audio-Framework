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
function makeGenericCallback(callback) {
	return function(data) {
		// If there is no data.success field or it is true, nothing needs to be done
		if(data.success == undefined || data.success) {
			callback(data);
		} else {
			// If there was an error
			switch (data.errorcode) {
			// If the error was that we are not logged in, log us out
			case ErrorCode.RIGHT_NOT_LOGGED_IN:
				forceLogout();
			break;
			// Otherwise just call the callback
			default:
				callback(data);
			break;
			}
		}
	}
}

function fetchUserInfo() {
	$.get("/userinfo",makeGenericCallback(onUserInfoResponse));
}

function onUserInfoResponse(data) {
	if(data.success == undefined) {
		user = new User(data.id, data.name, data.permissionlvl);
		updateUserInfo();
	} else {
		switch(data.errorcode) {
			default:
				$("#debug").append("<br />UserInfo: Received unhandled error "+data.errorcode);
			break;
		}
	}
}

function doLogin() {
	var user = $("#login_user").val();
	var pass = $("#login_pass").val();
	$.post("/login", {login_name: user, login_pass: pass},makeGenericCallback(onLoginResponse));
}

function onLoginResponse(data) {
	var resultbox = $("#login_result");
	resultbox.removeClass("errorbox successbox");
	if(data.success) {
		loginSwitch(true);
		resultbox.text("Login successfull!");
		resultbox.addClass("successbox");
	} else {
		resultbox.addClass("errorbox");
		switch(data.errorcode) {
			case ErrorCode.LOGIN_WRONG_CREDENTIALS:
				resultbox.text("Login failed: Wrong credentials");
			break;
			default:
				resultbox.text("Unknown error number "+data.errorcode);
			break;
		}
	}
	resultbox.fadeIn().delay(1000).fadeOut();
}

function doLogout() {
	$.get("/logout",makeGenericCallback(onLogoutResponse));
}

function onLogoutResponse(data) {
	var resultbox = $("#logout_result");
	resultbox.removeClass("errorbox successbox");
	if(data.success) {
		forceLogout();
		resultbox.text("Logout successfull!");
		resultbox.addClass("successbox");
	} else {
		switch(data.errorcode) {
			default:
				resultbox.text("Logout: Received unhandled error number "+data.errorcode);
			break;
		}
	}
	resultbox.fadeIn().delay(1000).fadeOut();
}

function doCreate() {
	var user = $("#usercreate_user").val();
	var pass = $("#usercreate_pass").val();
	var email = $("#usercreate_email").val();
	var permlvl = $("#usercreate_permlvl").val();
	$.post("/usercreate", {create_name: user, create_pass: pass, create_email: email, create_permlvl: permlvl},makeGenericCallback(onCreateResponse));
}
function onCreateResponse(data) {
	var resultbox = $("#usercreate_result");
	resultbox.removeClass("errorbox successbox");
	if(data.success) {
		resultbox.text("Created successfully");
		resultbox.addClass("successbox");
	} else {
		resultbox.addClass("errorbox");
		switch(data.errorcode) {
			case ErrorCode.CREATE_INVALID_CREDENTIALS:
				resultbox.text("Invalid credentials!");
			break;
			case ErrorCode.CREATE_NAME_TAKEN:
				resultbox.text("Name taken!");
			break;
			default:
				resultbox.text("Unknown error number "+data.errorcode);
			break;
		}
	}
	resultbox.fadeIn().delay(1000).fadeOut();
}

function loadServerDetails() {
	$.get("/serverdetails",makeGenericCallback(onServerDetailsResponse));
}

function onServerDetailsResponse(data) {
	$("#serverdetails_servertable tr").remove();
	// For each server add all the JSON data we receive
	$.each(data.servers,function(index, item) {
		$("<tr>").attr("id","serverdetails_server"+index).appendTo("#serverdetails_servertable");
		$("<td>").text(data.servers[index].id).appendTo("#serverdetails_server"+index);
		$("<td>").text(data.servers[index].registername).appendTo("#serverdetails_server"+index);
		$("<td>").text(data.servers[index].port).appendTo("#serverdetails_server"+index);
		$("<td>").text(data.servers[index].bandwidth).appendTo("#serverdetails_server"+index);
	});
}
