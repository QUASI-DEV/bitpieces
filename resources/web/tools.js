function expireTimeString(seconds) {
	var now = new Date();
	var time = now.getTime();
	time += seconds * 1000;
	now.setTime(time);
	return now.toUTCString();
}

function getCookies() {
  var c = document.cookie, v = 0, cookies = {};
  if (document.cookie.match(/^\s*\$Version=(?:"1"|1);\s*(.*)/)) {
    c = RegExp.$1;
    v = 1;
  }
  if (v === 0) {
    c.split(/[,;]/).map(function(cookie) {
      var parts = cookie.split(/=/, 2),
      name = decodeURIComponent(parts[0].trimLeft()),
      value = parts.length > 1 ? decodeURIComponent(parts[1].trimRight()) : null;
      cookies[name] = value;
    });
  } else {
    c.match(/(?:^|\s+)([!#$%&'*+\-.0-9A-Z^`a-z|~]+)=([!#$%&'*+\-.0-9A-Z^`a-z|~]*|"(?:[\x20-\x7E\x80\xFF]|\\[\x00-\x7F])*")(?=\s*[,;]|$)/g).map(function($0, $1) {
      var name = $0,
      value = $1.charAt(0) === '"'
      ? $1.substr(1, -1).replace(/\\(.)/g, "$1")
      : $1;
      cookies[name] = value;
    });
  }
  return cookies;
}
function getCookie(name) {
  return getCookies()[name];
}
function delete_cookie( name ) {
  document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

function showHideElementsLoggedIn() {
  sessionId = getCookie("authenticated_session_id");

  if (sessionId != null) {
    $("#dashboardhref").removeClass("hide");
    $("#loginhref").addClass("hide");
    $("#logouthref").removeClass("hide");

  } else {
    $("#dashboardhref").addClass("hide");
    $("#loginhref").removeClass("hide");
    $("#logouthref").addClass("hide");

  }
}

function fillUserHighChartStandardTemplate(url, id) {
       var url = "http://localhost:4567/" + sessionId + "/" + url// the script where you handle the form input.
        $.ajax({
            type: "GET",
            url: url,
            xhrFields: {
              withCredentials: true
          },
      // data: seriesData, 
      success: function(data, status, xhr) {
        console.log(data);
            // var jsonObj = JSON.parse(data);
            var jsonObj = jQuery.parseJSON(data);
            // var jsonObj = JSON.stringify(data);
            // console.log(jsonObj);
            standardTemplate(id, jsonObj);

        },
        error: function (request, status, error) {

            toastr.error(request.responseText);
        }
    });

}

function fillUserHighChartPieChartTemplate(url, id) {
       var url = "http://localhost:4567/" + sessionId + "/" + url// the script where you handle the form input.
        $.ajax({
            type: "GET",
            url: url,
            xhrFields: {
              withCredentials: true
          },
      // data: seriesData, 
      success: function(data, status, xhr) {
        console.log(data);
            // var jsonObj = JSON.parse(data);
            var jsonObj = jQuery.parseJSON(data);
            // var jsonObj = JSON.stringify(data);
            // console.log(jsonObj);
            pieChartTemplate(id, jsonObj);

        },
        error: function (request, status, error) {

            toastr.error(request.responseText);
        }
    });

}