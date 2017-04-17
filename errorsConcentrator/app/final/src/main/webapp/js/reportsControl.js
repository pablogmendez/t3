myApp.controller('reportsControl', function($scope , $location, $http, userservice, config){
    var newerCursor = "";
    var stack = [];
    $scope.apps = [];
    $scope.searching = true;

    $scope.previousClass="previous disabled";
    $scope.nextClass="next";

	$http({
		method: 'GET',
		url: config.apiUrl + "?type=reports"
	}).then(function successCallback(response) {
		$scope.searching = false;
		console.log(response.data);
		$scope.apps = response.data.data;
		newerCursor = response.data.cursor;
		if($scope.apps.length < config.pageRegs) {
			$scope.nextClass = "next disabled";
		}
	}, function errorCallback(response) {
		alert("Error " + response.status + ": " + response.data);
	});

    $scope.getNextReport = function() {   
    	$scope.searching = true;
    	var data = "?type=reports";
    	if($scope.newerCursor != "") {
    		data += "&cursor=" +  newerCursor;
    	}
        $http({
   			method: 'GET',
    		url: config.apiUrl + data
		}).then(function successCallback(response) {
			$scope.searching = false;
			$scope.apps = response.data.data;
			stack.push(newerCursor);
			newerCursor = response.data.cursor;
			if($scope.apps.length < config.pageRegs) {
				$scope.nextClass = "next disabled";
			}
			$scope.previousClass="previous";
            console.log(response);
		}, function errorCallback(response) {
			alert("Error " + response.status + ": " + response.data);
		});
    };

	$scope.getPreviousReport = function() {   
		$scope.searching = true;
	    var data = "?type=reports";
	    var olderCursor = stack.pop();
	    console.log("cursor " + olderCursor);
	    if(olderCursor != undefined && stack.length > 0) {
    		data += "&cursor=" +  olderCursor;
    	} else {
    		$scope.previousClass="previous disabled";
    	}
        $http({
   			method: 'GET',
    		url: config.apiUrl + data
		}).then(function successCallback(response) {
			$scope.searching = false;
			$scope.apps = response.data.data;
			newerCursor = response.data.cursor;
			if($scope.apps.length >= config.pageRegs) {
				$scope.nextClass = "next";
			}
            console.log(response);
		}, function errorCallback(response) {
			alert("Error " + response.status + ": " + response.data);
		});	
	}
});