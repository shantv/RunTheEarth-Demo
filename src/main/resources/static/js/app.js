'use strict';

angular.module("app", [
                       'ngRoute',
                       'ngResource',
                       'ngMap',
                       'angular.filter'
                       ])
.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/friends', {
        templateUrl: '/auth/friends.html',
        controller: 'home as home'
    })
    .when('/map',{
	templateUrl: '/auth/routes.html',
	controller: 'RouteCtrl as RouteCtrl'
    })
    .when('/stats',{
	templateUrl: '/auth/stats.html',
	controller: 'StatsCtrl as StatsCtrl'
    })
    .otherwise({redirectTo: '/map'});
} ])
.controller("RouteCtrl", RouteCtrl)
.controller("StatsCtrl", StatsCtrl)
.controller("home", function(MapService,$window, $rootScope, $scope, $http, $location, $resource,FriendService,UserService,RouteService) {
    var self = this;

    self.RouteService = RouteService;
    self.UserService = UserService;
    $window.ga('create', '<***EXCLUDED***>', 'auto');
    $window.ga('send', 'pageview');
    $scope.location = $location;
    $scope.init = function() {

    };
    $http.get("/user").success(function(data) {
	if (data.token) {
	    UserService.authenticate(data);
	    FriendService();
	    self.authenticated = true;
	} else {
	    self.name = "N/A";
	    self.authenticated = false;
	}
    }).error(function() {
	self.name = "N/A";
	self.authenticated = false;
	$rootScope.user = undefined;
    });
    self.logout = function() {
	$http.post('logout', {}).success(function() {
	    self.authenticated = false;
	    $rootScope.user = undefined;
	}).error(function(data) {
	    console.log("Logout failed")
	    self.authenticated = false;
	    $rootScope.user = undefined;
	});
    };
})
.factory('MapService', MapService)
.factory('UserService', UserService)
.factory('ActivityService', ActivityService)
.factory('RouteService', RouteService)
.factory('FriendService', FriendService)
.factory('WorkoutService', WorkoutService);