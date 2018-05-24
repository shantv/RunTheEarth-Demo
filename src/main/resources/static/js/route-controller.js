var RouteCtrl = [
		'$rootScope',
		'$scope',
		'$routeParams',
		'$location',
		'$resource',
		'$interval',
		'$log',
		'$timeout',
		'NgMap',
		'RouteService',
		'UserService',
		'MapService',
		function($rootScope, $scope, $routeParams, $location, $resource, $interval, $log, $timeout, NgMap,RouteService,UserService,MapService) {
                    NgMap.getMap().then(function(m) {
                	$rootScope.map = m;
                	$rootScope.map.setMapTypeId('hybrid');
                	if ($rootScope.routeIds.length == 0) {
                	    console.log($rootScope.routeIds);
                	    RouteService.get();
                	}
                    });
                    
                }
];
