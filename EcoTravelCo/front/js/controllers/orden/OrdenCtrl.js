'use strict';

/**
 * @ngdoc function
 * @name sistemagestionuniversitario.controller:PersonaListaCtrl
 * @description
 * # canastaCtrl
 * Controller of the Canasta
 */
angular.module('materialAdmin')
        .controller('OrdenCtrl', function ($scope, $rootScope, $http, $location, $uibModal, ngTableParams, tableService) {

            $scope.ordenes = [];
            $scope.orden = {};
            $scope.data = {};
            $rootScope.productos;

            $http.get("http://localhost:8181/orden/", {
                withCredentials: true,
                headers: {token: sessionStorage.token}
            }).success(function (res) {
                $scope.ordenes = res;
                console.log($scope.datos)

                $scope.tableOrden = new ngTableParams(
                        {page: 1, count: 10},
                        {
                            total: res.length,
                            getData: function ($defer, params) {

                                $defer.resolve(res.slice((params.page() - 1) * params.count(), params.page() * params.count()));

                            }
                        }
                );

            }).error(function (res) {
                console.log("Doesn't work");
                console.log("Que trae esto: " + res);
            });

            if ($rootScope.idOrdenCanasta != undefined && $rootScope.idOrdenCanasta != 0) {
                $rootScope.idOrden = $rootScope.idOrdenCanasta;
                $http.get("http://localhost:8181/orden/detalle/?id=" + $rootScope.idOrden)
                        .success(function (res) {
                            $rootScope.productos = [];
                            $rootScope.productos = res;
                            console.log(res);

                        }).error(function (res) {
                });
            }

            $scope.detalleOrden = function (idOrden) {
                $rootScope.idOrdenCanasta = 0;
                $rootScope.idOrden = idOrden;
                $http.get("http://localhost:8181/orden/detalle/?id=" + idOrden)
                        .success(function (res) {
                            $rootScope.productos = [];
                            $rootScope.productos = res;
                            console.log(res);

                        }).error(function (res) {
                });
            }

            $scope.cancelarOrden = function (idOrden) {
                $rootScope.idOrden = idOrden;
                $http.delete("http://localhost:8181/orden/?id=" + idOrden)
                        .success(function (res) {
                            var cancelar = res;
                            $http.get("http://localhost:8181/orden/", {
                                withCredentials: true,
                                headers: {token: sessionStorage.token}
                            }).success(function (res) {
                                $scope.ordenes = res;
                                console.log($scope.datos)
                            }).error(function (res) {
                                console.log("Doesn't work");
                                console.log("Que trae esto: " + res);
                            });
                        }).error(function (res) {
                });
            }

            //Create Modal
            function modalInstances() {
                var modalInstance = $uibModal.open({
                    animation: true,
                    templateUrl: 'myModalContent.html',
                    controller: 'ModalCtrl',
                    size: "sm",
                    backdrop: true,
                    keyboard: true,
                    resolve: {
                        content: function () {
                            return $rootScope.idOrden;
                        }
                    }
                });
            }

            $scope.open = function (url, nombre, id_producto, id_item) {
                $rootScope.urlMod = url;
                $rootScope.nombreMod = nombre;
                $rootScope.idProductoMod = id_producto;
                $rootScope.idItemMod = id_item;
                modalInstances();
            }

            $scope.pagar = function () {
                $rootScope.idOrdenPagar = $rootScope.idOrden;
                modalInstancesPagar();
            }

            //Create Modal
            function modalInstancesPagar() {
                var modalInstance = $uibModal.open({
                    animation: true,
                    templateUrl: 'myModalPagar.html',
                    controller: 'ModalCtrlPagar',
                    size: "sm",
                    backdrop: true,
                    keyboard: true,
                    resolve: {
                        content: function () {
                            return $rootScope.idOrden;
                        }
                    }
                });
            }

            $scope.detailProd = function (id) {
                $rootScope.prodId = id;
            };

        })


angular.module('materialAdmin')
        .controller('ModalCtrl', function ($scope, $rootScope, $modalInstance, $http, $uibModal) {
            $scope.cal = ["", "", "", "", ""];
            $scope.numCal = 0;
            $scope.errorModal = "";

            //variables para compartir en fb: se definen rootScope porque son usadas por otro controlador
            $rootScope.comment = "";
            $rootScope.note = "";
            $rootScope.onfb=false;

            (function () {
                console.log("Se ejecutto anónima");

                FB.init({
                    appId: '985703204846099',
                    xfbml: true,
                    version: 'v2.6'
                });
                FB.getLoginStatus(function (response) {
                    if (response.status === 'connected') {
                        // $scope.publish();
                        console.log("Conectado a fb");
                        $rootScope.onfb=true;
                    } else if (response.status === 'not_authorized') {
                        console.log("No iniciado en fb");
                    } else {
                        console.log("No Conectado a fb");
                    }
                });

            })();

            $scope.calificar = function (valor) {
                $scope.numCal = valor;
                for (var i = 0; i < 5; i++) {
                    if (i < valor) {
                        $scope.cal[i] = "active";
                    } else {
                        $scope.cal[i] = "";
                    }
                }
            };

            $scope.ok = function () {
                $scope.comentarios = document.getElementById('comentario').value;
                if ($scope.numCal == 0) {
                    $scope.errorModal = "La calificación es obligatoria.";
                } else if ($scope.comentarios == "") {
                    $scope.errorModal = "Los comentarios son obligatorios.";
                } else {
                    $scope.errorModal = "";
                }

                if ($scope.errorModal == "") {

                    var envCal = {};
                    envCal["id_producto"] = $rootScope.idProductoMod;
                    envCal["id_item"] = $rootScope.idItemMod;
                    envCal["calificacion"] = $scope.numCal;
                    envCal["comentario"] = $scope.comentarios;

                    $http.post("http://localhost:8181/calificacion/", envCal, {
                        withCredentials: true,
                        headers: {token: sessionStorage.token}
                    }).success(function (res) {
                        $rootScope.datos = res
                        //publica ok
                        if($rootScope.onfb){
                         $rootScope.comment = $scope.comentarios;
                                                $rootScope.note = $scope.numCal;
                                                modalInstancesSharefb();
                        }




                        $modalInstance.close();

                        $http.get("http://localhost:8181/orden/detalle/?id=" + $rootScope.idOrden)
                                .success(function (res) {
                                    $rootScope.productos = [];
                                    $rootScope.productos = res;
                                    console.log(res);
                                }).error(function (res) {
                        });

                    }).error(function (res) {
                        console.log("Doesn't work");
                        console.log("Que trae esto: " + res);
                    });
                }
            };

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };


            //Create Modal share fb
            function modalInstancesSharefb() {
                var modalInstance = $uibModal.open({
                    animation: true,
                    templateUrl: 'myModalSharefb.html',
                    controller: 'ModalCtrlSharefb',
                    size: "sm",
                    backdrop: true,
                    keyboard: true,
                    resolve: {
                        content: function () {
                            return $rootScope.idOrden;
                        }
                    }
                });
            }
        })

angular.module('materialAdmin')
        .controller('ModalCtrlPagar', function ($scope, $rootScope, $modalInstance, $http) {

            $scope.pagarOrden = function () {
                $http.put("http://localhost:8181/orden/?id=" + $rootScope.idOrdenPagar)
                        .success(function (res) {
                            var cancelar = res;
                            $http.get("http://localhost:8181/orden/", {
                                withCredentials: true,
                                headers: {token: sessionStorage.token}
                            }).success(function (res) {
                                $scope.ordenes = res;
                                console.log($scope.datos)
                            }).error(function (res) {
                                console.log("Doesn't work");
                                console.log("Que trae esto: " + res);
                            });
                        }).error(function (res) {
                });

                $modalInstance.dismiss('cancel');
            }

            $scope.cancel = function () {
                $modalInstance.dismiss('cancel');
            };
        })

angular.module('materialAdmin')
        .controller('ModalCtrlSharefb', function ($scope, $rootScope, $modalInstance, $http) {

            $scope.sharefb = function () {
                console.log("Publicado ");
                $scope.publish();
                $modalInstance.dismiss('cancel');
            }

            $scope.cancelsharefb = function () {
                $modalInstance.dismiss('cancel');
            };

            $scope.publish = function () {
                console.log("Publish antes " +$rootScope.comment + " --- " + $rootScope.note);
                var mensajePublicar = 'He calificado a este producto con ' + $rootScope.note + ' puntos. Mi comentario: ' + $rootScope.comment;
                FB.api('/me/feed', 'post', {link: 'https://www.facebook.com/EcoTravelColombiaPlus',
                    message: mensajePublicar
                }, function (response) {
                    console.log("Publish ok " + response.id);
                });
            };
        })

