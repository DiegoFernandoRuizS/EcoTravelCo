'use strict';

angular.module('materialAdmin')
        .controller('ProductosCtrl', function ($scope, $rootScope, $http, $location) {

            $scope.datos = [];
            $scope.data = {};

            $http.get("http://localhost:8181/producto/",{withCredentials: true,headers: {token:sessionStorage.token }}).success(function (res) {
                console.log("Consultando productos...");
                $scope.datos = res

                console.log("La respuesta en consultar: " + res);
            }).error(function (res) {
                console.log("Doesn't work");
                console.log("Que trae esto: " + res);
            });

		console.log("Antes de insertar");
		console.log(sessionStorage.token);


            $scope.insertarProducto = function () {

                console.log("Insertar producto en el controlador ");

                var i = document.getElementById('imagen1').files[0];

                var reader = new FileReader();

                 var imagenBytes=i.result;
                 console.log($scope.producto);
                 $scope.producto.imagen=imagenBytes;

                 console.log("En la funcion insertar del controlador de insertar");
                 console.log(sessionStorage.token);

                $http.post("http://localhost:8181/producto/", {withCredentials: true,headers: {token:sessionStorage.token }},$scope.producto, {})
                        .success(function (res) {
                            $scope.insertarProducto = {};
                            console.log("La respuesta del backend " + res);
                            console.log("insetar en el token");
                            console.log(sessionStorage.token);
                        }).error(function (res) {
                    console.log("Doesn't work para insertar producto");
                    console.log("El error para insertar producto: " + res);
                });
            };

            $scope.borrarProducto = function (id) {

                console.log("Borrar producto en el controlador " + id);

                $http.delete("http://localhost:8181/producto/" + id,{withCredentials: true,headers: {token:sessionStorage.token }}, $scope.productoDelete, {})
                        .success(function (res) {
                            $scope.borrarProducto = {};

                            console.log("La respuesta del backend " + res);
                        }).error(function (res) {
                    console.log("Doesn't work para Borrar producto");
                    console.log("El error para borar producto: " + res);
                });
            };

        });


angular.module('materialAdmin')
        .controller('ProductosHomeCtrl', function ($scope, $rootScope, $http, $location) {

            $scope.datos = [];
            $scope.data = {};

		console.log("Consultando productos...");
		console.log(sessionStorage.token);

		$http.get("http://localhost:8181/producto_home/",{withCredentials: true,headers: {token:sessionStorage.token }})

                    .success(function (res) {
                        $scope.datos = res
                        console.log(res);

                    }).error(function (res) {
                console.log("Doesn't work");
                console.log("Que trae esto: " + res);

            });

        });

angular.module('materialAdmin')
        .controller('ProductosDetalle', function ($scope, $rootScope, $http, $location) {

            $scope.datos = [];
            $scope.data = {};

            console.log("Consultando productos 22222...");
            $http.get("http://localhost:8181/producto_detalle/1")

                    .success(function (res) {
                        $scope.datos = res
                        console.log(res);

                    }).error(function (res) {
                console.log("Doesn't work");
                console.log("Que trae esto: " + res);

            });
        });


