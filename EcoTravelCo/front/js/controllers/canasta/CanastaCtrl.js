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
	  
      	$scope.datos = [];
      	$scope.data = {};	    
	    
	    console.log("Consultando canasta...");
	    $http.get("http://localhost:8181/canasta/")
	    
	    .success(function(res){
        $scope.datos=res
	    console.log(res);
	    
	    }).error(function(res){	  	  
	        console.log("Doesn't work");
	        console.log("Que trae esto: "+res);

	    });
  });
  
  
  
  
