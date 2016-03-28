'use strict';

/**
 * @ngdoc function
 * @name sistemagestionuniversitario.controller:PersonaListaCtrl
 * @description
 * # canastaCtrl
 * Controller of the Canasta
 */
angular.module('materialAdmin')
	.controller('CanastaCtrl', function ($scope, $rootScope, $http, $location) {
		
		$rootScope.canasta = [];
		$scope.data = {};
		$scope.error = [];

		console.log("Consultando canasta...");
		$http.get("http://localhost:8181/canasta/", {
				withCredentials: true,
				headers: {token: sessionStorage.token}
			}).success(function(res){
				$rootScope.canasta=res;
				if($rootScope.canasta.length > 0){
					$rootScope.idOrdenCanasta = $rootScope.canasta[0].idorden;
				}
				console.log($scope.datos);
			}).error(function(res){
			console.log("Doesn't work");
			console.log("Que trae esto: "+res);
		});
		

		$scope.borrarProductoCanasta = function (idItem) {
			$http.delete("http://localhost:8181/canasta/?id_orden_item="+idItem)
				.success(function(res){
					$http.get("http://localhost:8181/canasta/", {
					withCredentials: true,
						headers: {token: sessionStorage.token}
				}).success(function(res){
							$rootScope.canasta=res
							console.log(res);

						}).error(function(res){
					});
				}).error(function(res){
				console.log("Doesn't work");
				console.log("Que trae esto: "+res);
			});
		}

		$scope.adicionarProductoCanasta = function () {
			
			if(sessionStorage.token == undefined){
				$location.path( "/login.html" );
			} else {
				$http.post("http://localhost:8181/canasta/?" +
					"id_producto=" + $rootScope.prodId + "&cantidad=" + $rootScope.cantidadCanasta, {
					withCredentials: true,
					headers: {token: sessionStorage.token}
				}).success(function (res) {
					//Good
				}).error(function (res) {
					console.log("Doesn't work");
					console.log("Que trae esto: " + res);
				});
				$rootScope.cantidadCanasta = 1;
			}
		}

		$scope.confirmarCanasta = function () {
			$scope.error = [];
			$scope.detalle="";
			$http.post("http://localhost:8181/canasta/confirmar/?pru=p", {
				withCredentials: true,
				headers: {token: sessionStorage.token}
			}).success(function(res){
					if(res instanceof Array){
						$scope.error=res;
						
						// Asignamos los errores
						for(var i=0;i<$rootScope.canasta.length;i++){
							var mensaje = "";
							var clase = "";
							for(var ct=0;ct<$scope.error.length;ct++){
								if($rootScope.canasta[i].id_producto == $scope.error[ct].id_producto_id){
									if($scope.error[ct].estado == "Inactivo"){
										mensaje = "El producto ya no se encuentra disponible.";
										clase = "warning c-white";
									} else if($scope.error[ct].sum > $scope.error[ct].cantidad_actual){
										mensaje = "La cantidad supera el cupo disponible.";
										clase = "warning c-white";
									}
								}
							}
							$rootScope.canasta[i]["error"] = mensaje;
							$rootScope.canasta[i]["clase"] = clase;
						}
						
					} else {
						$location.path( "/orden/detalle" );
					}
					console.log(res);
				}).error(function(res){
				console.log("Doesn't work");
				console.log("Que trae esto: "+res);
			});
		}

		$scope.detailProd = function (id) {
			$rootScope.prodId= id;
		};
	});
