'use strict';

/**
 * @ngdoc function
 * @name sistemagestionuniversitario.controller:PersonaListaCtrl
 * @description
 * # canastaCtrl
 * Controller of the Canasta
 */
angular.module('materialAdmin')
	.controller('CanastaCtrl', function ($scope, $rootScope, $http, $location, growlService) {

		$rootScope.canasta = [];
		$scope.data = {};
		$scope.error = [];

		if(sessionStorage.token != undefined){
			console.log("Consultando canasta...");
			$http.get("http://localhost:8181/canasta/", {
				withCredentials: true,
				headers: {token: sessionStorage.token}
			}).success(function (res) {
				$rootScope.canasta = res;
				if ($rootScope.canasta.length > 0) {
					$rootScope.idOrdenCanasta = $rootScope.canasta[0].idorden;
				}
				console.log($scope.datos);
			}).error(function (res) {
				console.log("Doesn't work");
				console.log("Que trae esto: " + res);
			});
		}

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
			growlService.growl('Producto eliminado de la Canasta.', 'success');
		}

		$scope.adicionarProductoCanasta = function () {
			var error = false;

			if(sessionStorage.token == undefined){
				var error = true;
				growlService.growl('Debe inicial sesión para adicionar productos a la Canasta.', 'danger');
			}

			if($rootScope.cantidadCanasta == undefined){
				var error = true;
				growlService.growl('Ingrese una cantidad valida.', 'danger');
			}

			if(!error) {
				$scope.infoProducto = {
					id_producto : $rootScope.prodId,
					cantidad : $rootScope.cantidadCanasta
				}
				
				$scope.sesion = {withCredentials: true,	headers: {token: sessionStorage.token}}

				$http.post("http://localhost:8181/canasta/", $scope.infoProducto, $scope.sesion
				).success(function (res) {
					$scope.mensaje = "Producto Agregado al Carro."

					$http.get("http://localhost:8181/canasta/", {
						withCredentials: true,
						headers: {token: sessionStorage.token}
					}).success(function (res) {
						$rootScope.canasta = res;
						if ($rootScope.canasta.length > 0) {
							$rootScope.idOrdenCanasta = $rootScope.canasta[0].idorden;
						}
						console.log($scope.datos);
					}).error(function (res) {
						console.log("Doesn't work");
						console.log("Que trae esto: " + res);
					});
				}).error(function (res) {
					console.log("Doesn't work");
					console.log("Que trae esto: " + res);
				});
				$rootScope.cantidadCanasta = 1;
				growlService.growl('Producto agregado a la Canasta.', 'success');
			}
		}

		$scope.confirmarCanasta = function () {
			$scope.error = [];
			$scope.detalle="";
			$scope.sesion = {withCredentials: true,	headers: {token: sessionStorage.token}}
			$http.post("http://localhost:8181/canasta/confirmar/", {}, $scope.sesion, {}
			).success(function(res){
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
			growlService.growl('Canasta confirmada.', 'success');
		}

		$scope.detailProd = function (id) {
			$rootScope.prodId= id;
		};
	});
