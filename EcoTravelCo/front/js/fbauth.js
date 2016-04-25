/*$(function() {
	var app_id = '985703204846099';
	var scopes = 'email, user_friends, user_online_presence';
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
     	//	callback(false);
    	}
  	}

  	var checkLoginState = function(callback) {
    	FB.getLoginStatus(function(response) {
      		callback(response);
    	});
  	}

  	var getFacebookData =  function() {
        FB.api('/me?fields=id,name,email,permissions', function(response){
        console.log(response);
  		if(response.name!=null){
  		            	    //sessionStorage.token = res.token;
  		            	    sessionStorage.setItem ("auth","fb");
                            sessionStorage.setItem ("nombreusuario",response.name);
                            sessionStorage.setItem ("correousuario",response.email) ;
                            sessionStorage.setItem ("tipo","CLIENTE");
                            sessionStorage.setItem ("foto",'http://graph.facebook.com/'+response.id+'/picture?type=large');
                            // console.log(sessionStorage.getItem("nombreusuario"));
                            console.log(response);

                        var user={
                          "apellido_sec" : null,
                          tipo : "CLIENTE",
                          correo_electronico : "user@user.com",
                          login : "user",
                          nombre : response.name,
                          foto: sessionStorage.getItem("foto"),
                          id_direccion_id : null,
                          apellido : null,
                          nombre_sec : "Fernando",
                          contrasenia : "user",
                          telefono : "123456789"
                        }
                        console.log(user);
                            //guardarlo
                           $.ajax({
                               url : "http://localhost:8181/cliente/",
                               type: "POST",
                               dataType: "json",
                               data : user,
                               success: function(data, textStatus, jqXHR)
                               {
                                   alert("OK "+data);
                                   //data - response from server
                               },
                               error: function (jqXHR, textStatus, errorThrown)
                               {
                                    alert("ERROR "+errorThrown);
                               }
                           });

//        window.location.href = 'http://localhost:9291/#/home';


  		}else{

  		}

	  	});

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
				window.location.href = 'http://localhost:9291/login.html#/home';

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
	    facebookLogout();
  	})

})
