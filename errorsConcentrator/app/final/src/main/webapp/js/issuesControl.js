myApp.controller('issuesControl', function($scope , $location, $http, userservice){
	$scope.username = userservice.username;
	$scope.sendRequest = false;
	$scope.response = "";
    $scope.submit = function() {   
    	var date = new Date().format('m-d-Y h:i:s');
    	var data = {
    				username:    $scope.username,
    				timestamp: 	 date,
    				application: $scope.application, 
    				summary:     $scope.summary,
    				os:          $scope.os,
    				description: $scope.description
    			};
    	$scope.sendRequest = true;
        $http({
   			method: 'POST',
    		url: 'http://localhost:8080/sign',
    		params: data,
    		headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).then(function successCallback(response) {
			$scope.response = "Issue was created successfully with id 987";
		}, function errorCallback(response) {
			$scope.response = "Internal Error";
		});
    };
    $scope.back = function() {
    	$scope.sendRequest = false;
    	$scope.application = "";
    	$scope.summary = "";
    	$scope.summary = "";
    	$scope.os = "";
    	$scope.description = "";
    }
});