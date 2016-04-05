'use strict';


angular.module('materialAdmin')
  .controller('ProveedoresCtrl', function ($scope, $rootScope, $http, $location) {
	  
      	$scope.datos = [];
      	$scope.data = {};	    

 $http.get("http://localhost:8181/usuario/proveedores").success(function(res){
            console.log("Consultando proveedores...");
                $scope.datos=res

        	    console.log("La respuesta en consultar proveedores : "+res);
        	    }).error(function(res){
        	        console.log("Doesn't work");
        	        console.log("Que trae esto: "+res);
        	    });



        $scope.borrarProducto = function (id) {

             console.log("Borrar producto en el controlador "+id);

            $http.delete("http://localhost:8181/producto/"+id,$scope.productoDelete,{})
                                    .success(function(res){
                                    $scope.borrarProducto = {};

                                            console.log("La respuesta del backend "+res);
                                    }).error(function(res){
                                        console.log("Doesn't work para Borrar producto");
                                        console.log("El error para borar producto: "+res);
                                    });
                                    };

  });



angular.module('materialAdmin')
  .controller('CrearProveedorCtrl', function ($scope, $rootScope, $http, $location,growlService) {

      	$scope.data = {};

	     $scope.registrar = function () {
	     console.log("Insertar producto en el controlador");


	     $http.post("http://localhost:8181/usuario/proveedor",$scope.data,{withCredentials: true,headers: {token:sessionStorage.token }})
        	    .success(function(res){
              	        $scope.data = {};
              		    console.log("La respuesta del backend "+res);
              		    growlService.growl('Se registró correctamente el proveedor.', 'inverse');
        	    }).error(function(res){
        	        console.log("Doesn't work para insertar producto");
        	        console.log("El error para insertar producto: "+res);
        	        growlService.growl(' Ocurrió un error registrando el proveedor.', 'inverse');

        	    });
        		};



  });

  angular.module('materialAdmin')
    .controller('EditarProveedorCtrl', function ($scope, $rootScope, $http, $location,growlService) {

        	$scope.data = {};



 $http.get("http://localhost:8181/usuario/proveedores").success(function(res){
            console.log("Consultando proveedores...");
                $scope.datos=res

        	    console.log("La respuesta en consultar proveedores : "+res);
        	    }).error(function(res){
        	        console.log("Doesn't work");
        	        console.log("Que trae esto: "+res);
        	    });


  	     $scope.registrar = function () {

  	     $http.put("http://localhost:8181/usuario/proveedor",$scope.data,{withCredentials: true,headers: {token:sessionStorage.token }})
          	    .success(function(res){
                	        $scope.data = {};
                		    console.log("La respuesta del backend "+res);
                		    growlService.growl('Se registró correctamente el proveedor.', 'inverse');
          	    }).error(function(res){
          	        console.log("Doesn't work para insertar producto");
          	        console.log("El error para insertar producto: "+res);
          	        growlService.growl(' Ocurrió un error registrando el proveedor.', 'inverse');

          	    });
          		};



    });