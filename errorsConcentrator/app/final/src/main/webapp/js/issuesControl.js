myApp.controller('issuesControl', function($scope , $location, $http, userservice, config){
	$scope.username = userservice.username;
    $scope.hideForm = false;
	$scope.sendRequest = false;
    $scope.creatingIssue = false;
	$scope.response = "";
    $scope.submit = function() {   
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
    		url: config.apiUrl,
    		params: data,
    		headers: {'Content-Type': 'application/x-www-form-urlencoded'}
		}).then(function successCallback(response) {
            $scope.creatingIssue = false;
            $scope.sendRequest = true;
			$scope.response = "Issue was created successfully with id " + response.data + "!";
            console.log(response);
		}, function errorCallback(response) {
            $scope.creatingIssue = false;
            $scope.sendRequest = true;
			$scope.response = "Error " + response.status + ": " + response.data;
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