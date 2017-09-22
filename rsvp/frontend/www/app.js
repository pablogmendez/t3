// app.js
var routerApp = angular.module('routerApp', ['ui.router']);

routerApp.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/home');

    $stateProvider

        .state('home', {
            url: '/home',
            templateUrl: 'views/home.html'
        })

        .state('confirmarasistencia', {
            url: '/confirmarasistencia',
            templateUrl: 'views/confirmarasistencia.html',
            controller: 'confirmarasistenciaController'
        })

        .state('consultarasistencia', {
            url: '/consultarasistencia',
            templateUrl: 'views/consultarasistencia.html',
            controller: 'consultarasistenciaController'
        })

        .state('listarinvitados', {
            url: '/listarinvitados',
            templateUrl: 'views/listarinvitados.html',
            controller: 'listarinvitadosController'
        });

});
