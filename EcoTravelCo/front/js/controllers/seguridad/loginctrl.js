'use strict';


materialAdmin
	.controller('LoginCtrl', function ($scope, $rootScope, $http, $location,jwtHelper,$state,$window,growlService) {
		$scope.registro = {};
		$scope.usuario = {login:"",contrasenia:""};
		this.login = 1;
		this.register = 0;
		this.forgot = 0;

		$scope.registrarUsuario = function () {

			$http.post("http://localhost:8181/cliente/",$scope.registro,{})
				.success(function(res){
					$scope.registro = {};
					$scope.usuario = {login:document.getElementById("login").value,contrasenia:document.getElementById("pass").value};
					$scope.autenticarUsuario();

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
					sessionStorage.setItem ("nombreusuario",res.nombre + " " + res.apellido) ;
					sessionStorage.setItem ("correousuario",res.correo_electronico) ;
					sessionStorage.setItem ("tipo",res.tipo) ;
					sessionStorage.setItem ("foto",res.foto) ;

					console.log(sessionStorage.getItem("nombreusuario"));
					console.log(res);

					$window.location.href = '/#/home';
					console.log(sessionStorage.token);

				}).error(function(res){
            //    growlService.growl('Error de autenticación.', 'danger');
				console.log("Doesn't work");
				console.log("Que trae esto: "+res);

			});

		};

		$scope.limpiarSesion = function () {

			sessionStorage.clear();
		}

		$scope.limpiarSesion();

		//facebook
		$scope.autenticarfb = function () {
        		/*	$http.post("http://localhost:8181/seguridad/autenticar/fb",$scope.usuario,{})
        				.success(function(res){
        					$scope.registro = {};
        				}).error(function(res){
                        growlService.growl('Error de autenticación.', 'danger');
        				console.log("Doesn't work");
        				console.log("Que trae esto: "+res);
        			});
*/
        		};
	});



