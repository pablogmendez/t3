routerApp.controller('consultarasistenciaController', function($scope, $http, globalConf) {
	$scope.submitDisabled = false;
	$scope.response = "";
	$scope.submit = function() {  
		$scope.submitDisabled = true;
        $http({
   			method: 'GET',
    		url: globalConf.api_url + "/query?id=" + $scope.id,
    		headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).then(function successCallback(response) {
			$scope.response = "Asistencia confirmada del usuario = Nombre: " + response.data.Name + ", Apellido: " + response.data.LastName + ", Email: " + response.data.Email + ", Compania: " + response.data.Company;
		}, function errorCallback(response) {
			$scope.response = "No existe ninguna confirmacion para el ID: " + $scope.id;
		});
		$scope.submitDisabled = false; 
    };
});