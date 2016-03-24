app.controller('ctrl_register', function ($scope, $http) {

    $scope.service = {};
    $scope.register = {};
    $scope.register.foto = {};
    $scope.fotoCompressed = {};

    $scope.save_register = function () {

        $scope.post_register();
    }

    $scope.get_service = function () {

        $http({method: 'GET', url: '/api/servicios/'})
            .success(function (data, status) {
                $scope.service = data;
            }).error(function (data, status) {
            $scope.service = {};
            alert(data);
        })
    };

    $scope.get_service();

    $scope.post_register = function () {

        if ($scope.fotoCompressed.compressed.dataURL === 'undefined') {
            alert('Ingresa el campo Foto');
        } else {
            $scope.register.foto = $scope.fotoCompressed.compressed.dataURL;
            $http({
                method: 'POST',
                url: '/api/independientes/',
                data: $scope.serialize($scope.register),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            })
                .success(function (data, status) {
                    alert('Se registró satisfactoriamente la información');
                    window.location.href = "/login/";
                }).error(function (data, status) {
                alert('Se presentó un error registrando la información, intente de nuevo\n\n' + data);
            })
        }
    };

    $scope.serialize = function (obj) {
        var str = [];
        for (var p in obj) {
            if (obj.hasOwnProperty(p)) {
                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
            }
        }
        return str.join("&");
    };

});