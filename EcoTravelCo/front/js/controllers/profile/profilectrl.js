'use strict';

console.log("cargando el archivo profilectrl.js");

angular.module('materialAdmin')
  .controller('ProfileCtrl', function ($scope, $rootScope, $http,growlService) {


      	$scope.datos = {};
      	$scope.usuario = {login:"seduardojs",contrasenia:"Asdf1234$"};


//obtenermos la informacion del usuario

          $scope.datos = {nombre: "samuel"};


        /********************************/

        console.log("consultando usuario-->" + sessionStorage.token);



        $http.get("http://localhost:8181/usuario/consultar",{withCredentials: true,headers: {token:sessionStorage.token }})

        			.success(function(res){
        				$scope.datos=res
        				console.log(res);
        				console.log($scope.datos);
        				$scope.datos = res;




        			}).error(function(res){
        			growlService.growl(' Ocurrió un error consultanto la información.!', 'inverse');
        			console.error(res);
        		});


        /********************************/

         $scope.actualizarFoto = function () {
                                        	console.log("actualizando foto");


var foto = document.getElementById("fotoperfil").files[0];


//console.log(foto.result);

var objeto = {};
objeto.foto = foto.result;

console.log(objeto);

$http.put("http://localhost:8181/usuario/cliente/foto",objeto,{withCredentials: true,headers: {token:sessionStorage.token }})
        	    .success(function(res){

              	growlService.growl('Se actualizó correctamente la información.', 'inverse');

              	$scope.datos.foto = foto.result;

        	    }).error(function(res){
        	        growlService.growl(' Ocurrió un error actualizando la información.', 'inverse');

        	    });






                                       		};

        $scope.actualizar = function () {
         	$http.put("http://localhost:8181/usuario/cliente",$scope.datos,{withCredentials: true,headers: {token:sessionStorage.token }})
        	    .success(function(res){

              	growlService.growl('Se actualizó correctamente la información.', 'inverse');

        	    }).error(function(res){
        	        growlService.growl(' Ocurrió un error actualizando la información.', 'inverse');

        	    });

        		};


        //Get Profile Information from profileService Service

        $scope.nombre = "samuel";
        this.getNombre = function(){
        return $scope.datos.nombre;
        }

        //Edit
        this.editSummary = 0;
        this.editInfo = 0;
        this.editContact = 0;




    
  });
  
  
  
  
