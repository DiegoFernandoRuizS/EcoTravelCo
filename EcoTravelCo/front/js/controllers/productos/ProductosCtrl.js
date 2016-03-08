'use strict';

/**
 * @ngdoc function
 * @name sistemagestionuniversitario.controller:PersonaListaCtrl
 * @description
 * # PersonaListaCtrl
 * Controller of the PersonaLista
 */
angular.module('materialAdmin')
  .controller('ProductosCtrl', function ($scope, $rootScope, $http, $location) {
	  
      	$scope.datos = [];
      	$scope.data = {};	    
	    
	    console.log("Consultando productos...");
	    $http.get("http://localhost:8181/producto/")
	    
	    .success(function(res){
        $scope.datos=res
	    console.log(res);
	    
	    }).error(function(res){	  	  
	        console.log("Doesn't work");
	        console.log("Que trae esto: "+res);

	    });
	    
    
    
  });

angular.module('materialAdmin')
	.controller('ProductosHomeCtrl', function ($scope, $rootScope, $http, $location) {

		$scope.datos = [];
		$scope.data = {};

		console.log("Consultando productos...");
		$http.get("http://localhost:8181/producto_home/")

			.success(function(res){
				$scope.datos=res
				console.log(res);

			}).error(function(res){
			console.log("Doesn't work");
			console.log("Que trae esto: "+res);

		});



	});

angular.module('materialAdmin')
    .controller('ProductosDetalle', function ($scope, $rootScope, $http, $location) {

        $scope.datos = [];
        $scope.data = {};

        console.log("Consultando productos 22222...");
        $http.get("http://localhost:8181/producto_detalle/1")

            .success(function(res){
                $scope.datos=res
                console.log(res);

            }).error(function(res){
            console.log("Doesn't work");
            console.log("Que trae esto: "+res);

        });



    });


