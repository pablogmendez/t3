routerApp.controller('confirmarasistenciaController', function($scope, $http, globalConf) {
	$scope.submitDisabled = false;
	$scope.response = "";
	$scope.submit = function() {  
		$scope.submitDisabled = true; 
    	var data = {
    				name: 		$scope.name,
    				lastname: 	$scope.lastname, 
    				email:     	$scope.email,
    				company:    $scope.company,
    	};
        $http({
   			method: 'POST',
    		url: globalConf.api_url + "/confirm",
    		params: data,
    		headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).then(function successCallback(response) {
			$scope.response = "La confirmacion ha sido exitosa con el ID: " + response.data.id;
		}, function errorCallback(response) {
			$scope.response = "Error " + response.status + ": " + response.data;
		});
		$scope.submitDisabled = false; 
    };
});