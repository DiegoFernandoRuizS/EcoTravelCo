'use strict';

angular.module('materialAdmin')
        .controller('paquetectrl', function ($scope, $rootScope, $http, $location, $window, growlService) {

            $scope.paquetes = [];
            //Datos del paquete a crear
            $scope.paquete = {};
            $scope.datos = [];
            var cuantos = 0;
            this.mostrar = true;
            $rootScope.agregado = [];
            console.log("Entro al controlador de paquetes");
            //Insertando el paquete
            $scope.insertarPaquete = function () {
                console.log("inserto paquete");
                //Agregar productos asociados al paquete
                $scope.paquete.cuantos = cuantos;
                $scope.paquete.productos = $rootScope.agregado;
                console.log($scope.paquete);

                $http.post("http://localhost:8181/paquete/", $scope.paquete, {withCredentials: true, headers: {token: sessionStorage.token}})
                        .success(function (res) {
                            growlService.growl('Se guardo correctamente la información.', 'inverse');
                            $scope.paquete = {};
                            console.log("La respuesta del backend " + res);
                            $window.location.href = '/#/paquetes/paquetes';
                            // $scope.consultarProductos();
                        }).error(function (res) {
                    growlService.growl(' Ocurrió un error guardando la información.', 'inverse');
                    console.log("Doesn't work para insertar paquete");
                    console.log("El error para insertar paquete: " + res);
                });
            };
            $scope.listar = function () {
                $http.get("http://localhost:8181/paquete", {
                    withCredentials: true,
                    headers: {token: sessionStorage.token}
                }).success(function (res) {
                    $scope.paquetes = res;
                    $scope.cantidadPaquetes=0;
                    $scope.cantidadPaquetes=$scope.paquetes.length;
                }).error(function (res) {
                    console.log("Doesn't work");
                    console.log("Que trae esto paquetes " + res);
                });
            };
            $scope.listar();

            //para consultar los productos en la gestion de productos
            $scope.consultarProductos = function () {
                this.mostrar = true;
                console.log(this.mostrar);
                $rootScope.cantidadProductos = {};
                $http.get("http://localhost:8181/producto/", {
                    withCredentials: true,
                    headers: {token: sessionStorage.token}
                }).success(function (res) {
                    console.log("Consultando productos...");
                    $scope.datos = res;
                    $rootScope.cantidadProductos = res;
                    var productosTotal = Object.keys($rootScope.cantidadProductos).length;
                    $rootScope.cantidadProductos = productosTotal;
                }).error(function (res) {
                    growlService.growl(' Ocurrió un error consultando la información.', 'inverse');
                    console.log("Doesn't work");
                    console.log("Que trae esto: " + res);
                });
            };

            $scope.agregarProductos = function (productoId) {
                for (var i = 0; i < $scope.datos.length; i++) {
                    if ($scope.datos[i].id === productoId) {
                        $rootScope.agregado.push($scope.datos[i]);
                    }
                }
                console.log($rootScope.agregado);
            };

            $scope.quitarProducto = function (productoId) {
                for (var i = 0; i < $rootScope.agregado.length; i++) {
                    if ($rootScope.agregado[i].id === productoId) {
                        $rootScope.agregado.splice(i, 1);
                    }
                }

                console.log($rootScope.agregado);
            };

            //borrar paquete
            $scope.borrarPaquete = function (id) {
                $http.delete("http://localhost:8181/paquete/" + id, $scope.productoDelete, {withCredentials: true, headers: {token: sessionStorage.token}})
                        .success(function (res) {
                            $scope.listar();
                            growlService.growl('Se borró correctamente la información.', 'inverse');
                        }).error(function (res) {
                    growlService.growl(' Ocurrió un error borrando la información.', 'inverse');
                    console.log("Doesn't work para Borrar paquete");
                    console.log("El error para borar producto: " + res);
                });
            };
        });
