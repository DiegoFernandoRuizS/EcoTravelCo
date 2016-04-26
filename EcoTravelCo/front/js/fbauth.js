$(function() {
	var app_id = '985703204846099';
	var scopes = 'email, user_friends, user_online_presence';

	var btn_login = '<a href="" data-ng-click="autenticarfb();" id="loginFB"><img src="img/social/login-with-facebook.png" alt="Sign in with Facebook"></a>';

	var div_session = "<div id='facebook-session'>"+
					  "<strong></strong>"+
					  "<img>"+
					  "<a href='#' id='logout' class='btn btn-danger'>Cerrar sesión</a>"+
					  "</div>";

    console.log("entra");

	window.fbAsyncInit = function() {

	  	FB.init({
	    	appId      : app_id,
	    	status     : true,
	    	cookie     : true, 
	    	xfbml      : true, 
	    	version    : 'v2.1'
	  	});

	  	FB.getLoginStatus(function(response) {
	    	statusChangeCallback(response, function() {});
	  	});
  	};

  	var statusChangeCallback = function(response, callback) {
  	console.log("verificacion de status");
            console.log(response);
   		
    	if (response.status === 'connected') {
      		getFacebookData();
    	} else {
     		callback(false);
    	}
  	}

  	var checkLoginState = function(callback) {
    	FB.getLoginStatus(function(response) {
      		callback(response);
    	});
  	}

  	var getFacebookData =  function() {
            console.log("Trae los datos");
  		FB.api('/me', function(response) {
	  		$('#loginFB').after(div_session);
	  		$('#loginFB').remove();
	  		$('#facebook-session strong').text("Bienvenido: "+response.name);
	  		$('#facebook-session img').attr('src','http://graph.facebook.com/'+response.id+'/picture?type=large');
	  	});
             //window.location.href = 'http://localhost:9291/#/home';
  	}

  	var facebookLogin = function() {
  		checkLoginState(function(data) {
                    console.log("Verifico estado de login");
  			if (data.status !== 'connected') {
  				FB.login(function(response) {
  					if (response.status === 'connected')
                                            console.log("Se conecto");
  						getFacebookData();
  				}, {scope: scopes});
  			}
  		})
  	}

  	var facebookLogout = function() {
  		checkLoginState(function(data) {
  			if (data.status === 'connected') {
                            console.log("Cierra sesión");
				FB.logout(function(response) {
					$('#facebook-session').before(btn_login);
					$('#facebook-session').remove();
				})
			}
  		})

  	}



  	$(document).on('click', '#loginFB', function(e) {
  		e.preventDefault();
                console.log("Presiono login");
  		facebookLogin();
  	})

  	$(document).on('click', '#logout', function(e) {
  		e.preventDefault();

  		if (confirm("¿Está seguro?"))
  			facebookLogout();
  		else 
  			return false;
  	})

})
