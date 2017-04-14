myApp.controller('loginControl', function($scope , $location, $http, userservice){
	$scope.loginValidate = function() {
		if($scope.loginForm.$valid){
			userservice.username = $scope.username;
			$location.path("/issues");
		}
		else {
			alert("es validoiiii");
		}
	}
});