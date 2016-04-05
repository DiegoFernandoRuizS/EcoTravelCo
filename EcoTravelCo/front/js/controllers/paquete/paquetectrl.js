'use strict';

angular.module('materialAdmin')
        .controller('paquetectrl', function ($scope, $rootScope, $http, $location, $window, growlService) {

            $scope.paquetes = [];
            //Datos del paquete a crear
            $scope.paquete = {};
            $scope.datos = [];
            var cuantos = 0;
            $rootScope.total=0;
            $rootScope.mostrar = false;
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
                $rootScope.mostrar = true;
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
                         $rootScope.total=$rootScope.total+$scope.datos[i].precio;
                    }
                }
                console.log($rootScope.agregado);
            };

            $scope.quitarProducto = function (productoId) {
                for (var i = 0; i < $rootScope.agregado.length; i++) {
                    if ($rootScope.agregado[i].id === productoId) {
                        $rootScope.agregado.splice(i, 1);
                        $rootScope.total=$rootScope.total-$scope.datos[i].precio;
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


            //para listar el paquete a editar
            $scope.listarPaquete = function (id){
                        $rootScope.hijosPaquete = [];
                        $rootScope.actualPaquete = [];
                        console.log("Listar paquete en el controlador " + id);
                        $http.get("http://localhost:8181/paquete/" + id)
                            .success(function (res) {
                                $http({method: 'GET', url: 'http://localhost:8181/paquete/hijos/' + id})
                                    .success(function (resHijos) {
                                     $rootScope.hijosPaquete = resHijos;
                                     console.log(resHijos);
                                     $rootScope.agregado = $rootScope.hijosPaquete[0];
                                     
                                     console.log();
                                    }).error(function (resHijos) {
                                    console.log("Doesn't work");
                                    console.log("Que trae esto: " + resHijos);
                                })
                                $rootScope.actualPaquete = res[0];
                                console.log($rootScope.actualPaquete);
                                $rootScope.actualPaquete.cantidad = "" + $rootScope.actualPaquete.cantidad_actual;
                                $rootScope.actualPaquete.precio = "" + $rootScope.actualPaquete.precio;

                            }).error(function (res) {
                            console.log("Doesn't work para listar producto");
                            console.log("El error para borar producto: " + res);
                        });
                    };

             //para actualizr el paquete en la gestion
             $scope.actualizarProducto = function (id) {
                        console.log("Que envio? ");
                        console.log(id);
                        console.log($scope.actualProducto);
                        $http.put("http://localhost:8181/producto/" + id, $scope.actualProducto, {
                                withCredentials: true,
                                headers: {token: sessionStorage.token}
                            })
                            .success(function (res) {
                                growlService.growl('Se actualizó correctamente la información.', 'inverse');
                                console.log("La respuesta del backend " + res);
                                $scope.actualProducto = {};
                                $window.location.href = '/#/productos/productos';
                                $scope.consultarProductos();

                            }).error(function (res) {
                            growlService.growl(' Ocurrió un error actualizando la información.', 'inverse');
                            console.log("Doesn't work para actualizar producto");
                            console.log("El error para actualizar producto: " + res);
                        });

                        //Insertamos las nuevas imagenes
                        var idProd = id;
                        var principal = 0;
                        for(var i = 0; i < $rootScope.imagenesCargadas.length; i++){
                            if($rootScope.imagenesCargadas[i]["id"] == 0){
                                var datosImagen = {
                                    "imagen": $rootScope.imagenesCargadas[i]["url"],
                                    "id_producto":idProd,
                                    "principal": principal,
                                }

                                $http.post("http://localhost:8181/galeria/", datosImagen)
                                    .success(function (res) {
                                        console.log("El Imagen guardada: " + res);
                                    }).error(function (res) {
                                    console.log("Doesn't work para Borrar producto");
                                    console.log("El error para borar producto: " + res);
                                });
                            }
                        }

                        //Eliminamos las imagenes
                        for(var i = 0; i < $rootScope.imagenesEliminar.length; i++){
                            $http.delete("http://localhost:8181/galeria/"+$rootScope.imagenesEliminar[i]['id'], datosImagen)
                                .success(function (res) {
                                    console.log("El Eliminada guardada: " + res);
                                }).error(function (res) {
                                console.log("Doesn't work para Borrar producto");
                                console.log("El error para borar producto: " + res);
                            });
                        }

                    };


        });
