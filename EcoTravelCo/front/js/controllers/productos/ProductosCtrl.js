'use strict';

angular.module('materialAdmin')
        .controller('ProductosCtrl', function ($scope, $rootScope, $http, $location) {

            $scope.datos = [];
            $scope.data = {};
            $rootScope.load = 0;
            $scope.listaValores = [];

            $scope.queryAllProduct = function () {
                //var id = pathArray[pathArray.length-2];
                $http({method: 'GET', url: 'http://localhost:8181/producto_home/'})
                        .success(function (res) {
                            $scope.datos = res
                            $rootScope.load = 1;
                        }).error(function (res) {
                    console.log("Doesn't work");
                    console.log("Que trae esto: " + res);
                })
            };


            $scope.queryProducts = function () {
                $rootScope.busquedaProd = $scope.producto.search;
            };

            if ($rootScope.load == 0) {
                $scope.queryAllProduct();
            }

            $scope.detailProd = function (id) {
                $rootScope.prodId = id;
            };


        });



angular.module('materialAdmin')
        .controller('ProductosDetalle', function ($scope, $rootScope, $http, $location) {

            $scope.datos = [];
            $scope.data = {};

            $scope.comentario = [];
            $scope.comentarios = {};

            $scope.detailProd = function () {

                var id = $rootScope.prodId;

                console.log("Entro?" + id);
                $http({method: 'GET', url: 'http://localhost:8181/producto_detalle/' + id})
                        .success(function (res) {
                            $scope.datos = res
                            console.log(res);

                        }).error(function (res) {
                    console.log("Doesn't work");
                    console.log("Que trae esto: " + res);
                })
            };


            $scope.detailProd();

        });



angular.module('materialAdmin')

    .controller('ProductosBusqueda', function ($scope, $rootScope, $http, $location, $filter, $sce, ngTableParams, tableService) {

            $scope.datos = [];
            $scope.data = {};

            $scope.comentario = [];
            $scope.comentarios = {};

            $scope.queryProducts = function () {

                var criterios = $rootScope.busquedaProd;

            var criterios=  $rootScope.busquedaProd;
            $http({method: 'GET', url: 'http://localhost:8181/producto_busqueda/' + criterios})
                .success(function(res){
                    $scope.datos=res;

                   $scope.tableBasic = new ngTableParams(
                        {page: 1, count: 5},
                        {
                            total: res.length,
                            getData: function ($defer, params) {

                                $defer.resolve(res.slice((params.page() - 1) * params.count(), params.page() * params.count()));

                            }
                        }
                    );

                    console.log(res);
                        }).error(function (res) {
                    console.log("Doesn't work");
                    console.log("Que trae esto: " + res);
                })
            };


        $scope.detailProd = function (id) {
            $rootScope.prodId= id;
        };

        $scope.queryProducts();

        });
