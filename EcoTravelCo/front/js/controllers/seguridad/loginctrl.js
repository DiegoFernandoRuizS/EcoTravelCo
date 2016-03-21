'use strict';


angular.module('materialAdmin')
  .controller('LoginCtrl', function ($scope, $rootScope, $http, $location,jwtHelper,$state,$window) {
      	$scope.registro = {};
      	$scope.usuario = {login:"seduardojs",contrasenia:"Asdf1234$"};
		this.login = 1;
        this.register = 0;
        this.forgot = 0;

console.log("...>>>>>" );


      	$scope.registrarUsuario = function () {


 	$http.post("http://localhost:8181/cliente/",$scope.registro,{})
	    
	    .success(function(res){
      	$scope.registro = {};

	    
	    }).error(function(res){	  	  
	        console.log("Doesn't work");
	        console.log("Que trae esto: "+res);

	    });
		
		};	

$scope.autenticarUsuario = function () {


 	$http.post("http://localhost:8181/seguridad/autenticar",$scope.usuario,{})
	    
	    .success(function(res){
      	$scope.registro = {};
	    console.log(jwtHelper.decodeToken(res.token));

		sessionStorage.token = res.token;
		$window.location.href = '/#/home';
	    console.log(sessionStorage.token);

	    
	    }).error(function(res){	  	  
	        console.log("Doesn't work");
	        console.log("Que trae esto: "+res);

	    });
		
		};	     	    

    
  });
  
  
  
  
