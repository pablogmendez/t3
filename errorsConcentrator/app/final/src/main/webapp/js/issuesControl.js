myApp.controller('issuesControl', function($scope , $location, $http, userservice){
	$scope.username = userservice.username;
    $scope.hideForm = false;
	$scope.sendRequest = false;
    $scope.creatingIssue = false;
	$scope.response = "";
    $scope.submit = function() {   
    	var date = new Date().format('m-d-Y h:i:s');
    	var data = {
    				username:    $scope.username,
    				application: $scope.application, 
    				summary:     $scope.summary,
    				os:          $scope.os,
    				description: $scope.description
    			};
        $scope.hideForm = true;
        $scope.creatingIssue = true;
        $http({
   			method: 'POST',
    		url: 'http://localhost:8080/errorsconcentrator',
    		params: data,
    		headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).then(function successCallback(response) {
            $scope.creatingIssue = false;
            $scope.sendRequest = true;
			$scope.response = "Issue was created successfully with id 987";
		}, function errorCallback(response) {
            $scope.creatingIssue = false;
            $scope.sendRequest = true;
			$scope.response = "Internal Error";
		});
    };
    $scope.back = function() {
    	$scope.sendRequest = false;
        $scope.hideForm = false;
    	$scope.application = "";
    	$scope.summary = "";
    	$scope.summary = "";
    	$scope.os = "";
    	$scope.description = "";
    }
});