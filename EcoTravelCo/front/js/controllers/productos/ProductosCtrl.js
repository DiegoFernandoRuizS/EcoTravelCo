'use strict';

angular.module('materialAdmin')
    .controller('ProductosCtrl', function ($scope, $rootScope, $http, $location) {

        $scope.datos = [];
        $scope.data = {};
        $rootScope.load = 0;

        $scope.consultarProductos = function () {
            $http.get("http://localhost:8181/producto/", {
                withCredentials: true,
                headers: {token: sessionStorage.token}
            }).success(function (res) {
                console.log("Consultando productos...");
                $scope.datos = res

                console.log("La respuesta en consultar: " + res);
            }).error(function (res) {
                console.log("Doesn't work");
                console.log("Que trae esto: " + res);
            });

            console.log("Antes de insertar");
            console.log(sessionStorage.token);
        };

        $scope.insertarProducto = function () {

            console.log("Insertar producto en el controlador ");

            var i = document.getElementById('imagen1').files[0];

            var reader = new FileReader();

            var imagenBytes=i.result;
            console.log($scope.producto);
            $scope.producto.imagen=imagenBytes;

            console.log("En la funcion insertar del controlador de insertar");
            console.log(sessionStorage.token);

            $http.post("http://localhost:8181/producto/",$scope.producto, {withCredentials: true,headers: {token:sessionStorage.token }})
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

            $http.delete("http://localhost:8181/producto/" + id, $scope.productoDelete, {withCredentials: true,headers: {token:sessionStorage.token }})
                .success(function (res) {
                    $scope.borrarProducto = {};

                    console.log("La respuesta del backend " + res);
                }).error(function (res) {
                console.log("Doesn't work para Borrar producto");
                console.log("El error para borar producto: " + res);
            });
        };

        $scope.queryAllProduct = function () {
            //var id = pathArray[pathArray.length-2];
            $http({method: 'GET', url: 'http://localhost:8181/producto_home/'})
                .success(function(res){
                    $scope.datos=res
                    $rootScope.load =1;
                    console.log(res);

                }).error(function(res){
                console.log("Doesn't work");
                console.log("Que trae esto: "+res);
            })
        };


        $scope.queryProducts = function () {
            $rootScope.busquedaProd=$scope.producto.search;
        };

        if ( $rootScope.load ==0) {
            $scope.queryAllProduct();
        }

        $scope.detailProd = function (id) {
            $rootScope.prodId= id;
        };

    });



angular.module('materialAdmin')
    .controller('ProductosDetalle', function ($scope, $rootScope, $http, $location) {

        $scope.datos = [];
        $scope.data = {};

        $scope.comentario = [];
        $scope.comentarios = {};

        $scope.detailProd = function () {

            var id=  $rootScope.prodId;

            console.log("Entro?"+id);
            $http({method: 'GET', url: 'http://localhost:8181/producto_detalle/' + id})
                .success(function(res){
                    $scope.datos=res
                    console.log(res);

                }).error(function(res){
                console.log("Doesn't work");
                console.log("Que trae esto: "+res);
            })
        };


        $scope.detailProd();

    });



angular.module('materialAdmin')
    .controller('ProductosBusqueda', function ($scope, $rootScope, $http, $location) {

        $scope.datos = [];
        $scope.data = {};

        $scope.comentario = [];
        $scope.comentarios = {};

        $scope.queryProducts = function () {


            var criterios=  $rootScope.busquedaProd;
            $http({method: 'GET', url: 'http://localhost:8181/producto_busqueda/' + criterios})
                .success(function(res){
                    $scope.datos=res
                    console.log(res);

                }).error(function(res){
                console.log("Doesn't work");
                console.log("Que trae esto: "+res);
            })
        };

        $scope.queryProducts();

    });
