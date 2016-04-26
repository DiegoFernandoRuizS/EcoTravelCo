/**
 * Created by Jorge on 27/03/2016.
 */

'use strict';


angular.module('materialAdmin')

    .controller('MensajesCtrl', function ($scope, $rootScope, $http, $location, $filter, $sce, ngTableParams, tableService,$interval) {

        $scope.usuarios = [];
        $scope.usuarioSeleccionado = {};

        $scope.conversacion = {};
        $scope.mensaje ="";

        var stop;

        $scope.queryUsuarios = function () {

            $http.get("http://localhost:8181/usuario/",  {withCredentials: true, headers: {token: sessionStorage.token}})
                .success(function (res) {
                    $scope.usuarios=res;
                    $scope.seleccionarUsuario(res[0])
                    console.log(res);
                }).error(function (res) {
                console.log("Doesn't work para insertar pregunta");
                console.log("El error para insertar pregunta: " + res);





            });

            console.log("inicializando el poll");

            if ( !angular.isDefined(stop) ){
            stop =   $interval( function(){
                                                   console.log("ejecutando la funcion de consultar los mensajes!!!!");
                                                   if ($scope.usuarioSeleccionado.id){
                                                       console.log("llamando la funcion de consultar los mensajes!!!!");
                                                       $scope.getMensajes($scope.usuarioSeleccionado);
                                                   }
                                               }, 15000);

            } else{
            console.log("ya esta definido");
            }

            console.log(stop);




        }


         $scope.getMensajes = function (usuario) {

                     $http.get("http://localhost:8181/mensaje/" + usuario.id ,  {withCredentials: true, headers: {token: sessionStorage.token}})
                                                        .success(function (res) {
                                                            $scope.conversacion=res;
                                                            console.log(res);
                                                        }).error(function (res) {
                                                        console.log("Doesn't work para insertar pregunta");
                                                        console.log("El error para insertar pregunta: " + res);
                                                    });

                 }



         $scope.seleccionarUsuario = function (usuario) {
            $scope.usuarioSeleccionado = usuario;
            $scope.getMensajes($scope.usuarioSeleccionado);
         }


         $scope.enviarMensaje = function () {
            console.log("enviando mensaje:" + $scope.mensaje);

             var data = {};
                data.id =$scope.conversacion.id;
                data.mensaje =$scope.mensaje;
                data.remitente = sessionStorage.correousuario;
                data.fecha=new Date();







             $http.post("http://localhost:8181/mensaje",data,{withCredentials: true,headers: {token:sessionStorage.token }})
             				.success(function(res){
             					$scope.mensaje="";
             					$scope.getMensajes($scope.usuarioSeleccionado);
             				}).error(function(res){
             				console.log("Doesn't work para insertar producto");
             				console.log("El error para insertar producto: "+res);
             				growlService.growl(' Ocurrió un error registrando el proveedor.', 'inverse');

             			});



         }

          $scope.getClase = function (m) {

          return "lv-item media " + (m.remitente===sessionStorage.correousuario ? 'right' : '');
          }







        $scope.queryUsuarios();

    });