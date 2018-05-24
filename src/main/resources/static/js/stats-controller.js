var StatsCtrl = [
		'$filter',
		'$rootScope',
		'$scope',
		'$routeParams',
		'$location',
		'$resource',
		'$interval',
		'$log',
		'$timeout',
		'UserService',
		'WorkoutService',
		'ActivityService',
		function($filter, $rootScope, $scope, $routeParams, $location,
				$resource, $interval, $log, $timeout, UserService,
				WorkoutService, ActivityService) {
			var self = this;

			if ($rootScope.activitys == undefined) {
				ActivityService.update();
			}
			if ($rootScope.workoutIds.length == 0) {
				console.log($rootScope.workoutIds);
				WorkoutService.get();
			}

			$scope.getMonthYear = function(date) {
				var f = $filter('date')(date.start_datetime, 'MMM-yyyy');
				return f;
			};
			$scope.getWeekYear = function(date) {
				var f = $filter('date')(date.start_datetime, 'MMM-yyyy ww');
				return f;
			};
			$scope.getYear = function(date) {
				var f = $filter('date')(date.start_datetime, 'yyyy');
				return f;
			};
    	    $scope.grouping = $scope.getMonthYear;



		} ];
