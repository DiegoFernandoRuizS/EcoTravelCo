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
    .controller('ProductosCtrlProveedor', function ($scope, $rootScope, $http, $location, $window,growlService) {
        $scope.datos = [];
        $scope.listaValores = [];
        $scope.listaTipo = [];

        //para cargar el combox box de paises
        $scope.combox = function () {
            $http.get("http://localhost:8181/datos", {
                withCredentials: true,
                headers: {token: sessionStorage.token}
            }).success(function (res) {
                $scope.listaValores = res
            }).error(function (res) {
                console.log("Doesn't work");
                console.log("Que trae esto paises " + res);
            });
        };
        //para cargar el combox box de tipoproducto
        $scope.comboxTipo = function () {
            $http.get("http://localhost:8181/datos/tipo", {
                withCredentials: true,
                headers: {token: sessionStorage.token}
            }).success(function (res) {
                $scope.listaTipo = res
            }).error(function (res) {
                console.log("Doesn't work");
                console.log("Que trae esto paises " + res);
            });
        };
        //para consultar los productos en la gestion de productos
        $scope.consultarProductos = function () {
            $rootScope.cantidadProductos={};
            $http.get("http://localhost:8181/producto/", {
                withCredentials: true,
                headers: {token: sessionStorage.token}
            }).success(function (res) {
                console.log("Consultando productos...");
                $scope.datos = res;
                $rootScope.cantidadProductos=res;
                var productosTotal = Object.keys($rootScope.cantidadProductos).length;
                $rootScope.cantidadProductos=productosTotal;
                //    console.log(productosTotal);
                //  console.log("La respuesta en consultar: " + res);
            }).error(function (res) {
                growlService.growl(' Ocurrió un error consultando la información.', 'inverse');
                console.log("Doesn't work");
                console.log("Que trae esto: " + res);
            });
        };
        //para insertar los productos
        $scope.insertarProducto = function () {
            var i = document.getElementById('imagen').files[0];
            var i2 = document.getElementById('imagen2').files[0];
            var i3 = document.getElementById('imagen3').files[0];

            var imagenBytes = i.result;
            var imagenBytes2 = i2.result;
            var imagenBytes3 = i3.result;
            $scope.producto.imagen = imagenBytes;
            $scope.producto.imagen2 = imagenBytes2;
            $scope.producto.imagen3 = imagenBytes3;
            console.log("Imagen1");
            console.log(imagenBytes);
            console.log("Imagen2");
            console.log(imagenBytes2);
            console.log("Imagen3");
            console.log(imagenBytes3);
            $http.post("http://localhost:8181/producto/", $scope.producto, {withCredentials: true, headers: {token: sessionStorage.token}})
                .success(function (res) {
                    growlService.growl('Se guardo correctamente la información.', 'inverse');
                    $scope.insertarProducto = {};
                    console.log("La respuesta del backend " + res);
                    $window.location.href = '/#/productos/productos';
                    $scope.consultarProductos();

                }).error(function (res) {
                growlService.growl(' Ocurrió un error guardando la información.', 'inverse');
                console.log("Doesn't work para insertar producto");
                console.log("El error para insertar producto: " + res);
            });
        };
        //para borrar los productos
        $scope.borrarProducto = function (id) {
            console.log("Borrar producto en el controlador " + id);
            $http.delete("http://localhost:8181/producto/" + id, $scope.productoDelete, {withCredentials: true, headers: {token: sessionStorage.token}})
                .success(function (res) {
                    $scope.borrarProducto = {};
                    $scope.consultarProductos();
                    //console.log("La respuesta del backend " + res);
                    growlService.growl('Se borró correctamente la información.', 'inverse');

                }).error(function (res) {
                growlService.growl(' Ocurrió un error borrando la información.', 'inverse');
                console.log("Doesn't work para Borrar producto");
                console.log("El error para borar producto: " + res);
            });
        };
        //para listar el producto a editar
        $scope.listarProducto = function (id) {
            $rootScope.productoEditar = [];
            $rootScope.actualProducto = {};
            console.log("Listar producto en el controlador " + id);
            $http.get("http://localhost:8181/producto/" + id, {withCredentials: true, headers: {token: sessionStorage.token}})
                .success(function (res) {
                    $rootScope.actualProducto = res;
                    console.log($rootScope.actualProducto);
                    $rootScope.actualProducto.cantidad = "" + $rootScope.actualProducto.cantidad;
                    $rootScope.actualProducto.latitud = "" + $rootScope.actualProducto.latitud;
                    $rootScope.actualProducto.longitud = "" + $rootScope.actualProducto.longitud;
                    $rootScope.actualProducto.precio = "" + $rootScope.actualProducto.precio;

                    console.log($rootScope.actualProducto);
                }).error(function (res) {
                console.log("Doesn't work para listar producto");
                console.log("El error para borar producto: " + res);
            });
        };
        //para actualizr el producto en la gestion
        $scope.actualizarProducto = function (id) {

            var i = document.getElementById('imagen1').files[0];
            var i2 = document.getElementById('imagen2').files[0];
            var i3 = document.getElementById('imagen3').files[0];
            console.log(i);
            if (i === undefined) {
            } else {
                var imagenBytes = i.result;
                $scope.actualProducto.imagen1 = imagenBytes;
                console.log("cambia 1 ");
                console.log($scope.actualProducto.imagen);
            }
            if (i2 === undefined) {
            } else {
                var imagenBytes2 = i2.result;
                $scope.actualProducto.imagen2 = imagenBytes2;
                console.log("cambia 2 ");
                console.log($scope.actualProducto.imagen1);
            }
            if (i3 === undefined) {
            } else {
                var imagenBytes3 = i3.result;
                $scope.actualProducto.imagen3 = imagenBytes3;
                console.log("cambia 3 ");
                console.log($scope.actualProducto.imagen2);
            }
            console.log("Actualizado....")
            console.log($scope.actualProducto);
            $http.put("http://localhost:8181/producto/" + id, $scope.actualProducto, {withCredentials: true, headers: {token: sessionStorage.token}})
                .success(function (res) {
                    growlService.growl('Se actualizó correctamente la información.', 'inverse');
                    console.log("Que tiene el combobox "+$scope.actualProducto.tipo_producto_id);
                    console.log("La respuesta del backend " + res);
                    $scope.actualProducto = {};
                    $window.location.href = '/#/productos/productos';
                    $scope.consultarProductos();

                }).error(function (res) {
                growlService.growl(' Ocurrió un error actualizando la información.', 'inverse');
                console.log("Doesn't work para actualizar producto");
                console.log("El error para actualizar producto: " + res);
            });
        };
        //autocarga de los productos en la gestion
        $scope.consultarProductos();
        $scope.combox();
        $scope.comboxTipo();

        $scope.detailProd = function (id) {
            $rootScope.prodId = id;
        };

    });

angular.module('materialAdmin')
    .controller('ProductosDetalle', function ($scope, $rootScope, $http, $location, $filter, $sce, ngTableParams, tableService) {

        $scope.datos = [];
        $scope.data = {};

        $scope.myInterval = 0;
        $scope.galeria = [];

        $scope.preguntas = [];

        $scope.calificaciones = [];
        $scope.showCalificaciones = 0;


        if (sessionStorage.token != null) {
            $scope.lok = 1;
            $scope.nombreUsr= sessionStorage.nombreusuario;
        }
        else
            $scope.lok = 0;


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


            $http({method: 'GET', url: 'http://localhost:8181/galeria/' + id})
                .success(function(res){
                    $scope.galeria=res
                    console.log(res);

                }).error(function(res){
                console.log("Doesn't work");
                console.log("Que trae esto: "+res);
            })

            $http({method: 'GET', url: 'http://localhost:8181/producto_detalle/calificacion/' + id})
                .success(function(res){
                    $scope.calificaciones=res
                    if(res.length > 0)
                        $scope.showCalificaciones = 1;
                    $scope.tableBasic = new ngTableParams(
                        {page: 1, count: 5},
                        {
                            total: res.length,
                            getData: function ($defer, params) {

                                $defer.resolve(res.slice((params.page() - 1) * params.count(), params.page() * params.count()));

                            }
                        }
                    );

                }).error(function(res){
                console.log("Doesn't work");
                console.log("Que trae esto: "+res);
            })


            $scope.buscarPreg(id);



        };

        $scope.crearPreg = function () {



            if (sessionStorage.token != null){
                $scope.idUser = 1;
                $scope.manejador = {withCredentials: true, headers: {token: sessionStorage.token}};
            }else {
                $scope.idUser = 0;
                $scope.manejador ="";


            }

            $scope.addPreg = {
                usuario : $scope.idUser,
                producto : $rootScope.prodId,
                pregunta : document.getElementById('textoPregunta').value
            }



            $http.post("http://localhost:8181/preguntas/",  $scope.addPreg  , $scope.manejador)
                .success(function (res) {
                    $scope.buscarPreg($rootScope.prodId);
                    document.getElementById('textoPregunta').value ='';
                }).error(function (res) {
                console.log("Doesn't work para insertar pregunta");
                console.log("El error para insertar pregunta: " + res);
            });



        };

        $scope.buscarPreg = function (id) {
            $http({method: 'GET', url: 'http://localhost:8181/preguntas/' + id})
                .success(function (res) {
                    $scope.preguntas = res
                    console.log(res);

                }).error(function (res) {
                console.log("Doesn't work");
                console.log("Que trae esto: " + res);
            })
        };

        $scope.detailProd();

        $scope.getLok = function () {
            return $scope.lok;
        };

        $scope.getShowCalificaciones = function () {
            return $scope.showCalificaciones;
        };



    });


angular.module('materialAdmin')

    .controller('ProductosBusqueda', function ($scope, $rootScope, $http, $location, $filter, $sce, ngTableParams, tableService) {

        $scope.datos = [];
        $scope.data = {};

        $scope.comentario = [];
        $scope.comentarios = {};

        $scope.queryProducts = function () {

            var criterios = document.getElementById('criterios').value

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
