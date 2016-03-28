'use strict';

/**
 * @ngdoc function
 * @name sistemagestionuniversitario.controller:PersonaListaCtrl
 * @description
 * # canastaCtrl
 * Controller of the Canasta
 */
angular.module('materialAdmin')
	.controller('OrdenCtrl', function ($scope, $rootScope, $http, $location, $uibModal) {
		
		$scope.ordenes = [];
		$scope.orden = {};
		$scope.data = {};
		$rootScope.productos;

		$http.get("http://localhost:8181/orden/", {
				withCredentials: true,
				headers: {token: sessionStorage.token}
			}).success(function(res){
				$scope.ordenes=res;
				console.log($scope.datos)
			}).error(function(res){
			console.log("Doesn't work");
			console.log("Que trae esto: "+res);
		});
		
		if($rootScope.idOrdenCanasta != undefined && $rootScope.idOrdenCanasta != 0){
			$rootScope.idOrden = $rootScope.idOrdenCanasta;
			$http.get("http://localhost:8181/orden/detalle/?id="+$rootScope.idOrden)
				.success(function(res){
					$rootScope.productos = [];
					$rootScope.productos=res;
					console.log(res);

				}).error(function(res){
			});
		}
		
		
		$scope.detalleOrden = function (idOrden) {
			$rootScope.idOrdenCanasta = 0;
			$rootScope.idOrden = idOrden;
			$http.get("http://localhost:8181/orden/detalle/?id="+idOrden)
				.success(function(res){
					$rootScope.productos = [];
					$rootScope.productos=res;
					console.log(res);

				}).error(function(res){
			});
		}

		$scope.cancelarOrden = function (idOrden) {
			$rootScope.idOrden = idOrden;
			$http.delete("http://localhost:8181/orden/?id="+idOrden)
				.success(function(res){
					var cancelar = res;
					$http.get("http://localhost:8181/orden/", {
							withCredentials: true,
							headers: {token: sessionStorage.token}
						}).success(function(res){
							$scope.ordenes=res;
							console.log($scope.datos)
						}).error(function(res){
						console.log("Doesn't work");
						console.log("Que trae esto: "+res);
					});
				}).error(function(res){
			});
		}
		
	//Create Modal
	function modalInstances() {
		var modalInstance = $uibModal.open({
			animation: true,
			templateUrl: 'myModalContent.html',
			controller: 'ModalCtrl',
			size: "sm",
			backdrop: true,
			keyboard: true,
			resolve: {
				content: function () {
					return $rootScope.idOrden;
				}
			}
		});
	}
		
	$scope.open = function (url, nombre, id_producto, id_item) {
		$rootScope.urlMod = url;
		$rootScope.nombreMod = nombre;
		$rootScope.idProductoMod = id_producto;
		$rootScope.idItemMod = id_item;
		modalInstances();
	}
})

		
angular.module('materialAdmin')
	.controller('ModalCtrl', function ($scope, $rootScope, $modalInstance, $http) {
		$scope.cal = ["","","","",""];
		$scope.numCal = 0;
		$scope.errorModal = "";

		$scope.calificar = function (valor) {
			$scope.numCal = valor;
			for (var i = 0; i < 5; i++) {
				if(i < valor ){
					$scope.cal[i] = "active";	
				} else {
					$scope.cal[i] = "";
				}
			}
		};

		$scope.ok = function () {
			$scope.comentarios = document.getElementById('comentario').value;
			if($scope.numCal == 0){
				$scope.errorModal = "La calificación es obligatoria.";
			} else if($scope.comentarios == ""){
				$scope.errorModal = "Los comentarios son obligatorios.";
			} else {
				$scope.errorModal = "";
			}

			if($scope.errorModal == ""){
				var envCal = {};
				envCal["id_producto"] = $rootScope.idProductoMod;
				envCal["id_item"] = $rootScope.idItemMod;
				envCal["calificacion"] = $scope.numCal;
				envCal["comentario"] = $scope.comentarios;

				$http.post("http://localhost:8181/orden/calificar/",envCal, {
						withCredentials: true,
						headers: {token: sessionStorage.token}
					}).success(function(res){
						$rootScope.datos=res
						$modalInstance.close();

						$http.get("http://localhost:8181/orden/detalle/?id="+$rootScope.idOrden)
							.success(function(res){
								$rootScope.productos = [];
								$rootScope.productos=res;
								console.log(res);
							}).error(function(res){
						});
						
					}).error(function(res){
					console.log("Doesn't work");
					console.log("Que trae esto: "+res);
				});
			}
		};

		$scope.cancel = function () {
			$modalInstance.dismiss('cancel');
		};
})
