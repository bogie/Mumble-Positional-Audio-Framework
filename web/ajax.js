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
		if(data.success) {
			callback(data);
		}
		switch (data.errorcode) {
			case ErrorCode.RIGHT_NOT_LOGGED_IN:
				forceLogout();
			break;
			default:
				callback(data);
			break;
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
	if(data.success) {
		loginSwitch(true);
	} else {
		switch(data.errorcode) {
			case ErrorCode.LOGIN_WRONG_CREDENTIALS:
				$("#debug").append("<br />Login: Failed wrong credentials");
			break;
			default:
				$("#debug").append("<br />Login: Received unhandled error "+data.errorcode);
			break;
		}
	}
}

function doLogout() {
	$.get("/logout",makeGenericCallback(onLogoutResponse));
}

function onLogoutResponse(data) {
	if(data.success) {
		forceLogout();
	} else {
		switch(data.errorcode) {
			default:
				$("#debug").append("<br />Login: Received unhandled error "+data.errorcode);
			break;
		}
	}
}

function doCreate() {
	var user = $("#create_user").val();
	var pass = $("#create_pass").val();
	var email = $("#create_email").val();
	var permlvl = $("#create_permlvl").val();
	$.post("/usercreate", {create_name: user, create_pass: pass, create_email: email, create_permlvl: permlvl},makeGenericCallback(onCreateResponse));
}
function onCreateResponse(data) {
	if(data.success) {
		$("#debug").append("<br />User created!");
	} else {
		switch(data.errorcode) {
			case ErrorCode.CREATE_INVALID_CREDENTIALS:
				$("#debug").append("<br />UserCreate: Invalid credentials");
			break;
			case ErrorCode.CREATE_NAME_TAKEN:
				$("#debug").append("<br />UserCreate: Name already taken");
			break;
			default:
				$("#debug").append("<br />UserCreate: Received unhandled error "+data.errorcode);
			break;
		}
	}
}

function loadServerDetails() {
	$.get("/serverdetails",makeGenericCallback(onServerDetailsResponse));
}

function onServerDetailsResponse(data) {
	$("#servertable tr").remove();
	// For each server add all the JSON data we receive
	$.each(data.servers,function(index, item) {
		$("<tr>").attr("id","server"+index).appendTo("#servertable");
		$("<td>").text(data.servers[index].id).appendTo("#server"+index);
		$("<td>").text(data.servers[index].registername).appendTo("#server"+index);
		$("<td>").text(data.servers[index].port).appendTo("#server"+index);
		$("<td>").text(data.servers[index].bandwidth).appendTo("#server"+index);
	});
}
