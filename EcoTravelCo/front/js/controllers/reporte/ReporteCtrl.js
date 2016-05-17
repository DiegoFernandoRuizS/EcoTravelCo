'use strict';

/**
 * @ngdoc function
 * @name sistemagestionuniversitario.controller:PersonaListaCtrl
 * @description
 * # canastaCtrl
 * Controller of the Canasta
 */
angular.module('materialAdmin')
	.controller('ReporteCtrl', function ($scope, $rootScope, $http, $location, growlService) {

		$scope.format = 'yyyy-MM-dd';
		$scope.pdfUrl = 'https://s3-us-west-2.amazonaws.com/s.cdpn.io/149125/relativity.pdf';
		$scope.today = function() {
			$scope.dt = new Date();
		};
		
		$scope.today();

		$scope.open = function($event, opened) {
			$event.preventDefault();
			$event.stopPropagation();

			$scope[opened] = true;
		};

		$scope.dateOptions = {
			formatYear: 'yy',
			startingDay: 1
		};

		$scope.generarReporte = function(tipo) {
			var error = false;
			if($scope.dtPopup == undefined){
				growlService.growl('La Fecha Inicial es obligatoria.', 'danger');
				error = true;
			}
			if($scope.dtPopup2 == undefined){
				growlService.growl('La Fecha Final es obligatoria.', 'danger');
				error = true;
			}
			if(!error && $scope.dtPopup > $scope.dtPopup2 ){
				growlService.growl('La Fecha Inicial no puede ser mayor a la Fecha Inicial.', 'danger');
				error = true;
			}

			if(!error){
				var fechaInicial = $scope.dtPopup.toISOString().slice(0, 10);
				var fechaFinal = $scope.dtPopup2.toISOString().slice(0, 10);
				$scope.infoReporte = {
					tipo : tipo,
					fecha_inicial: fechaInicial,
					fecha_final: fechaFinal
				};

				$scope.sesion = {withCredentials: true,	headers: {token: sessionStorage.token}};

				$http.post("http://localhost:8181/reporte/", $scope.infoReporte, $scope.sesion
				).success(function (res) {
					if(res.error){
						growlService.growl('Error al generar el reporte.', 'danger');
					}else{
						$scope.urlReporte = "/reports/"+res.ruta;
						growlService.growl('Reporte generado en la rura .'+$scope.urlReporte, 'success');
						window.open($scope.urlReporte);
					}
				}).error(function (res) {
					console.log("Doesn't work");
					console.log("Que trae esto: " + res);
					growlService.growl('Error al generar el reporte.', 'danger');
				});
			}
			
		};

		$scope.downloadFile = function(downloadPath) {
			var ruta = $scope.urlReporte.replace(/\\/g, "/");
			window.open("file:///" + ruta);
		}
	});