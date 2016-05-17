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
    .controller('ProductosCtrlProveedor', function ($scope, $rootScope, $http, $location, $window,growlService, ngTableParams, tableService) {
        $scope.datos = [];
        $scope.listaValores = [];
        $scope.listaTipo = [];
        $scope.imagen=[];
        $rootScope.imagenesCargadas = [];
        $rootScope.imagenesEliminar = [];



        //Manejo de imagenes para crear
        $scope.imageUpload = function (event) {
            var files = event.target.files;

            for (var i = 0; i < files.length; i++) {
                var file = files[i];
                var reader = new FileReader();
                reader.onload = $scope.imageIsLoaded;
                reader.readAsDataURL(file);
            }
        }

        $scope.imageIsLoaded = function (e) {
            $scope.$apply(function () {
                var img = {"img":e.target.result};
                $rootScope.imagenesCargadas.push(img);
            });
        }

        $scope.eliminarFoto = function (key) {
            for (var i = 0; i < $rootScope.imagenesCargadas.length; i++){
                if($rootScope.imagenesCargadas[i]["$$hashKey"] == key){
                    $rootScope.imagenesCargadas.splice(i,1);
                }
            }
        }

        //Manejo de imagenes para editar
        $scope.imageUploadEdit = function (event) {
            var files = event.target.files;

            for (var i = 0; i < files.length; i++) {
                var file = files[i];
                var reader = new FileReader();
                reader.onload = $scope.imageIsLoadedEdit;
                reader.readAsDataURL(file);
            }
        }

        $scope.imageIsLoadedEdit = function (e) {
            $scope.$apply(function () {
                var img = {"url":e.target.result,
                    "id":0  };
                $rootScope.imagenesCargadas.push(img);
            });
        }

        $scope.eliminarFotoEdit = function (key) {
            for (var i = 0; i < $rootScope.imagenesCargadas.length; i++){
                if($rootScope.imagenesCargadas[i]["$$hashKey"] == key){
                    if($rootScope.imagenesCargadas[i]["id"] == 0){
                        $rootScope.imagenesCargadas.splice(i,1);
                    } else {
                        $rootScope.imagenesEliminar.push($rootScope.imagenesCargadas[i]);
                        $rootScope.imagenesCargadas.splice(i,1);
                    }
                }
            }
        }

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

                $scope.tableBasic = new ngTableParams(
                    {page: 1, count: 10},
                    {
                        total: res.length,
                        getData: function ($defer, params) {

                            $defer.resolve(res.slice((params.page() - 1) * params.count(), params.page() * params.count()));

                        }
                    }
                );


                var productosTotal = Object.keys($rootScope.cantidadProductos).length;
                $rootScope.cantidadProductos=productosTotal;
            }).error(function (res) {
                growlService.growl(' Ocurrió un error consultando la información.', 'inverse');
                console.log("Doesn't work");
                console.log("Que trae esto: " + res);
            });
        };
        //para insertar los productos
        $scope.insertarProducto = function () {
            var error = false;
            // Validamos que se carge almenos una imagen
            if($rootScope.imagenesCargadas.length == 0){
                growlService.growl('Se debe ingresar una imagen.', 'danger');
                error = true;
            }

            if(!error){
                var sesion = {withCredentials: true, headers: {token: sessionStorage.token}};
                $http.post("http://localhost:8181/producto/", $scope.producto, sesion)
                    .success(function (res) {
                        //agregar Imagenes
                        var idProd = res["keys"][0];
                        var principal = 1;
                        for (var i = 0; i < $rootScope.imagenesCargadas.length; i++){
                            var datosImagen = {
                                "imagen": $rootScope.imagenesCargadas[i]["img"],
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
                            principal = 0;
                        }

                        growlService.growl('Se guardo correctamente la información.', 'success');
                        $scope.insertarProducto = {};
                        console.log("La respuesta del backend " + res);
                        $location.path( "/productos/productos" );
                        //$scope.consultarProductos();
                    }).error(function (res) {
                    growlService.growl(' Ocurrió un error guardando la información.', 'danger');
                    console.log("Doesn't work para insertar producto");
                    console.log("El error para insertar producto: " + res);
                });
            }

        };
        //para borrar los productos
        $scope.borrarProducto = function (id) {
            console.log("Borrar producto en el controlador " + id);
            $http.delete("http://localhost:8181/producto/" + id, $scope.productoDelete, {withCredentials: true, headers: {token: sessionStorage.token}})
                .success(function (res) {
                    $scope.consultarProductos();
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
            $rootScope.actualProducto = [];
            console.log("Listar producto en el controlador " + id);
            $http.get("http://localhost:8181/producto_detalle/" + id)
                .success(function (res) {
                    $http({method: 'GET', url: 'http://localhost:8181/galeria/' + id})
                        .success(function (resGaleria) {
                            $rootScope.imagenesCargadas = [];
                            $rootScope.imagenesCargadas = resGaleria;
                        }).error(function (resGaleria) {
                        console.log("Doesn't work");
                        console.log("Que trae esto: " + resGaleria);
                    })
                    $rootScope.actualProducto = res[0];
                    console.log($rootScope.actualProducto);
                    $rootScope.actualProducto.cantidad = "" + $rootScope.actualProducto.cantidad_actual;
                    $rootScope.actualProducto.latitud = "" + $rootScope.actualProducto.latitud;
                    $rootScope.actualProducto.longitud = "" + $rootScope.actualProducto.longitud;
                    $rootScope.actualProducto.precio = "" + $rootScope.actualProducto.precio;

                }).error(function (res) {
                console.log("Doesn't work para listar producto");
                console.log("El error para borar producto: " + res);
            });
        };

        //para actualizr el producto en la gestion
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


        $scope.calcularCoordenadas = function (id) {
            $rootScope.productoEditar = [];
            $rootScope.actualProducto = [];
            var dir = ""+
                $scope.producto.nombredireccion +" "+
                $scope.producto.pais +" "+
                $scope.producto.departamento +" "+
                $scope.producto.ciudad ;

            $http.get("http://localhost:8181/direccion/coordenada/" + dir)
                .success(function (res) {
                    $scope.producto.latitud = res[0].latitude;
                    $scope.producto.longitud = res[0].longitude;
                }).error(function (res) {
                console.log("Doesn't work para listar producto");
                console.log("El error para borar producto: " + res);
            });
        };


        $scope.calcularCoordenadasEdit = function (id) {
            var dir = ""+
                $scope.actualProducto.nombredireccion +" "+
                $scope.actualProducto.pais +" "+
                $scope.actualProducto.departamento +" "+
                $scope.actualProducto.ciudad ;

            $http.get("http://localhost:8181/direccion/coordenada/" + dir)
                .success(function (res) {
                    $scope.actualProducto.latitud = res[0].latitude;
                    $scope.actualProducto.longitud = res[0].longitude;
                }).error(function (res) {
                console.log("Doesn't work para listar producto");
                console.log("El error para borar producto: " + res);
            });
        };

        //autocarga de los productos en la gestion
        $scope.consultarProductos();
        $scope.combox();
        $scope.comboxTipo();

        $scope.detailProd = function (id) {
            $rootScope.prodId = id;
            $rootScope.cantidadCanasta = 1;
        };

        this.getLocalStorageById=function(id){
            return localStorage.getItem(id);
        }


    });

angular.module('materialAdmin')
    .controller('ProductosDetalle', function ($scope, $rootScope, $http, $location, $filter, $sce, growlService, ngTableParams, tableService) {

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
                    $rootScope.latitud = res[0].latitud;
                    $rootScope.longitud = res[0].longitud;
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

            $http({method: 'GET', url: 'http://localhost:8181/calificacion/' + id})
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

            //Cargamos los hijos del paquete
            $http({method: 'GET', url: 'http://localhost:8181/paquete/hijos/' + id})
                .success(function (res) {
                    $scope.hijos = res
                    console.log(res);
                }).error(function (res) {
                console.log("Doesn't work");
                console.log("Que trae esto: " + res);
            })

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

        $scope.detailProdDet = function (id) {
            $rootScope.prodId= id;
        };

        $scope.tweet = function () {

            var param1 = sessionStorage.getItem("oauth_token");
            var param2 = $scope.datos[0].nombre;
            var param3 = $scope.datos[0].vendedor;
            var param = param1 + "/"+  param2 +"/"+  param3;
            $http({method: 'GET', url: 'http://localhost:8181/seguridad/tweet/' + param})
                .success(function (res) {

                    growlService.growl('Se publicó el mensaje exitosamente en tu perfil de twitter');

                }).error(function (res) {
                console.log("Doesn't work");
                console.log("Que trae esto: " + res);
            })
        }

        this.getLocalStorageById=function(id){
            return localStorage.getItem(id);
        }

    });


angular.module('materialAdmin')

    .controller('ProductosBusqueda', function ($scope, $rootScope, $http, $location, $filter, $sce, ngTableParams, tableService) {

        $scope.datos = [];
        $scope.data = {};

        $scope.comentario = [];
        $scope.comentarios = {};
        $scope.position = "";

        $scope.queryProducts = function () {

            var criterios = document.getElementById('criterios').value

            $http({method: 'GET', url: 'http://localhost:8181/producto_busqueda/' + criterios})
                .success(function(res){
                    $scope.datos=res;

                    $scope.tableFilter = new ngTableParams(
                        {page: 1, count: 5},
                        {
                            total: res.length,
                            getData: function($defer, params) {
                                // use build-in angular filter
                                var orderedData = params.filter() ? $filter('filter')(res, params.filter()) : res;
                                this.nombre = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());
                                this.tipo = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());
                                this.descripcion = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());
                                this.precio = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());
                                this.cantidad_actual = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());
                                this.vendedor = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());
                                params.total(orderedData.length); // set total for recalc pagination
                                $defer.resolve( this.nombre , this.tipo, this.descripcion, this.precio, this.cantidad_actual, this.vendedor );
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

    .controller('ProductosMapa', function ($scope, $rootScope, growlService, $http, $location, $filter, $sce) {


        function initMap() {

            var lat = sessionStorage.getItem('latitude');
            var lon = sessionStorage.getItem('longitude');

            var pointA = new google.maps.LatLng(lat,lon),
                pointB = new google.maps.LatLng( $rootScope.latitud,$rootScope.longitud ),
                myOptions = {
                    zoom: 7,
                    center: pointA
                },
                map = new google.maps.Map(document.getElementById("ruta"), myOptions),
            // Instantiate a directions service.
                directionsService = new google.maps.DirectionsService,
                directionsDisplay = new google.maps.DirectionsRenderer({
                    map: map
                }),
                markerB = new google.maps.Marker({
                    position: pointB,
                    title: "point B",
                    label: "B",
                    map: map
                });

            // get route from A to B
            calculateAndDisplayRoute(directionsService, directionsDisplay, pointA, pointB);

        }


        function calculateAndDisplayRoute(directionsService, directionsDisplay, pointA, pointB) {
            directionsService.route({
                origin: pointA,
                destination: pointB,
                travelMode: google.maps.TravelMode.DRIVING
            }, function(response, status) {
                if (status == google.maps.DirectionsStatus.OK) {
                    directionsDisplay.setDirections(response);
                } else {
                    growlService.growl('No fue posible calcular tu ruta, intenta de nuevo.', 'danger');
                }
            });
        }



        function getIp() {
            if(localStorage.getItem("Ruta")!=null){
                var latitude = sessionStorage.getItem('latitude');
                if (latitude == null) {
                    navigator.geolocation.getCurrentPosition(function (position) {
                        sessionStorage.setItem("latitude", position.coords.latitude);
                        sessionStorage.setItem("longitude", position.coords.longitude);
                        $scope.position = position;
                        initMap();
                    });
                }else {
                    initMap();
                }
            }
        }


        this.getLocalStorageById=function(id){
            return localStorage.getItem(id);
        }


        getIp();



    });
