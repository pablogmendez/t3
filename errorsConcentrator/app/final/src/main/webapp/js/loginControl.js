myApp.controller('loginControl', function($scope , $location, $http, userservice){
	$scope.signIn = true;
	$scope.signUp = false;
	$scope.created = false;
	$scope.newUser;
	$scope.loginValidate = function() {
		if($scope.loginForm.$valid){
			userservice.username = $scope.username;
			$location.path("/issues");
		}
	}

	$scope.register = function() {
		$scope.signIn = false;
		$scope.signUp = true;
	}

	$scope.create = function() {
		$scope.signUp = false;
		$scope.created = true;
	}

	$scope.back = function() {
		$scope.signIn = true;
		$scope.signUp = false;
		$scope.created = false;
	}
});