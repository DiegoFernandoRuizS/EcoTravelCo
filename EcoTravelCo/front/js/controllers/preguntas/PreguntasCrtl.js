/**
 * Created by Jorge on 27/03/2016.
 */

'use strict';



angular.module('materialAdmin')

    .controller('MisPreguntasCtrl', function ($scope, $rootScope, $http, $location, $filter, $sce, ngTableParams, tableService) {

        $scope.datos = [];
        $scope.data = {};

        $scope.queryProducts = function () {

            var criterios = $rootScope.busquedaProd;

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



angular.module('materialAdmin')

    .controller('ResPreguntasCtrl', function ($scope, $rootScope, $http, $location, $filter, $sce, ngTableParams, tableService) {

        $scope.datos = [];
        $scope.data = {};


        $scope.queryProducts = function () {

            var criterios = $rootScope.busquedaProd;

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

