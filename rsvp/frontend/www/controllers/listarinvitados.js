routerApp.controller('listarinvitadosController', function($scope, $http, globalConf) {
    var newerCursor = "";
    var stack = [];
    $scope.guests = [];
    $scope.search = true;

	$scope.name = "";
	$scope.lastname = "";
	$scope.email = "";
	$scope.company = "";

    $scope.submit = function() {
    	$scope.searching = true;
    	$scope.showTable = false;
    	$scope.search = false;
    	$scope.previousClass="previous disabled";
    	$scope.nextClass="next";
		$http({
			method: 'GET',
			url: globalConf.api_url + "/list?name=" + $scope.name + "&lastname=" + $scope.lastname + "&email=" + $scope.email + "&company=" + $scope.company + "&cursor="
		}).then(function successCallback(response) {
			$scope.searching = false;
			$scope.showTable = true;
			$scope.guests = response.data.guests;
			newerCursor = response.data.cursor;
			if($scope.guests.length < globalConf.pageRegs) {
				$scope.nextClass = "next disabled";
			} else {
				$scope.nextClass="next";
			}
		}, function errorCallback(response) {
			$scope.searching = false;
			$scope.showTable = false;
			$scope.nextClass="next disabled";
		});
	}

    $scope.getNextReport = function() {   
    	$scope.searching = true;
    	var data = "/list?name=" + $scope.name + "&lastname=" + $scope.lastname + "&email=" + $scope.email + "&company=" + $scope.company;
    	if($scope.newerCursor != "") {
    		data += "&cursor=" +  newerCursor;
    	}
        $http({
   			method: 'GET',
    		url: globalConf.api_url + data
		}).then(function successCallback(response) {
			$scope.searching = false;
			$scope.showTable = true;
			$scope.guests = response.data.guests;
			console.log(response.data.guests);
			stack.push(newerCursor);
			newerCursor = response.data.cursor;
			if($scope.guests.length < globalConf.pageRegs) {
				$scope.nextClass = "next disabled";
			}
			$scope.previousClass="previous";
		}, function errorCallback(response) {
			$scope.searching = false;
			$scope.showTable = false;
		});
    };

	$scope.getPreviousReport = function() {   
		$scope.searching = true;
	    var data = "/list?name=" + $scope.name + "&lastname=" + $scope.lastname + "&email=" + $scope.email + "&company=" + $scope.company;
	    var olderCursor = stack.pop();
	    if(olderCursor != undefined && stack.length > 0) {
    		data += "&cursor=" +  olderCursor;
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
		}, function errorCallback(response) {
			$scope.searching = false;
			$scope.showTable = false;
		});	
	}	
});