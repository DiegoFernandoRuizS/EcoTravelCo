'use strict';

angular.module('materialAdmin')
        .controller('ProductosCtrl', function ($scope, $rootScope, $http, $location) {

            $scope.datos = [];
            $scope.data = {};



            $http.get("http://localhost:8181/producto/").success(function (res) {
                console.log("Consultando productos...");
                $scope.datos = res

                console.log("La respuesta en consultar: " + res);
            }).error(function (res) {
                console.log("Doesn't work");
                console.log("Que trae esto: " + res);
            });

            $scope.insertarProducto = function () {

                console.log("Insertar producto en el controlador ");

                var i = document.getElementById('imagen1').files[0];
               console.log("Imagen");
               console.log(i);
               console.log("----->");
              // console.log(i.result);

                var reader = new FileReader();

                console.log("readAsDataURL");
                console.log(reader.readAsDataURL(i));

                   console.log("reader");
                   console.log(reader);

                  console.log("readerResul");

                   console.log(reader);
                  console.log(reader.result);

                 var imagenBytes=i.result;

                 //.attr('ng-model', attributes.forModel);

               // $scope.producto[0].add('imagen',"Holaaaaa");
               // $scope.producto[0].push('imagen',imagenBytes);
console.log($scope.producto);
              // $scope.producto.imagen={'imagen' : ""+imagenBytes};
$scope.producto.imagen=imagenBytes;

                $http.post("http://localhost:8181/producto/", $scope.producto, {})
                        .success(function (res) {
                            $scope.insertarProducto = {};

                            console.log("La respuesta del backend " + res);
                        }).error(function (res) {
                    console.log("Doesn't work para insertar producto");
                    console.log("El error para insertar producto: " + res);
                });
            };

            $scope.borrarProducto = function (id) {

                console.log("Borrar producto en el controlador " + id);

                $http.delete("http://localhost:8181/producto/" + id, $scope.productoDelete, {})
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
            $http.get("http://localhost:8181/producto_home/")

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


