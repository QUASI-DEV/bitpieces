$(document).ready(function() {

    sessionId = getCookie("authenticated_session_id");
    userType = getCookie("usertype");

    // fillUserInfoMustache('get_user_data');

    setupMiniSubmenu();
    navigateWithParams();


});