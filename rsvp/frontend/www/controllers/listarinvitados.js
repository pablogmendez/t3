routerApp.controller('listarinvitadosController', function($scope, $http, globalConf) {
    var newerCursor = "";
    var stack = [];
    $scope.guests = [];
    $scope.searching = true;
    $scope.showTable = false;

    $scope.previousClass="previous disabled";
    $scope.nextClass="next";

	$http({
		method: 'GET',
		url: globalConf.api_url + "/list?cursor="
	}).then(function successCallback(response) {
		$scope.searching = false;
		$scope.showTable = true;
		$scope.guests = response.data.guests;
		console.log(response.data.guests);
		newerCursor = response.data.cursor;
		if($scope.guests.length < globalConf.pageRegs) {
			$scope.nextClass = "next disabled";
		} else {
			$scope.nextClass="next disabled";
		}
	}, function errorCallback(response) {
				console.log("bbbb");
		console.log(response);
		$scope.searching = false;
		$scope.showTable = false;
		$scope.nextClass="next disabled";
	});

    $scope.getNextReport = function() {   
    	$scope.searching = true;
    	var data = "/list?";
    	if($scope.newerCursor != "") {
    		data += "cursor=" +  newerCursor;
    	}
        $http({
   			method: 'GET',
    		url: globalConf.api_url + data
		}).then(function successCallback(response) {
			$scope.searching = false;
			$scope.showTable = true;
			$scope.guests = response.data.guests;
			stack.push(newerCursor);
			newerCursor = response.data.cursor;
			if($scope.guests.length < globalConf.pageRegs) {
				$scope.nextClass = "next disabled";
			}
			$scope.previousClass="previous";
            console.log(response);
		}, function errorCallback(response) {
			$scope.searching = false;
			$scope.showTable = false;
		});
    };

	$scope.getPreviousReport = function() {   
		$scope.searching = true;
	    var data = "/list?";
	    var olderCursor = stack.pop();
	    console.log("cursor " + olderCursor);
	    if(olderCursor != undefined && stack.length > 0) {
    		data += "cursor=" +  olderCursor;
    	} else {
    		$scope.previousClass="previous disabled";
    	}
        $http({
   			method: 'GET',
    		url: globalConf.api_url + data
		}).then(function successCallback(response) {
			$scope.searching = false;
			$scope.showTable = true;
			$scope.guests = response.data.guests;
			newerCursor = response.data.cursor;
			if($scope.guests.length >= globalConf.pageRegs) {
				$scope.nextClass = "next";
			}
            console.log(response);
		}, function errorCallback(response) {
			$scope.searching = false;
			$scope.showTable = false;
		});	
	}	
});