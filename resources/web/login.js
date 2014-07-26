/* 
This sets up all the common stuff, having to do with the top bar, redirects, search bar, logging in, loggin out, etc
*/

$(document).ready(function() {
    var sessionId = getCookie("authenticated_session_id");
    var userType = getCookie('usertype');
    var userName = getCookie('username');
    // set up the correct dashboard if its a creator
    if (userType == 'Creator') {
        $("#dashboardhref").prop("href", "creator_overview?creator=" + userName);
    }


    setupCreatorSearch();
    fillUserInfoMustacheFromCookie();
    setupLogout();

    console.log(document.cookie);
    $('#registerForm').bootstrapValidator({
        message: 'This value is not valid',
        excluded: [':disabled'],

    });
    $('#loginForm').bootstrapValidator({
        message: 'This value is not valid',
        excluded: [':disabled'],

    });

    showHideElementsLoggedIn();


    $('#registerBtn').button();

    // $('#registerForm').submit(function(){
    //  $.each($('#registerForm').serializeArray(), function(i, field) {
    //         values[field.name] = field.value;
    //         console.log(values);
    //  });


    // });


    // !!!!!!They must have names unfortunately
    $("#registerBtn").click(function(event) {

        // serializes the form's elements.
        var formData = $("#registerForm").serializeArray();
        console.log(formData);

        // Loading
        $(this).button('loading');

        var url = sparkService + "registeruser"; // the script where you handle the form input.

        $.ajax({
            type: "POST",
            url: url,
            xhrFields: {
                withCredentials: true
            },
            data: formData,
            success: function(data, status, xhr) {

                xhr.getResponseHeader('Set-Cookie');
                // document.cookie="authenticated_session_id=" + data + 
                // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
                // Hide the modal, reset the form, show successful
                $("#userloginModal").modal('hide');
                $('#registerForm')[0].reset();

                toastr.success('Registered and logged in.')
                showHideElementsLoggedIn();


                console.log(document.cookie);
                setTimeout(
                    function() {
                        var url = "userdashboard_overview";
                        window.location.replace(url);

                    }, 1000);


            },
            error: function(request, status, error) {
                delete_cookie("authenticated_session_id");
                toastr.error(request.responseText);
            }
        });



        event.preventDefault();
    });


    $("#signinBtn").click(function(event) {

        // serializes the form's elements.
        var formData = $("#loginForm").serializeArray();
        console.log(formData);

        // Loading
        $(this).button('loading');


        var url;
        // Decide if its a creator or not
        var isCreator = $("#creatorCheckbox").is(':checked')
        console.log(isCreator);
        if (isCreator) {
            url = sparkService + "creatorlogin";
        } else {
            url = sparkService + "userlogin";
        }

        // username = $('#userLoginDiv').find('input[name="username"]').val();
        // // = $("#inputUsername3").val();
        // document.cookie = "username=" + username ;


        $.ajax({
            type: "POST",
            url: url,
            xhrFields: {
                withCredentials: true
            },
            data: formData,
            success: function(data, status, xhr) {

                //    console.log(xhr);
                // console.log(asdf);
                // console.log(xhr.getAllResponseHeaders()); 

                // alert(data); // show response from the php script.

                xhr.getResponseHeader('Set-Cookie');

                // old way
                // document.cookie="authenticated_session_id=" + data + 
                // "; expires=" + expireTimeString(60*60); // 1 hour (field is in seconds)
                // Hide the modal, reset the form, show successful
                $("#userloginModal").modal('hide');
                $('#loginForm')[0].reset();

                toastr.success('Logged in.');

                showHideElementsLoggedIn();



                console.log(document.cookie);
                console.log(formData.username);

                // GO to the dashboard
                if (!isCreator) {
                    window.location.replace("userdashboard_overview");
                } else {
                    console.log(formData);
                    var url = "creator_overview?creator=" + formData[0]['value'];
                    console.log(url);
                    window.location.replace(url);

                }


            },
            error: function(request, status, error) {
                delete_cookie("authenticated_session_id");
                toastr.error(request.responseText);
            }



        });

        $("#signinBtn").button('reset');
        event.preventDefault();
        return false;
    });



    // Logging out


});