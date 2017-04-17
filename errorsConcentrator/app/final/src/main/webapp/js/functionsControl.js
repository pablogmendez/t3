myApp.controller('functionsControl', function($scope , $location, $http, userservice, config){
    $scope.searching = false;
    $scope.found = true;
    $scope.functions = [];
    $scope.currentTimeQuery = "...";
    $scope.timeQuery = ["1 hour", "6 hour", "12 hour", "1 day", "2 days", "4 days",
     "7 days", "14 days", "30 days"];
    $scope.status = ["dispabled", "dispabled", "dispabled", "dispabled",
     "dispabled", "dispabled", "dispabled", "dispabled", "dispabled"];
    $scope.time = [1, 6, 12, 24, 48, 96, 168, 336, 720];


	$scope.getData = function(event)
	{
		$scope.searching = true;
		for(var i = 0; i < $scope.status.length; i++) {
			$scope.status[i] = "dispabled";
		}
		$scope.status[event.target.id] = "active";
		$scope.currentTimeQuery = $scope.timeQuery[event.target.id];
		$http({
			method: 'GET',
			url: config.apiUrl + "?type=functions&hours=" + $scope.time[event.target.id]
		}).then(function successCallback(response) {
			$scope.searching = false;
			console.log(response.data.data);
			$scope.functions = response.data.data;
			if($scope.functions.length > 0) {
				$scope.found = true;
			} else {
				$scope.found = false;
			}
		}, function errorCallback(response) {
			alert("Error " + response.status + ": " + response.data);
		});
	};
});