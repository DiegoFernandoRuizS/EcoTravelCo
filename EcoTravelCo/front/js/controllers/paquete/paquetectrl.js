'use strict';

angular.module('materialAdmin')
    .controller('paquetectrl', function ($scope, $rootScope, $http, $location) {

       $scope.paquetes=[];
       $scope.datos = [];
       this.mostrar=true;
       $rootScope.agregado=[];
       console.log("Entro al controlador de paquetes");
       $scope.insertarPaquete=function(){
       console.log("inserto paquete");
       };
       $scope.listar = function (){
                   $http.get("http://localhost:8181/paquete", {
                       withCredentials: true,
                       headers: {token: sessionStorage.token}
                   }).success(function (res) {
                       $scope.paquetes = res;
                       console.log($scope.paquetes);
                   }).error(function (res) {
                       console.log("Doesn't work");
                       console.log("Que trae esto paquetes " + res);
                   });
               };
       $scope.listar();

              //para consultar los productos en la gestion de productos
               $scope.consultarProductos = function () {
                      this.mostrar=true;
                      console.log(this.mostrar);
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
                   }).error(function (res) {
                       growlService.growl(' Ocurrió un error consultando la información.', 'inverse');
                       console.log("Doesn't work");
                       console.log("Que trae esto: " + res);
                   });
               };

               $scope.agregarProductos=function(productoId){
               for(var i=0;i<$scope.datos.length;i++){
                    if($scope.datos[i].id===productoId){
                        $rootScope.agregado.push($scope.datos[i]);
                    }
               }
               console.log($rootScope.agregado);
               };

                $scope.quitarProducto=function(productoId){
                      for(var i=0;i<$rootScope.agregado.length;i++){
                          if($rootScope.agregado[i].id===productoId){
                            $rootScope.agregado.splice(i,1);
                          }
                       }

              console.log($rootScope.agregado);
              };
 });
