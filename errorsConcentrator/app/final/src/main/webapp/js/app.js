var myApp = angular.module('errorsConcentretor', ['ngRoute'] );

myApp.config( [ '$routeProvider' , function($routeProvider){
	$routeProvider.when('/' , {      
        templateUrl: "views/login.html",
        controller: 'loginControl'
    })
	.when('/issues' , {      
        templateUrl: "views/issues.html",
        controller: 'issuesControl'
    })
    .when('/reports' , {      
        templateUrl: "views/reports.html",
        controller: 'reportsControl'
    })
    .when('/functions' , {      
        templateUrl: "views/functions.html",
        controller: 'functionsControl'
    })
    .when('/404', {
        templateUrl: 'views/404.html',
        controller: '404Control'
    })
    .otherwise({
        redirectTo: '/404'
        
    })
	} ] )