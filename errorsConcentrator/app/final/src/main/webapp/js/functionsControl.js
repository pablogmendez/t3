myApp.controller('functionsControl', function($scope , $location, $http, userservice, config){
    $scope.searching = false;
    $scope.found = false;
    $scope.functions = [];
    $scope.currentTimeQuery = "...";
    $scope.timeQuery = ["1 hour", "2 hours", "3 hours", "4 hours", "5 hours", "6 hours",
     "7 hours", "8 hours", "9 hours", "10 hours"];
    $scope.status = ["dispabled", "dispabled", "dispabled", "dispabled",
     "dispabled", "dispabled", "dispabled", "dispabled", "dispabled", "dispabled"];
    $scope.time = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];


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