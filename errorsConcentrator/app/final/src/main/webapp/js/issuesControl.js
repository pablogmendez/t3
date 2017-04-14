myApp.controller('issuesControl', function($scope , $location, $http, userservice){
	$scope.username = userservice.username;
});