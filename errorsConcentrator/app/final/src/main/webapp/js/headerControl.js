myApp.controller('headerCtrl', function($scope, $location, userservice) {
    $scope.$on('$locationChangeSuccess', function(/* EDIT: remove params for jshint */) {
        var path = $location.path();
        //EDIT: cope with other path
        if(path==='/issues' || path==='/reports' || path==='/functions' || path==='/404') {
        	$scope.username = userservice.username;
			$scope.templateUrl = 'views/header.html';
        } else {
        	$scope.templateUrl = 'views/emptyHeader.html';
        }
    });
})