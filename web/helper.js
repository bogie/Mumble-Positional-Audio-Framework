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
var loginState = false;

var ErrorCode = {
		/**
		 * Given whenever an operation was successful
		 */
		SUCCESS: 0,

		/**
		 * There was an internal error. Tough luck.
		 */
		INTERNAL_ERROR: 1,

		/**
		 * The given login credentials were wrong
		 */
		LOGIN_WRONG_CREDENTIALS: 2,

		/**
		 * The given user create credentials were invalid
		 */
		CREATE_INVALID_CREDENTIALS: 3,

		/**
		 * The given name has already been taken
		 */
		CREATE_NAME_TAKEN: 4,

		/**
		 * The attempting user is not logged in
		 */
		RIGHT_NOT_LOGGED_IN: 5,

		/**
		 * The rights of the attempting user are too low
		 */
		RIGHT_INSUFFICIENT_PERMISSION: 6
}

function User(id, name, permlvl) {
	this.id = id;
	this.name = name;
	this.permlvl = permlvl;
}

var user;

function updateUserInfo() {
	$("#logout_userinfo").text("Hello "+user.name+"! Permission level: "+user.permlvl);
}

function loadPage(event) {
	fetchUserInfo();
	checkLoginStatus();
	loadServerDetails();
	onHashChanged(event);
}

function onHashChanged(event) {
	// Remove leading #
	var hash = location.hash.substring(1);
	// Hide all other content switches
	$(".cswitch").hide();
	// Show the one switched to
	$("#"+hash).show();
}

function removeCookie() {
	$.removeCookie("JSESSIONID");
}

function forceLogout() {
	removeCookie();
	loginSwitch(false);
}

function checkLoginStatus() {
	$("#debug").append("<br />Checking login status...");
	if($.cookie("JSESSIONID") != null) {
		$("#debug").append("<br />Logged in");
		loginSwitch(true);
	} else {
		$("#debug").append("<br />Not logged in");
		loginSwitch(false);
	}
}

function loginSwitch(login) {
	loginState = login;
	fetchUserInfo();
	if(loginState) {
		$(".loggedout").hide();
		$(".loggedin").show();
	} else {
		$(".loggedin").hide();
		$(".loggedout").show();
	}
}
