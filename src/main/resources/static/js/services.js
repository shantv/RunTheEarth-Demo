var ActivityService = ['$rootScope','$resource',function($rootScope,$resource) {
    return {
		update: function() {
		    $rootScope.activitys = new Map();
		    $resource('/mapmyrun/activitys').query({}, function(acts) {
			acts.forEach(function(act) {
			    $rootScope.activitys[act.activityId] = act;
			});
		    });
		},
    }
}];
var UserService = ['$window','$rootScope','$resource','RouteService','WorkoutService','ActivityService','NgMap','MapService',function($window,$rootScope,$resource,RouteService,WorkoutService,ActivityService,NgMap,MapService) {
    $rootScope.$on('workouts:get', function(event, workouts) {
    	workouts._embedded.workouts.forEach(function(workout) {
    		if ($rootScope.workoutIds.indexOf(workout.workoutId) == -1) {
    			$rootScope.workoutIds.push(workout.workoutId);
    			$rootScope.workouts.push(workout);
    		}
    	});
    	console.log("Workouts Got: " + $rootScope.workoutIds.length + " of " + workouts.total_count);
    });
    var routeListener = $rootScope.$on('routes:get', function(event, routes) {
    	routes._embedded.routes.forEach(function(route) {
    		if ($rootScope.routeIds.indexOf(route.routeId) == -1) {
    			MapService.addRoute(route);
    		}
    	});
    	console.log("Routes Got: " + $rootScope.routeIds.length + " of " + routes.total_count);
    });
   return {
	   authenticate: function(user) {
		   $rootScope.token = user.token;
		   $rootScope.userAuthDetail = user.user;
		   $rootScope.userDetail = user.user;
		   if (user.isNew == true) {
			   $window.ga('create', '<***EXCLUDED***>', 'auto');
			   $window.ga('send', {
				   hitType: 'event',
				   eventCategory: 'Login',
				   eventAction: $rootScope.userAuthDetail.display_name + ' Logged in',
				   eventLabel: $rootScope.userAuthDetail.display_name + ' Logged in'
			   });
			   console.log("New session, update routes");
			   ActivityService.update();
		   } else {
//			   console.log("Old session");
		   }
	   },
	   getUserAuth: function() {
		   return $rootScope.userAuthDetil;
	   },
	   getUser: function() {
		   return $rootScope.userDetail;
	   },
	   changeUser: function(userId) {
		   console.log("Changing user: " + userId);
		   $resource('/mapmyrun/user?userId='+userId,{}).get({}, function(user) {
			   $rootScope.userDetail = user;
			   MapService.clear();
			   RouteService.get();
			   WorkoutService.get();
			   $window.ga('send', {
				   hitType: 'event',
				   eventCategory: 'Change User',
				   eventAction: $rootScope.userAuthDetail.display_name + " viewing " + user.display_name,
				   eventLabel: $rootScope.userAuthDetail.display_name + " viewing " + user.display_name
			   });
		   });
	   }
   }
}];
var MapService  = ['$rootScope','NgMap', function($rootScope,NgMap) {
	$rootScope.routeIds = [];
    var routes = [];
    var markers = [];
    var infoWindows = [];
    return {
		getSize: function() {
		    return routes.size;
		},
		clear: function() {
			$rootScope.routeIds = [];
			console.log("Clear called");
		    markers.forEach(function(m) {
		    	google.maps.event.clearInstanceListeners(m);
		    	m.setMap(null);
		    	m = null;
		    });
		    routes.forEach(function(r) {
		    	google.maps.event.clearInstanceListeners(r);
		    	r.setMap(null);
		    	r = null;
		    });
		    infoWindows.forEach(function(i) {
		    	google.maps.event.clearInstanceListeners(i);
		    	i.setMap(null);
		    	i = null;
		    });
		    markers = [];
		    routes = [];
		    infoWindows = [];
		},
		addRoute: function(route) {
		    var line = new google.maps.Polyline({
		    	path: route.points,
		    	geodesic: true,
		    	strokeColor: '#FF0000',
		    	strokeOpacity: 1.0,
		    	strokeWeight: 5,
		    	map: $rootScope.map
		    });
		    var marker = new google.maps.Marker({
		    	position: route.points[0],
		    	map: $rootScope.map,
		    	title: route.name
		    });
		    var activityIcon = "http://static.mapmyfitness.com/d/website/activity_icons/generic.png";
		    if (route.activityIcon != undefined) {
		    	activityIcon = route.activityIcon;
		    }
		    var contentString = '<div id="content">'+
		    '<div id="bodyContent">'+
		    '<p><b>Click marker to zoom in/out</b></p>'+
		    '<div class="col-lg-2"><h1 id="firstHeading" class="firstHeading"><img height=50 width=50 src="'+activityIcon+'"></h1></div>'+
		    '<div class="col-lg-10"><img src="'+'http:'+route._links.thumbnail[0].href.replace('size=100x100','size=200x200')+'"></div>' +
		    '</div>'+
		    '</div>';
		    var infoWindow = new google.maps.InfoWindow({
		    	content: contentString
		    });
		    
		    google.maps.event.addListener(marker, 'click', function(e) {
		    	var z = $rootScope.map.getZoom();
		    	var l = $rootScope.map.getCenter();
		    	if (e.latLng == l) {
		    		console.log("Same Location");
		    		if (z == 15) {
		    			$rootScope.map.setZoom(3);
		    		} else {
		    			$rootScope.map.setZoom(15);			
		    		}
		    	} else {
		    		$rootScope.map.setCenter(e.latLng);
		    		$rootScope.map.setZoom(15);
		    		console.log("Different location");
		    	}
		    });
		    
		    google.maps.event.addListener(marker, 'mouseover', function() {
				line.setOptions({strokeColor: '#FFFF00'});
				line.setOptions({ zIndex: 1000 });
				infoWindow.open($rootScope.map, marker);
		    });
		    google.maps.event.addListener(marker, 'mouseout', function() {
				line.setOptions({strokeColor: '#FF0000'});
				line.setOptions({ zIndex: 1 });
				infoWindow.close($rootScope.map, marker);
		    });
		    $rootScope.routeIds.push(route.routeId);
		    routes.push(line);
		    markers.push(marker);
		    infoWindows.push(infoWindow);
		}
    }
}];

var FriendService = ['$rootScope','$resource', function($rootScope,$resource) {
    return function() {
		$resource('/mapmyrun/friends',{}).query({}, function(friends) {
		    $rootScope.friends = [];
		    friends.forEach(function(friend) {
			$rootScope.friends.push(friend);
		    });
		});
    };
}];
var RouteService = ['$q','$timeout','$rootScope','$resource','NgMap','MapService',function($q,$timeout,$rootScope,$resource,NgMap,MapService) {
    var aborter = $q.defer();
    function createResourceInstance(url, options, aborter) {
    	return $resource(url, options || {}, {
    		'get': {
    			method: 'GET',
    			timeout: aborter.promise
    		}});
    }

    
    function getRoutes(offset, limit) {
    	this.promise = $resource('/mapmyrun/routes',{},{get:{method:'GET',timeout:aborter.promise}}).get({userId:$rootScope.userDetail.userId,offset:offset,limit:limit},function(routes) {
    		$rootScope.totalRoutes = routes.total_count;
    		$rootScope.$broadcast('routes:get',routes);
//    		console.log(routes.offset, " ", (routes.offset*limit), " ", routes);
    		if ((routes.offset * limit) < routes.total_count) {
    			getRoutes(routes.offset, limit);
    		} else {
    			updateRoutes(routes.total_count,5);
    			console.log("Done from RTE now check MMR");
    		}
    	}, function() {
    		console.log("ERROR ABORTING GETS");
    	});
    };

    function updateRoutes(offset, limit) {
    	this.promise = $resource('/mapmyrun/updateRoutes',{},{get:{method:'GET',timeout:aborter.promise}}).get({userId:$rootScope.userDetail.userId,offset:offset,limit:limit},function(routes) {
    		$rootScope.totalRoutes = routes.total_count;
    		$rootScope.$broadcast('routes:get',routes);
    		if (routes.offset > 0) {
    			console.log("UPDATE Route offset is now: " + routes.offset + " " + routes.total_count);
    			if (routes.offset > routes.total_count) {
    				console.log("Missing something from the middle start over");
    				updateRoutes(0, 40);
    			} else {
    				updateRoutes(routes.offset, 40);		    
    			}
    		}
    	},function() {
    		console.log("ERROR ABORTING");
    	});
    }
    return {
    	get: function(callback) {
    		getRoutes(0,40);
    	},
    	getR: function() { return this.routes; },
    	renew: function() {
    		aborter = $q.defer();
    		MapService.clear();
//    		resource = createResourceInstance(url, options, aborter);
    	},
    	abort: function() {
    		console.log("ABORTING");
    		aborter.resolve();
    		this.renew();
    	}
    }
}];
var WorkoutService = ['$filter','$timeout','$rootScope','$resource',function($filter,$timeout,$rootScope,$resource) {
    $rootScope.workoutIds = [];
    $rootScope.workouts = [];
    
    function getWorkouts(offset, limit) {
		$resource('/mapmyrun/workouts?userId='+$rootScope.userDetail.userId+'&offset='+offset+'&limit='+limit, {}).get({}, function(workouts) {
		    $rootScope.$broadcast('workouts:get',workouts);
		    if ((workouts.offset * limit) < workouts.total_count) {
		    	getWorkouts(workouts.offset, limit);
		    } else {
		    	console.log("Done from RTE now check MMR");
		    	updateWorkouts(0, 5);
		    }
		});
    };

    function updateWorkouts(offset, limit) {
		$resource('/mapmyrun/updateWorkouts?userId='+$rootScope.userDetail.userId+'&offset='+offset+'&limit='+limit,{}).get({}, function(workouts) {
		    $rootScope.$broadcast('workouts:get',workouts);
		    if (workouts.offset > 0) {
		    	console.log("UPDATE Workout offset is now: " + workouts.offset + " " + workouts.total_count);
				if (workouts.offset > workouts.total_count) {
				    console.log("Missing something from the middle start over");
				    updateWorkouts(0, 40);
				} else {
				    updateWorkouts(workouts.offset, 40);		    
				}
		    }
		});
    }
    return {
		get: function() {
		    $rootScope.workoutIds = [];
		    $rootScope.workouts = [];
		    getWorkouts(0,40);	    
		}
    }
}];
